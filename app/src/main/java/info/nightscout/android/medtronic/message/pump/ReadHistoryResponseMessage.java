package info.nightscout.android.medtronic.message.pump;

/**
 * Created by volker on 29.01.2017.
 */


import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.UnexpectedMessageException;
import info.nightscout.android.medtronic.message.MessageUtils;

/**
 * Created by lgoedhart on 27/03/2016.
 */
public class ReadHistoryResponseMessage extends MedtronicSendMessageResponseMessage {
    private static final String TAG = ReadHistoryResponseMessage.class.getSimpleName();
    private long historySize = 0;
    private Date startDate;
    private Date endDate;

    protected ReadHistoryResponseMessage(MedtronicCnlSession pumpSession, byte[] payload) throws EncryptionException, ChecksumException, UnexpectedMessageException {
        super(pumpSession, payload);

        long rtc, offset;
        if (this.encode().length < 0x18) {
            // Invalid message.
            // TODO - deal with this more elegantly
            Log.e(TAG, "Invalid message received for ReadHistoryInfo");
            throw new UnexpectedMessageException("Invalid message received for ReadHistoryInfo");
        } else {

            ByteBuffer readHistoryInfoBuffer = ByteBuffer.allocate(payload.length);
            readHistoryInfoBuffer.order(ByteOrder.BIG_ENDIAN);
            readHistoryInfoBuffer.put(this.encode());

            this.historySize = (readHistoryInfoBuffer.getLong(0x04) & 0x00000000ffffffffL);

            rtc = readHistoryInfoBuffer.getInt(0x08) & 0x00000000ffffffffL;
            offset = readHistoryInfoBuffer.getInt(0x0c) - 0x100000000L;
            startDate = MessageUtils.decodeDateTime(rtc, offset);

            rtc = readHistoryInfoBuffer.getInt(0x10) & 0x00000000ffffffffL;
            offset = readHistoryInfoBuffer.getInt(0x14) - 0x100000000L;
            endDate = MessageUtils.decodeDateTime(rtc, offset);
        }
    }

    public long getHistorySize() {
        return historySize;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}