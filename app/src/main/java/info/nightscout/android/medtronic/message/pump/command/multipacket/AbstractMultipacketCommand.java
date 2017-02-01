package info.nightscout.android.medtronic.message.pump.command.multipacket;

import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.message.pump.MedtronicSendMessageRequestMessage;

/**
 * Created by volkerrichert on 27/03/2016.
 */
public abstract class AbstractMultipacketCommand extends MedtronicSendMessageRequestMessage {
    private static final String TAG = AbstractMultipacketCommand.class.getSimpleName();

    public enum SegmentCommand {
        INITIATE_TRANSFER(0xFF00),
        SEND_NEXT_SEGMENT(0xFF01),
        REPEAT_SEGMENT_PACKETS(0xFF02),

        NO_TYPE(0x0);

        protected short value;

        SegmentCommand(int messageType) {
            value = (short) messageType;
        }

        public short getValue() {
            return value;
        }
        public boolean equals(short value) {
            return this.value == value;
        }

        public static SegmentCommand getSegmentCommand(short value) {
            for(SegmentCommand item : SegmentCommand.values()) {
                if (item.value == value)
                    return item;
            }
            return NO_TYPE;
        }
    }

    protected AbstractMultipacketCommand(SegmentCommand command, MedtronicCnlSession pumpSession) throws EncryptionException, ChecksumException {
        super(MessageCommand.ACK_MULTIPACKET_COMMAND, pumpSession, buildPayload(command));
    }

    private static byte[] buildPayload(SegmentCommand command) {
        return new byte[] {
                (byte) (command.value >> 8 & 0xFF),
                (byte) (command.value & 0xFF)
        };
    }
}
