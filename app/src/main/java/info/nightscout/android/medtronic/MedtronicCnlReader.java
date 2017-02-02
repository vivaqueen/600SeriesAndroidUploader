package info.nightscout.android.medtronic;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import info.nightscout.android.USB.UsbHidDriver;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.InvalidMessageException;
import info.nightscout.android.medtronic.message.ASCIICommandMessage;
import info.nightscout.android.medtronic.message.DeviceInfoRequestCommandMessage;
import info.nightscout.android.medtronic.message.DeviceInfoResponseCommandMessage;
import info.nightscout.android.medtronic.message.contourlink.CloseConnectionRequestMessage;
import info.nightscout.android.medtronic.message.contourlink.OpenConnectionRequestMessage;
import info.nightscout.android.medtronic.message.contourlink.ReadInfoRequestMessage;
import info.nightscout.android.medtronic.message.contourlink.ReadInfoResponseMessage;
import info.nightscout.android.medtronic.message.pump.ChannelNegotiateRequestMessage;
import info.nightscout.android.medtronic.message.pump.ChannelNegotiateResponseMessage;
import info.nightscout.android.medtronic.message.pump.PumpBasalPatternRequestMessage;
import info.nightscout.android.medtronic.message.pump.PumpBasalPatternResponseMessage;
import info.nightscout.android.medtronic.message.pump.PumpStatusRequestMessage;
import info.nightscout.android.medtronic.message.pump.PumpStatusResponseMessage;
import info.nightscout.android.medtronic.message.pump.PumpTimeRequestMessage;
import info.nightscout.android.medtronic.message.pump.PumpTimeResponseMessage;
import info.nightscout.android.medtronic.message.pump.ReadHistoryInfoResponseMessage;
import info.nightscout.android.medtronic.message.pump.ReadPumpHistoryInfoRequestMessage;
import info.nightscout.android.medtronic.message.pump.RequestLinkKeyRequestMessage;
import info.nightscout.android.medtronic.message.pump.RequestLinkKeyResponseMessage;
import info.nightscout.android.medtronic.message.pump.command.BeginHighSpeedModeMessage;
import info.nightscout.android.medtronic.message.pump.command.EndHighSpeedModeMessage;
import info.nightscout.android.model.medtronicNg.PumpStatusEvent;

/**
 * Created by lgoedhart on 24/03/2016.
 */
public class MedtronicCnlReader {
    private static final String TAG = MedtronicCnlReader.class.getSimpleName();

    private static final byte[] RADIO_CHANNELS = {0x14, 0x11, 0x0e, 0x17, 0x1a};
    private UsbHidDriver mDevice;

    private MedtronicCnlSession mPumpSession = new MedtronicCnlSession();
    private String mStickSerial = null;

    public MedtronicCnlReader(UsbHidDriver device) {
        mDevice = device;
    }

    public String getStickSerial() {
        return mStickSerial;
    }

    public MedtronicCnlSession getPumpSession() {
        return mPumpSession;
    }

    public void requestDeviceInfo()
            throws IOException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException {
        DeviceInfoResponseCommandMessage response = new DeviceInfoRequestCommandMessage().send(mDevice);

        //TODO - extract more details form the device info.
        mStickSerial = response.getSerial();
    }

    public void enterControlMode() throws IOException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException {
        boolean doRetry;

        do {
            doRetry = false;
            try {
                new ASCIICommandMessage(ASCIICommandMessage.ASCII.NAK)
                        .send(mDevice, 500).checkControlMessage(ASCIICommandMessage.ASCII.EOT);
                new ASCIICommandMessage(ASCIICommandMessage.ASCII.ENQ)
                        .send(mDevice, 500).checkControlMessage(ASCIICommandMessage.ASCII.ACK);
            } catch (InvalidMessageException e2) {
                try {
                    new ASCIICommandMessage(ASCIICommandMessage.ASCII.EOT).send(mDevice);
                } catch (IOException e) {}
                finally {
                    doRetry = true;
                }
            }
        } while (doRetry);
    }

    public void enterPassthroughMode() throws IOException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException {
        Log.d(TAG, "Begin enterPasshtroughMode");

        new ASCIICommandMessage("W|")
                .send(mDevice, 500).checkControlMessage(ASCIICommandMessage.ASCII.ACK);

        new ASCIICommandMessage("Q|")
                .send(mDevice, 500).checkControlMessage(ASCIICommandMessage.ASCII.ACK);

        new ASCIICommandMessage("1|")
                .send(mDevice, 500).checkControlMessage(ASCIICommandMessage.ASCII.ACK);

        Log.d(TAG, "Finished enterPasshtroughMode");
    }

    public void openConnection() throws IOException, TimeoutException, NoSuchAlgorithmException, EncryptionException, ChecksumException, InvalidMessageException {
        Log.d(TAG, "Begin openConnection");
        new OpenConnectionRequestMessage(mPumpSession, mPumpSession.getHMAC()).send(mDevice);
        Log.d(TAG, "Finished openConnection");
    }

    public void requestReadInfo() throws IOException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException {
        Log.d(TAG, "Begin requestReadInfo");
        ReadInfoResponseMessage response = new ReadInfoRequestMessage(mPumpSession).send(mDevice);

        long linkMAC = response.getLinkMAC();
        long pumpMAC = response.getPumpMAC();

        this.getPumpSession().setLinkMAC(linkMAC);
        this.getPumpSession().setPumpMAC(pumpMAC);
        Log.d(TAG, String.format("Finished requestReadInfo. linkMAC = '%s', pumpMAC = '%s'",
                Long.toHexString(linkMAC), Long.toHexString(pumpMAC)));
    }

    public void requestLinkKey() throws IOException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException {
        Log.d(TAG, "Begin requestLinkKey");

        RequestLinkKeyResponseMessage response = new RequestLinkKeyRequestMessage(mPumpSession).send(mDevice);
        this.getPumpSession().setKey(response.getKey());

        Log.d(TAG, String.format("Finished requestLinkKey. linkKey = '%s'", this.getPumpSession().getKey()));
    }

    public byte negotiateChannel(byte lastRadioChannel) throws IOException, ChecksumException, TimeoutException, EncryptionException {
        ArrayList<Byte> radioChannels = new ArrayList<>(Arrays.asList(ArrayUtils.toObject(RADIO_CHANNELS)));

        if (lastRadioChannel != 0x00) {
            // If we know the last channel that was used, shuffle the negotiation order
            Byte lastChannel = radioChannels.remove(radioChannels.indexOf(lastRadioChannel));

            if (lastChannel != null) {
                radioChannels.add(0, lastChannel);
            }
        }

        Log.d(TAG, "Begin negotiateChannel");
        for (byte channel : radioChannels) {
            Log.d(TAG, String.format("negotiateChannel: trying channel '%d'...", channel));
            mPumpSession.setRadioChannel(channel);
            ChannelNegotiateResponseMessage response = new ChannelNegotiateRequestMessage(mPumpSession).send(mDevice);

            if (response.getRadioChannel() == mPumpSession.getRadioChannel()) {
                break;
            } else {
                mPumpSession.setRadioChannel((byte)0);
            }
        }

        Log.d(TAG, String.format("Finished negotiateChannel with channel '%d'", mPumpSession.getRadioChannel()));
        return mPumpSession.getRadioChannel();
    }

    public void beginEHSMSession() throws EncryptionException, ChecksumException, InvalidMessageException, IOException, TimeoutException {
        Log.d(TAG, "Begin beginEHSMSession");
        new BeginHighSpeedModeMessage(mPumpSession).send(mDevice);
        Log.d(TAG, "Finished beginEHSMSession");
    }

    public Date getPumpTime() throws EncryptionException, ChecksumException, InvalidMessageException, IOException, TimeoutException {
        Log.d(TAG, "Begin getPumpTime");
        // FIXME - throw if not in EHSM mode (add a state machine)

        PumpTimeResponseMessage response = new PumpTimeRequestMessage(mPumpSession).send(mDevice);

        Log.d(TAG, "Finished getPumpTime with date " + response.getPumpTime());
        return response.getPumpTime();
    }

    public PumpStatusEvent updatePumpStatus(PumpStatusEvent pumpRecord) throws IOException, EncryptionException, ChecksumException, InvalidMessageException, TimeoutException {
        Log.d(TAG, "Begin updatePumpStatus");

        // FIXME - throw if not in EHSM mode (add a state machine)
        PumpStatusResponseMessage response = new PumpStatusRequestMessage(mPumpSession).send(mDevice);
        response.updatePumpRecord(pumpRecord);

        Log.d(TAG, "Finished updatePumpStatus");

        return pumpRecord;
    }

    public void getBasalPatterns() throws EncryptionException, ChecksumException, InvalidMessageException, IOException, TimeoutException {
        Log.d(TAG, "Begin getBasalPatterns");
        // FIXME - throw if not in EHSM mode (add a state machine)

        PumpBasalPatternResponseMessage response = new PumpBasalPatternRequestMessage(mPumpSession).send(mDevice);

        Log.d(TAG, "Finished getBasalPatterns");
    }


    public void getHistory(Date from, Date to) throws EncryptionException, ChecksumException, InvalidMessageException, IOException, TimeoutException {
        Log.d(TAG, "Begin getHistory");
        // FIXME - throw if not in EHSM mode (add a state machine)

        ReadHistoryInfoResponseMessage response = new ReadPumpHistoryInfoRequestMessage(mPumpSession, from, to).send(mDevice);
        Log.d(TAG, "number of pump history entries: " + response.getHistorySize() );

        Log.d(TAG, "Finished getHistory");
    }

    public void endEHSMSession() throws EncryptionException, ChecksumException, InvalidMessageException, IOException, TimeoutException {
        Log.d(TAG, "Begin endEHSMSession");
        new EndHighSpeedModeMessage(mPumpSession).send(mDevice);
        Log.d(TAG, "Finished endEHSMSession");
    }

    public void closeConnection() throws IOException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException, NoSuchAlgorithmException {
        Log.d(TAG, "Begin closeConnection");
        new CloseConnectionRequestMessage(mPumpSession, mPumpSession.getHMAC()).send(mDevice);
        Log.d(TAG, "Finished closeConnection");
    }

    public void endPassthroughMode() throws IOException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException {
        Log.d(TAG, "Begin endPassthroughMode");
        new ASCIICommandMessage("W|")
                .send(mDevice, 500).checkControlMessage(ASCIICommandMessage.ASCII.ACK);
        new ASCIICommandMessage("Q|")
                .send(mDevice, 500).checkControlMessage(ASCIICommandMessage.ASCII.ACK);
        new ASCIICommandMessage("0|")
                .send(mDevice, 500).checkControlMessage(ASCIICommandMessage.ASCII.ACK);

        Log.d(TAG, "Finished endPassthroughMode");
    }

    public void endControlMode() throws IOException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException {
        Log.d(TAG, "Begin endControlMode");

        new ASCIICommandMessage(ASCIICommandMessage.ASCII.EOT)
                .send(mDevice, 500).checkControlMessage(ASCIICommandMessage.ASCII.ENQ);

        Log.d(TAG, "Finished endControlMode");
    }
}
