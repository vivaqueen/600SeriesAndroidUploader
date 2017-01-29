package info.nightscout.android.medtronic.message;

import java.io.IOException;
import java.util.Date;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.UnexpectedMessageException;

/**
 * Created by volker on 29.01.2017.
 */

public class ReadPumpHistoryInfoRequestMessage extends ReadHistoryInfoRequestMessage<ReadHistoryInfoResponseMessage> {
    public ReadPumpHistoryInfoRequestMessage(MedtronicCnlSession pumpSession, Date from, Date to) throws EncryptionException, ChecksumException {
        super(pumpSession, HistoryDataType.PUMP_DATA, from, to);
    }

}
