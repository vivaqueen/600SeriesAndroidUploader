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
public class PumpStatusRequestMessage extends MedtronicSendMessageRequestMessage<PumpStatusResponseMessage> {
    private static final String TAG = PumpStatusRequestMessage.class.getSimpleName();

    public PumpStatusRequestMessage(MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(MessageCommand.READ_PUMP_STATUS_REQUEST, pumpSession, null);
    }

    public PumpStatusResponseMessage send(UsbHidDriver mDevice, int millis) throws TimeoutException, EncryptionException, ChecksumException, InvalidMessageException, IOException {
        sendMessage(mDevice);
        sleep(millis);

        // Read the 0x81
        readMessage(mDevice);
        sleep(millis);

        PumpStatusResponseMessage response = this.getResponse(readMessage(mDevice));

        return response;
    }

    @Override
    protected PumpStatusResponseMessage getResponse(byte[] payload) throws IOException, EncryptionException, ChecksumException, InvalidMessageException {
        return new PumpStatusResponseMessage(mPumpSession, payload);
    }
}
