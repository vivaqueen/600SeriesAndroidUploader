package info.nightscout.android.medtronic.message;

import info.nightscout.android.medtronic.exception.ChecksumException;

/**
 * Created by lgoedhart on 26/03/2016.
 */
public class ASCIICommandMessage extends AbstractRequestMessage<CommandResponseMessage> {
    public ASCIICommandMessage(ASCII command) {
        super(new byte[]{command.getValue()});
    }

    public ASCIICommandMessage(byte command) {
        super(new byte[]{command});
    }

    public ASCIICommandMessage(String command) {
        super(command.getBytes());
    }

    @Override
    protected CommandResponseMessage getResponse(byte[] payload) throws ChecksumException {
        return new CommandResponseMessage(payload);
    }

}
