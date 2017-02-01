package info.nightscout.android.medtronic.message.pump.command.multipacket;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;

public abstract class RepeatSegmentPacketsCommand extends AbstractMultipacketCommand {

    protected RepeatSegmentPacketsCommand(MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(SegmentCommand.REPEAT_SEGMENT_PACKETS, pumpSession);
    }
}
