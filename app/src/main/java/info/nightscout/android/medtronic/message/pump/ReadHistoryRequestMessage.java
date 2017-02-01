package info.nightscout.android.medtronic.message.pump;

import java.io.IOException;
import java.util.Date;

import info.nightscout.android.medtronic.MedtronicCnlSession;

import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.UnexpectedMessageException;
import info.nightscout.android.medtronic.message.AbstractBaseMessage.MessageCommand;

/**
 * Created by volker on 29.01.2017.
 */

    public abstract class ReadHistoryRequestMessage<T> extends ReadHistoryBaseRequestMessage<T> {
        private static final String TAG = ReadHistoryRequestMessage.class.getSimpleName();
        private boolean receviedEndHistoryCommand;
        private int bytesFetched;
        private long expectedSize = 0;

        protected ReadHistoryRequestMessage(MedtronicCnlSession pumpSession, HistoryDataType historyDataType, Date from, Date to, long expectedSize) throws EncryptionException, ChecksumException {
            super(pumpSession, historyDataType, from, to);

            this.expectedSize = expectedSize;
            this.bytesFetched = 0;
            this.receviedEndHistoryCommand = false;
        }

        @Override
        protected ReadHistoryResponseMessage getResponse(byte[] payload) throws ChecksumException, EncryptionException, IOException, UnexpectedMessageException, EncryptionException, ChecksumException, UnexpectedMessageException {
            return new ReadHistoryResponseMessage(mPumpSession, payload);
        }


        protected boolean fetchMoreData() {
            return !this.receviedEndHistoryCommand;
        }
    /*
        protected void initSession(payload) {
            this.segmentSize = payload.readUInt32BE(0x03);
            this.packetSize = payload.readUInt16BE(0x07);
            this.lastPacketSize = payload.readUInt16BE(0x09);
            this.packetsToFetch = payload.readUInt16BE(0x0B);
            this.segments = [];
        }*/
    }
