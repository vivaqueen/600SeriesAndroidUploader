package info.nightscout.android.medtronic.message.pump.command;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import info.nightscout.android.USB.UsbHidDriver;
import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.UnexpectedMessageException;
import info.nightscout.android.medtronic.message.AbstractResponseMessage;
import info.nightscout.android.medtronic.message.pump.MedtronicSendMessageRequestMessage;

/**
 * Created by volker on 22.12.2016.
 */

public class EHSMMessage extends MedtronicSendMessageRequestMessage<AbstractResponseMessage> {
    protected EHSMMessage(MessageCommand sendMessageType, MedtronicCnlSession pumpSession, byte[] payload) throws EncryptionException, ChecksumException {
        super(sendMessageType, pumpSession, payload);
    }

    @Override
    public AbstractResponseMessage send(UsbHidDriver mDevice, int millis) throws IOException, TimeoutException, UnexpectedMessageException {
        sendMessage(mDevice);
        sleep(millis);

        // The End EHSM Session only has an 0x81 response
        readMessage(mDevice);
        if (this.encode().length != 54) {
            throw new UnexpectedMessageException("length of EHSMMessage response does not match");
        }
        return null;
    }
}
