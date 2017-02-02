package info.nightscout.android.medtronic.message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import info.nightscout.android.USB.UsbHidDriver;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.InvalidMessageException;

/**
 * Created by volker on 12.12.2016.
 */

public abstract class AbstractRequestMessage<T> extends AbstractBaseMessage {
    private static final String TAG = AbstractRequestMessage.class.getSimpleName();

    protected AbstractRequestMessage(byte[] bytes) {
        super(bytes);
    }

    public T send(UsbHidDriver mDevice) throws IOException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException {
        return send(mDevice, 0);
    }

    public T send(UsbHidDriver mDevice, int millis) throws EncryptionException, ChecksumException, InvalidMessageException, TimeoutException, EncryptionException, ChecksumException, InvalidMessageException, IOException {
        sendMessage(mDevice);
        sleep(millis);

        T response = this.getResponse(readMessage(mDevice)); //new CommandResponseMessage();

        // FIXME - We need to care what the response message is - wrong MAC and all that
        return response;
    }

    protected abstract <T> T getResponse(byte[] payload) throws EncryptionException, ChecksumException, InvalidMessageException, IOException, TimeoutException;

}
