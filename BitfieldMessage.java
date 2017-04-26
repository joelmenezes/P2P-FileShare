
/**
 *
 * @author Joel
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//Sent when handshaking is done. Contains information about pieces available with this peer
public class BitfieldMessage extends Message {

    public BitfieldMessage(byte[] payloadBitmap) throws IOException, InterruptedException {
        this.MsgType = "Bitfield Message";
        this.MsgTypeValue = 5;
        this.MsgLength = payloadBitmap.length + 1;

        ByteArrayOutputStream baos = Utilities.getStreamHandle();
        baos.write(Utilities.getBytes(this.MsgLength));
        baos.write((byte) this.MsgTypeValue);
        baos.write(payloadBitmap);
        FullMessage = baos.toByteArray();
        Utilities.returnStreamHandle();
    }
}
