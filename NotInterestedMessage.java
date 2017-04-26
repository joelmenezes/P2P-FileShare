
/**
 *
 * @author Joel
 */

import java.io.*;

public class NotInterestedMessage extends Message {

    public NotInterestedMessage() throws InterruptedException, IOException {
        this.MsgType = "NotInterestedMessage";
        this.MsgTypeValue = 3;
        this.MsgLength = 1;

        ByteArrayOutputStream baos = Utilities.getStreamHandle();
        baos.write(Utilities.getBytes(this.MsgLength));
        baos.write((byte) this.MsgTypeValue);
        FullMessage = baos.toByteArray();
        Utilities.returnStreamHandle();
    }

}
