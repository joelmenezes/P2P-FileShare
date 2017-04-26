
/**
 *
 * @author Joel
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChokeMessage extends Message {

    //Message to choke a peer
    public ChokeMessage() throws InterruptedException, IOException {
        this.MsgType = "ChokeMessage";
        this.MsgTypeValue = 0;
        this.MsgLength = 1;

        ByteArrayOutputStream baos = Utilities.getStreamHandle();
        baos.write(Utilities.getBytes(this.MsgLength));
        baos.write((byte) this.MsgTypeValue);
        FullMessage = baos.toByteArray();
        Utilities.returnStreamHandle();
    }

}
