package info.nightscout.android.medtronic.message.pump.command.multipacket;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;

public class InitiateTransferCommand extends AbstractMultipacketCommand {

    public InitiateTransferCommand(MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(SegmentCommand.INITIATE_TRANSFER, pumpSession);
    }
}
