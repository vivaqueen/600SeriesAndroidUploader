package info.nightscout.android.medtronic.message.pump.command.multipacket;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.InvalidMessageException;
import info.nightscout.android.medtronic.message.AbstractResponseMessage;

public class SendNextSegmentCommand extends AbstractMultipacketCommand {

    protected SendNextSegmentCommand(MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(SegmentCommand.SEND_NEXT_SEGMENT, pumpSession);
    }

    @Override
    protected AbstractResponseMessage getResponse(byte[] payload) throws EncryptionException, ChecksumException, InvalidMessageException, IOException, TimeoutException {
        return null;
    }
}
