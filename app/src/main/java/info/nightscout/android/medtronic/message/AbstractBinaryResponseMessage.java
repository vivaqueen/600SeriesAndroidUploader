package info.nightscout.android.medtronic.message;

import info.nightscout.android.medtronic.exception.ChecksumException;

/**
 * Created by lgoedhart on 26/03/2016.
 */
public class AbstractBinaryResponseMessage extends AbstractResponseMessage {

    public AbstractBinaryResponseMessage(byte[] payload) throws ChecksumException {
        super(payload);
    }
}
