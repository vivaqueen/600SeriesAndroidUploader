package info.nightscout.android.medtronic.message.pump;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import info.nightscout.android.USB.UsbHidDriver;
import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.InvalidMessageException;

/**
 * Created by lgoedhart on 26/03/2016.
 */
public class PumpTimeRequestMessage extends MedtronicSendMessageRequestMessage<PumpTimeResponseMessage> {
    public PumpTimeRequestMessage(MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(MessageCommand.TIME_REQUEST, pumpSession, null);
    }

    @Override
    public PumpTimeResponseMessage send(UsbHidDriver mDevice, int millis) throws TimeoutException, EncryptionException, ChecksumException, InvalidMessageException, IOException {
        sendMessage(mDevice);
        sleep(millis);

        // Read the 0x81
        readMessage(mDevice);
        sleep(millis);

        // Read the 0x80
        PumpTimeResponseMessage response = this.getResponse(readMessage(mDevice));

        return response;
    }

    @Override
    protected PumpTimeResponseMessage getResponse(byte[] payload) throws ChecksumException, EncryptionException, IOException, InvalidMessageException {
        return new PumpTimeResponseMessage(mPumpSession, payload);
    }
}
