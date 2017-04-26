
/**
 *
 * @author Joel
 */

import java.io.*;

public class HaveMessage extends Message {

    private static final long serialVersionUID = 9L;

    public HaveMessage(int pieceIndex) throws InterruptedException, IOException {
        this.MsgType = "HaveMessage";
        this.MsgTypeValue = 4;
        this.MsgLength = (Utilities.getBytes(pieceIndex)).length + 1;

        //utilities.getBytes(pieceIndex) is payload for this message
        ByteArrayOutputStream baos = Utilities.getStreamHandle();
        baos.write(Utilities.getBytes(this.MsgLength));
        baos.write((byte) this.MsgTypeValue);
        baos.write(Utilities.getBytes(pieceIndex));
        FullMessage = baos.toByteArray();
        Utilities.returnStreamHandle();
    }
}
