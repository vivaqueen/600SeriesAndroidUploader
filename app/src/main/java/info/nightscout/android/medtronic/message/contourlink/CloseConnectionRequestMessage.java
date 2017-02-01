package info.nightscout.android.medtronic.message.contourlink;

import java.io.IOException;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.message.AbstractBinaryRequestMessage;

/**
 * Created by volker on 10.12.2016.
 */

public class CloseConnectionRequestMessage extends AbstractBinaryRequestMessage<CloseConnectionResponseMessage> {
    public CloseConnectionRequestMessage(MedtronicCnlSession pumpSession, byte[] payload) throws ChecksumException {
        super(CommandType.CLOSE_CONNECTION, pumpSession, payload);
    }

    @Override
    protected CloseConnectionResponseMessage getResponse(byte[] payload) throws ChecksumException, EncryptionException, IOException {
        return new CloseConnectionResponseMessage(payload);
    }
}
