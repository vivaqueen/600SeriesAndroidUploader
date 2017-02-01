package info.nightscout.android.medtronic.message.pump.command.multipacket;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;

public class SendNextSegmentCommand extends AbstractMultipacketCommand {

    protected SendNextSegmentCommand(MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(SegmentCommand.SEND_NEXT_SEGMENT, pumpSession);
    }
}
