package info.nightscout.android.medtronic.message.pump;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import info.nightscout.android.USB.UsbHidDriver;
import info.nightscout.android.medtronic.MedtronicCnlSession;
import info.nightscout.android.medtronic.exception.ChecksumException;
import info.nightscout.android.medtronic.exception.EncryptionException;
import info.nightscout.android.medtronic.exception.UnexpectedMessageException;

import info.nightscout.android.medtronic.message.pump.command.multipacket.MultiPacketSegmentResponseMessage;
import info.nightscout.android.medtronic.message.pump.command.multipacket.InitiateTransferCommand;

import static info.nightscout.android.medtronic.message.AbstractBaseMessage.MessageCommand.HIGH_SPEED_MODE_COMMAND;
import static info.nightscout.android.medtronic.message.AbstractBaseMessage.MessageCommand.INITIATE_MULTIPACKET_TRANSFER;
import static info.nightscout.android.medtronic.message.AbstractBaseMessage.MessageCommand.MULTIPACKET_SEGMENT_TRANSMISSION;

/**
 * Created by lgoedhart on 26/03/2016.
 */
public abstract class ReadHistoryBaseRequestMessage<T> extends MedtronicSendMessageRequestMessage<T> {

    protected enum HistoryDataType {
        PUMP_DATA(0x2),
        SENSOR_DATA(0x2);

        protected byte value;

        HistoryDataType(int messageType) {
            value = (byte) messageType;
        }
    }


    private boolean receviedEndHistoryCommand;
    private int bytesFetched;
    private long expectedSize = 0;

    protected ReadHistoryBaseRequestMessage(MedtronicCnlSession pumpSession, HistoryDataType historyDataType, Date from, Date to) throws EncryptionException, ChecksumException {
        super(MessageCommand.READ_HISTORY_REQUEST, pumpSession, buildPayload(historyDataType, from, to));

        this.expectedSize = expectedSize;
        this.bytesFetched = 0;
        this.receviedEndHistoryCommand = false;
    }

    @Override
    protected ReadHistoryResponseMessage getResponse(byte[] payload) throws ChecksumException, EncryptionException, IOException, UnexpectedMessageException {
        return new ReadHistoryResponseMessage(mPumpSession, payload);
    }

    @Override
    public T send(UsbHidDriver mDevice, int sendDelay) throws UnexpectedMessageException, EncryptionException, TimeoutException, ChecksumException, IOException {
        sendMessage(mDevice);
        sleep(sendDelay);

        while (fetchMoreData()) {
            MultiPacketSegmentResponseMessage multiPacketSegmentResponseMessage = new MultiPacketSegmentResponseMessage(mPumpSession, readMessage(mDevice));
            switch (multiPacketSegmentResponseMessage.getComDCommand()) {
                case HIGH_SPEED_MODE_COMMAND:
                    //callback(null, null);
                    break;
                case INITIATE_MULTIPACKET_TRANSFER:
                    //this.initSession(response.decryptedPayload);
                    // Acknowledge that we're ready to start receiving data.
                    new InitiateTransferCommand(this.mPumpSession).send(mDevice);
                    break;
                case MULTIPACKET_SEGMENT_TRANSMISSION:
                    break;
                case END_HISTORY_TRANSMISSION:
                    this.receviedEndHistoryCommand = true;

                    // Check that we received as much data as we were expecting.
                    if (this.bytesFetched < this.expectedSize) {
                        //callback(new InvalidMessageError('Got less data than expected'), null);
                    } else {
                        // We need to read another HIGH_SPEED_MODE_COMMAND off the stack.
                        this.readMessage(mDevice);
                        //.then(() => callback(null, response));
                    }
                    break;
                default:
                    break;
            }

        }
        return null;
    }

    protected boolean fetchMoreData() {
        return !this.receviedEndHistoryCommand;
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
