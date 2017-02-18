package info.nightscout.android.medtronic.message.pump.command.multipacket;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.InvalidMessageException;
import info.nightscout.android.medtronic.message.AbstractResponseMessage;

public class RepeatSegmentPacketsCommand extends AbstractMultipacketCommand {

    public RepeatSegmentPacketsCommand(MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(SegmentCommand.REPEAT_SEGMENT_PACKETS, pumpSession);
    }

    @Override
    protected AbstractResponseMessage getResponse(byte[] payload) throws EncryptionException, ChecksumException, InvalidMessageException, IOException, TimeoutException {
        return null;
    }
}
