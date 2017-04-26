
/**
 *
 * @author Joel
 */

import java.io.*;

public class HandshakeMessage extends Message {

    final String HANDSHAKE_MSG_HEADER = "P2PFILESHARINGPROJ";
    int peerID;

    public int getPeerID() {
        return peerID;
    }

    public HandshakeMessage(int peerid) throws InterruptedException, IOException {
        ByteArrayOutputStream baos = Utilities.getStreamHandle();
        baos.write(HANDSHAKE_MSG_HEADER.getBytes());
        baos.write(new byte[10]);  //10 bytes zero bits
        this.peerID = peerid;
        baos.write(Utilities.getBytes(peerID));
        this.FullMessage = baos.toByteArray();
        Utilities.returnStreamHandle();
    }

    
    public HandshakeMessage(byte[] HandShakeMsg) {
        this.FullMessage = HandShakeMsg;
        this.peerID = Utilities.getIntFromByte(FullMessage, 28);
    }
}
