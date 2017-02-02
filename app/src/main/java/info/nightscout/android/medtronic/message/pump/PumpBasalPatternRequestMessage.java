package info.nightscout.android.medtronic.message.pump;

import java.io.IOException;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.InvalidMessageException;

/**
 * Created by lgoedhart on 26/03/2016.
 */
public class PumpBasalPatternRequestMessage extends MedtronicSendMessageRequestMessage<PumpBasalPatternResponseMessage> {
    public PumpBasalPatternRequestMessage(MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(MessageCommand.READ_BASAL_PATTERN_REQUEST, pumpSession, null);
    }

    @Override
    protected PumpBasalPatternResponseMessage getResponse(byte[] payload) throws ChecksumException, EncryptionException, IOException, InvalidMessageException {
        return new PumpBasalPatternResponseMessage(mPumpSession, payload);
    }
}
