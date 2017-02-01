package info.nightscout.android.medtronic.message.contourlink;

import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.message.AbstractBinaryResponseMessage;

/**
 * Created by lgoedhart on 10/05/2016.
 */
public class OpenConnectionResponseMessage extends AbstractBinaryResponseMessage {
    protected OpenConnectionResponseMessage(byte[] payload) throws ChecksumException, EncryptionException {
        super(payload);
    }

}