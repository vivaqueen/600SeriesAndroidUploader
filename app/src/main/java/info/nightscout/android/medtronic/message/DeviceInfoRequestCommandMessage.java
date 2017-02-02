package info.nightscout.android.medtronic.message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import info.nightscout.android.USB.UsbHidDriver;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.InvalidMessageException;


/**
 * Created by volker on 10.12.2016.
 */

public class DeviceInfoRequestCommandMessage extends AbstractRequestMessage<DeviceInfoResponseCommandMessage> {
    public DeviceInfoRequestCommandMessage() {
        super("X".getBytes());
    }

    @Override
    public DeviceInfoResponseCommandMessage send(UsbHidDriver mDevice, int millis) throws TimeoutException, EncryptionException, ChecksumException, InvalidMessageException, IOException {
        sendMessage(mDevice);
        sleep(millis);

        byte[] response1 = readMessage(mDevice);
        sleep(millis);
        byte[] response2 = readMessage(mDevice);

        boolean doRetry = false;
        DeviceInfoResponseCommandMessage response = null;

        do {
            try {
                if (ASCII.EOT.equals(response1[0])) {
                    // response 1 is the ASTM message
                    response = this.getResponse(response1);
                    // ugly....
                    response.checkControlMessage(response2, ASCII.ENQ);
                } else {
                    // response 2 is the ASTM message
                    response = this.getResponse(response2);
                    // ugly, too....
                    response.checkControlMessage(response1, ASCII.ENQ);
                }
            } catch (TimeoutException e) {
                doRetry = true;
            }
        } while (doRetry);

        return response;
    }

    @Override
    protected DeviceInfoResponseCommandMessage getResponse(byte[] payload) throws EncryptionException, ChecksumException, InvalidMessageException, IOException, TimeoutException {
        return new DeviceInfoResponseCommandMessage(payload);
    }
}
