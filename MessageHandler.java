
/**
 *
 * @author Joel
 */
import java.io.*;

public class MessageHandler implements Runnable {

    private int connectedToID = -1;
    private boolean isChoked = true;
    private ClientConnection myClient = null;
    private DataInputStream dis = null;
    private Connection myConnection;
    private BitMap myBitMap;
    private volatile boolean requestSenderStarted = false;
    WriteLog w = new WriteLog();
    private int myID;
    private long start_Download;
    private long stop_Download;

    public MessageHandler(ClientConnection aClient, Connection myConnection) throws IOException {
        this.myConnection = myConnection;
        this.myBitMap = myConnection.getBitMap();
        this.myClient = aClient;
        this.dis = new DataInputStream(new PipedInputStream(aClient.getPipedOutputStream()));
        this.myID = myConnection.getMyPeerID();
    }

    @Override
    public void run() {
        try {
            this.sendHandshake(myID);
            this.processHandshake(this.receiveHandshake());
            myClient.setSoTimeout();
            (new Thread(myClient)).start();
            this.processData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HandshakeMessage receiveHandshake() throws IOException {
        myClient.receive(32);
        byte[] handshakeMsg = new byte[32];
        dis.readFully(handshakeMsg);
        return new HandshakeMessage(handshakeMsg);
    }

    private void sendHandshake(int myPeerID) throws IOException, InterruptedException {
        Message msg = new HandshakeMessage(myPeerID);
        myClient.send(msg.getFullMessage());

    }

    private void processData() throws IOException, InterruptedException {
        while (true) {
            Message msg = getNextMessage();
            //now interpret the message and take action

            int payloadLength = msg.getMsgLength() - 1;
            int msgType = msg.getMsgTypeValue();

            if (msgType == 0) {
                processChokeMessage();
            } else if (msgType == 1) {
                processUnchokeMessage();
            } else if (msgType == 2) {
                processInterestedMessage();
            } else if (msgType == 3) {
                processNotInterestedMessage();
            } else if (msgType == 4) {
                processHaveMessage(payloadLength);
            } else if (msgType == 5) {
                processBitfieldMessage(payloadLength);
            } else if (msgType == 6) {
                processRequestMessage();
            } else if (msgType == 7) {
                processPieceMessage(payloadLength);
            } else {
                System.out.println("Undefined Messae Type");
            }

        }
    }

    //Processes message if type = 5
    private void processBitfieldMessage(int msgLength) throws IOException, InterruptedException {
        byte[] BitMap = new byte[msgLength];
        dis.readFully(BitMap);
        myBitMap.setPeerBitMap(connectedToID, BitMap);
        if (myBitMap.hasInterestingPiece(connectedToID)) {
            InterestedMessage i = new InterestedMessage();
            myClient.send(i.getFullMessage());
        } else {
            myClient.send((new NotInterestedMessage()).getFullMessage());
        }
    }

    private void processPieceMessage(int msgLength) throws IOException, InterruptedException {
        byte[] pieceBuffer = new byte[4];
        dis.readFully(pieceBuffer);
        int pieceIndex = Utilities.getIntFromByte(pieceBuffer, 0);
        byte[] pieceData = new byte[msgLength - 4];
        dis.readFully(pieceData);
        stop_Download = System.currentTimeMillis();
        myConnection.addOrUpdatedownloadrate_peer(connectedToID, (stop_Download - start_Download));
        myBitMap.reportPieceReceived(pieceIndex, pieceData);
        w.PieceDownload(Integer.toString(myID), Integer.toString(connectedToID), pieceIndex, myBitMap.getDownloadedPieceCount(myID));
        int myCount = myBitMap.getDownloadedPieceCount(myID);
        int totalCount = myBitMap.getTotalPieceCount();
        if (myCount == totalCount) {
            w.DownloadComplete(Integer.toString(myID));
            myConnection.sendGroupMessage(myConnection.getconnectedPeersList(), new NotInterestedMessage().getFullMessage());
        }

        if (!isChoked) {
            int desiredPiece = myBitMap.getPeerPieceIndex(connectedToID);
            if (desiredPiece != -1) {
                myClient.send((new RequestMessage(desiredPiece)).getFullMessage());
            }
        }

        myConnection.sendGroupMessage(myConnection.getconnectedPeersList(), (new HaveMessage(pieceIndex)).getFullMessage());
        myConnection.sendGroupMessage(myConnection.computeAndGetWastePeersList(), new NotInterestedMessage().getFullMessage());
    }

    //Request for piece using piece ID
    private void processRequestMessage() throws IOException, InterruptedException {
        byte[] indexBuffer = new byte[4];
        dis.readFully(indexBuffer);
        int pieceIndex = Utilities.getIntFromByte(indexBuffer, 0);
        byte[] dataForPiece = myBitMap.getPieceData(pieceIndex);
        myClient.send((new PieceMessage(pieceIndex, dataForPiece)).getFullMessage());
    }

    private void processHaveMessage(int msgLength) throws IOException, InterruptedException {
        byte[] payload = new byte[msgLength];
        dis.readFully(payload);
        int pieceIndex = Utilities.getIntFromByte(payload, 0);
        w.Have(Integer.toString(myID), Integer.toString(connectedToID), pieceIndex);

        myBitMap.reportPeerPieceAvailablity(connectedToID, pieceIndex);
        if (!myBitMap.doIHavePiece(pieceIndex)) {
            myConnection.reportInterestedPeer(connectedToID);
            myClient.send((new InterestedMessage()).getFullMessage());
        }
    }

    private void processNotInterestedMessage() {

        w.NotInterested(Integer.toString(myID), Integer.toString(connectedToID));
        myConnection.reportNotInterestedPeer(connectedToID);
    }

    private void processInterestedMessage() {
        w.Interested(Integer.toString(myID), Integer.toString(connectedToID));
        myConnection.reportInterestedPeer(connectedToID);
    }

    private void processUnchokeMessage() throws IOException, InterruptedException {
        w.Unchoked(Integer.toString(myID), Integer.toString(connectedToID));
        this.isChoked = false;

        if (!requestSenderStarted) {
            (new Thread(new RequestMessageProcessor())).start();
            this.requestSenderStarted = true;
        }
    }

    private void processChokeMessage() {
        w.Choked(Integer.toString(myID), Integer.toString(connectedToID));
        this.isChoked = true;
        myConnection.resetdownloadrate_peer(connectedToID);
    }

    private void processHandshake(HandshakeMessage handshakeMsg) throws IOException, InterruptedException {
        byte[] msgBytes = handshakeMsg.getFullMessage();
        byte[] msgHeader = new byte[18];
        System.arraycopy(msgBytes, 0, msgHeader, 0, 18);
        this.connectedToID = handshakeMsg.getPeerID();
        this.myConnection.reportNewClientConnection(this.connectedToID, myClient);
        w.ReceivedHandshake(myID, connectedToID);
        if (myBitMap == null) {
            System.out.println("My file is NULL");
        }
        if (myBitMap.doIHaveAnyPiece()) {
            myClient.send(new BitfieldMessage(myBitMap.getMyFileBitMap()).getFullMessage());
        }
    }

    private Message getNextMessage() throws IOException, InterruptedException {
        byte[] lengthBuffer = new byte[4];
        try {
            dis.readFully(lengthBuffer);
        } catch (Exception e) {
            // System.out.println("Connection closed");
        }
        int msgLength = Utilities.getIntFromByte(lengthBuffer, 0);
        byte[] msgType = new byte[1];

        try {
            dis.readFully(msgType);
        } catch (Exception e) {
        }
        Message m = new Message();
        m.setMsgLength(msgLength);
        int mType = msgType[0];
        
        if (mType == 0){
        m.setMsgTypeValue(0);
                m.setMsgType("ChokeMessage");
                
        }
        else if (mType == 1){
        m.setMsgTypeValue(1);
                m.setMsgType("UnchokedMessage");
                
        }
        else if (mType == 2){
                m.setMsgTypeValue(2);
                m.setMsgType("InterestedMessage");
        }
        else if (mType == 3){    
            m.setMsgTypeValue(3);
                m.setMsgType("NotInterestedMessage");
        }
        else if (mType == 4){
            m.setMsgTypeValue(4);
                m.setMsgType("HaveMessage");
            }
        else if (mType == 5){
        m.setMsgTypeValue(5);
                m.setMsgType("BitfieldMessage");
                }
        else if (mType == 6){
        m.setMsgTypeValue(6);
                m.setMsgType("RequestMessage");
                }
        else if (mType == 7){m.setMsgTypeValue(7);
                m.setMsgType("PieceMessage");
                }
        else{
            System.out.println("Undefined Message");
        }
        

        return m;
    }

    class RequestMessageProcessor implements Runnable {

        private void sendRequestMessage() throws IOException, InterruptedException {
            int desiredPiece = myBitMap.getPeerPieceIndex(connectedToID);
            if (desiredPiece != -1) {
                myClient.send((new RequestMessage(desiredPiece)).getFullMessage());
            }
        }

        @Override
        public void run() {
            while (!myBitMap.canIQuit()) {
                try {
                    this.sendRequestMessage();
                    Thread.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
