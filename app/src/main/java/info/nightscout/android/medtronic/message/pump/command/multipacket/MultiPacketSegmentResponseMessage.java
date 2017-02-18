package info.nightscout.android.medtronic.message.pump.command.multipacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.InvalidMessageException;
import info.nightscout.android.medtronic.message.pump.MedtronicSendMessageResponseMessage;

/**
 * Created by volker on 29.01.2017.
 */


public class MultiPacketSegmentResponseMessage extends MedtronicSendMessageResponseMessage {
    private static final String TAG = MultiPacketSegmentResponseMessage.class.getSimpleName();
    private final int packetNumber;
    private byte[] segmentPayload;

    public MultiPacketSegmentResponseMessage(MedtronicCnlSession pumpSession, byte[] payload) throws EncryptionException, ChecksumException, InvalidMessageException {
        super(pumpSession, payload);

        segmentPayload = this.encode();
        ByteBuffer buffer = ByteBuffer.allocate(segmentPayload.length - 7);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(segmentPayload, 0x05, segmentPayload.length - 7);

        this.packetNumber = buffer.getShort(0x03) & 0x0000ffff;
    }

    public int getPacketNumber() {
        return packetNumber;
    }

    public byte[] getSegmentPayload() {
        return segmentPayload;
    }
}