package info.nightscout.android.medtronic.message;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.InvalidMessageException;

/**
 * Created by lgoedhart on 26/03/2016.
 */
public abstract class AbstractResponseMessage extends AbstractBaseMessage {

    public AbstractResponseMessage(byte[] payload) throws ChecksumException {
        super(payload);
    }


    public void checkControlMessage(ASCII controlCharacter) throws IOException, TimeoutException, InvalidMessageException {
        checkControlMessage(mPayload.array(), controlCharacter);
    }

    public void checkControlMessage(byte[] msg, ASCII controlCharacter) throws IOException, TimeoutException, InvalidMessageException {
        if (msg.length != 1 || !controlCharacter.equals(msg[0])) {
            throw new InvalidMessageException(String.format(Locale.getDefault(), "Expected to get control character '%d' Got '%d'.",
                    (int) controlCharacter.getValue(), (int) msg[0]));
        }
    }
}
