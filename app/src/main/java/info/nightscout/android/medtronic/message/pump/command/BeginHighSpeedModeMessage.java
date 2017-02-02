package info.nightscout.android.medtronic.message.pump.command;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;

/**
 * Created by lgoedhart on 26/03/2016.
 */
public class BeginHighSpeedModeMessage extends HighSpeedModeMessage {
    public BeginHighSpeedModeMessage(MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(MessageCommand.HIGH_SPEED_MODE_COMMAND, pumpSession, buildPayload());
    }

    protected static byte[] buildPayload() {
        // Not sure what the payload of a null byte means, but it's the same every time.
        return new byte[] { 0x00 };
    }
}
