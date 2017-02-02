package info.nightscout.android.medtronic.message.pump;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;

/**
 * Created by lgoedhart on 26/03/2016.
 */
public abstract class ReadHistoryInfoRequestMessage<T> extends MedtronicSendMessageRequestMessage<T> {

    protected enum HistoryDataType {
        PUMP_DATA(0x2),
        SENSOR_DATA(0x2);

        private byte value;

        HistoryDataType(int messageType) {
            value = (byte) messageType;
        }
    }

    protected ReadHistoryInfoRequestMessage(MedtronicCnlSession pumpSession, HistoryDataType historyDataType, Date from, Date to) throws EncryptionException, ChecksumException {
        super(MessageCommand.READ_HISTORY_INFO_REQUEST, pumpSession, buildPayload(historyDataType, from, to));
    }


    protected static byte[] buildPayload(HistoryDataType type, Date from, Date to) {
        ByteBuffer payloadBuffer = ByteBuffer.allocate(12);
        payloadBuffer.order(ByteOrder.BIG_ENDIAN);

        payloadBuffer.put(type.value);
        payloadBuffer.put((byte) 0x04);
        payloadBuffer.putLong(from.getTime());
        payloadBuffer.putLong(to.getTime());
        payloadBuffer.putShort((short) 0x00);

        return payloadBuffer.array();
    }
}
