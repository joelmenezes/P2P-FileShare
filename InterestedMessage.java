
/**
 *
 * @author Joel
 */
import java.io.*;

public class InterestedMessage extends Message {

    public InterestedMessage() throws InterruptedException, IOException {
        this.MsgType = "InterestedMessage";
        this.MsgTypeValue = 2;
        this.MsgLength = 1;

        ByteArrayOutputStream baos = Utilities.getStreamHandle();
        baos.write(Utilities.getBytes(this.MsgLength));
        baos.write((byte) this.MsgTypeValue);
        FullMessage = baos.toByteArray();
        Utilities.returnStreamHandle();
    }
}
