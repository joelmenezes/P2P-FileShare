
/**
 *
 * @author Joel
 */
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class BitMap {

    private final String fileName;

    private Map<Integer, byte[]> peerIDtoPiece;
    private Map<Integer, AtomicInteger> peerIDtoCountDownload;
    private Set<Integer> piecesAskedfor;
    private FileIO myFileHandler;
    private final String myDirectory;
    private final int myPeerID;
    private final int totalPiecesRequired;
    private byte[] fullBitMap;
    private Connection myConnection;

    public BitMap(int myPeerID, configProperties myCommonConfig, Set<Integer> peerConfigIDs,
            boolean doIHaveFile, Connection myConnection, Map<Integer, PeerConfig> map) throws IOException {

        this.myConnection = myConnection;
        this.myPeerID = myPeerID;
        this.fileName = myCommonConfig.getFileName();
        this.peerIDtoPiece = new ConcurrentHashMap<Integer, byte[]>();
        this.peerIDtoCountDownload = new ConcurrentHashMap<Integer, AtomicInteger>();
        this.piecesAskedfor = new ConcurrentSkipListSet<Integer>();
        this.totalPiecesRequired = (int) Math.ceil((double) myCommonConfig.getFileSize() / myCommonConfig.getPieceSize());
        int totalBytesRequiredForPieces = (int) Math.ceil((double) totalPiecesRequired / 8);

        //initialize maps with all peerIDs 
        for (Integer PeerID : peerConfigIDs) {
            this.peerIDtoPiece.put(PeerID, new byte[totalBytesRequiredForPieces]);
            this.peerIDtoCountDownload.put(PeerID, new AtomicInteger(0));

        }
        this.myDirectory = System.getProperty("user.dir") + "/peer_" + myPeerID;

        this.myFileHandler = new FileIO(myDirectory + "/" + myCommonConfig.getFileName(),
                myCommonConfig.getFileSize(), myCommonConfig.getPieceSize());

        //create the BitMap
        this.fullBitMap = getfullBitMap(totalPiecesRequired);

        //create a dummy file
        if (doIHaveFile == true) {

            System.out.println(myDirectory);

            File tempFile = new File(myDirectory + "/" + myCommonConfig.getFileName());
            if (tempFile.exists() == false) {
                System.out.println("File Name:: " + myCommonConfig.getFileName());
                System.out.println("File Does Not Exist");
                System.exit(0);
            }

            // check if file size matches the file-size specified in Common.cfg
            int configFileSize = myCommonConfig.getFileSize();
            if (tempFile.length() != configFileSize) {
                System.out.println("File Size Incorrect");
                System.exit(0);
            }

            this.peerIDtoPiece.put(myPeerID, this.fullBitMap);
        } else {
            this.myFileHandler.createDummyFile();

        }
        System.out.println("BitMap created");
    }

    private byte[] getfullBitMap(int totalPiecesRequired) {
        //add 1 to all of bits in fullBitMap
        int len = (int) Math.ceil((double) totalPiecesRequired / 8);
        byte[] fullBitMap = new byte[len];
        int a = 0;
        while (a < len) {
            fullBitMap[a] = (byte) 0xFF;
            a++;
        }
        int lastBytePieces = totalPiecesRequired & 7;
        if (lastBytePieces > 0) {
            fullBitMap[len - 1] = (byte) (fullBitMap[len - 1] & 0xFF >>> (8 - lastBytePieces));
        }
        return fullBitMap;
    }

    //Update piece map with ID of piece received and update on disk
    public void reportPieceReceived(int pieceID, byte[] pieceData) throws IOException {
        myFileHandler.writeFilePiece(pieceID, pieceData);
        synchronized (this) {
            byte[] myFileBitMap = this.peerIDtoPiece.get(myPeerID);
            this.updateBitMapWithPiece(myFileBitMap, pieceID);
            this.peerIDtoCountDownload.get(myPeerID).addAndGet(1);
        }
        Boolean quit = canIQuit();
        if (quit == true) {
            this.myConnection.QuitProcess();
        }
    }

    //Piece Data getter and setter
    public byte[] getPieceData(int pieceIndex) throws IOException {
        return myFileHandler.getChunk(pieceIndex);
    }

    public byte[] getMyFileBitMap() {
        return this.peerIDtoPiece.get(myPeerID);
    }

    public int getTotalPieceCount() {
        return totalPiecesRequired;
    }

    //Getter Setter for count of pieces
    public int getDownloadedPieceCount(int peerID) {
        return this.peerIDtoCountDownload.get(peerID).get();
    }

    public byte[] getPeerBitMap(int peerID) {
        return this.peerIDtoPiece.get(peerID);
    }

    public synchronized void setPeerBitMap(int peerID, byte[] BitMap) {
        this.peerIDtoPiece.put(peerID, BitMap);
    }

    public String getFileName() {
        return fileName;
    }

    //Updates bitmap and piece count
    public void reportPeerPieceAvailablity(int peerID, int pieceIndex) {
        synchronized (this) {
            byte[] peerFileBitMap = this.peerIDtoPiece.get(peerID);
            updateBitMapWithPiece(peerFileBitMap, pieceIndex);
            this.peerIDtoCountDownload.get(peerID).addAndGet(1);
        }
        Boolean quit = canIQuit();
        if (quit == true) {
            this.myConnection.QuitProcess();
        }
    }

    //Check if whole file downloaded
    public boolean canIQuit() {

        for (byte[] aBitMap : this.peerIDtoPiece.values()) {
            Boolean isFinal = isBitMapFinal(aBitMap);
            if (isFinal == false) {
                return false;
            }
        }
        return true;

    }

    private boolean isBitMapFinal(byte[] BitMap) {
        int len = BitMap.length;
        int i = 0;
        while (i < len) {
            if ((BitMap[i] ^ fullBitMap[i]) != 0) {
                return false;
            }
            i++;
        }

        return true;
    }

    public boolean doIHavePiece(int pieceIndex) {
        byte[] myFileBitMap = this.peerIDtoPiece.get(myPeerID);
        int pieceLocation = pieceIndex / 8;
        int bitLocation = pieceIndex & 7;   // = pieceIndex % 8
        if ((myFileBitMap[pieceLocation] & (1 << bitLocation)) != 0) // == 0 means we don't have that piece
        {
            return true;
        }
        return false;
    }

    private void updateBitMapWithPiece(byte[] peerFileBitMap, int pieceIndex) {
        int pieceLocation = pieceIndex / 8;
        int bitLocation = pieceIndex & 7;   // = pieceIndex % 8
        peerFileBitMap[pieceLocation] |= (1 << bitLocation);
    }

    public boolean doIHaveAnyPiece() {
        byte[] myFileBitMap = this.peerIDtoPiece.get(myPeerID);
        final int mask = 0x000000FF;
        final int len = myFileBitMap.length;
        int i = 0;
        while (i < len) {
            if ((myFileBitMap[i] & mask) != 0) {
                return true;
            }
            i++;
        }

        return false;
    }

    //Check if the peer has interested piece
    public boolean hasInterestingPiece(int anotherPeerID) {
        byte[] myFileBitMap = this.peerIDtoPiece.get(myPeerID);
        final int len = myFileBitMap.length;
        byte[] peerFileBitMap = this.peerIDtoPiece.get(anotherPeerID);
        int i = 0;
        while (i < len) {
            if ((0xFF & (int) (myFileBitMap[i] | peerFileBitMap[i])) > (0xFF & (int) myFileBitMap[i])) {
                return true;
            }
            i++;
        }
        return false;
    }

    //Get random piece not with this peer, but with connected peer
    public int getPeerPieceIndex(int peerID) {
        byte[] myFileBitMap = this.peerIDtoPiece.get(myPeerID);
        int desiredPieceID = -1;
        //check the first available piece which is not requested, if found add to requested piece list
        byte[] peerBitMap = this.peerIDtoPiece.get(peerID);
        final int len = myFileBitMap.length;
        List<Integer> possiblePieces = new ArrayList<Integer>();

        for (int i = 0; i < len; i++) {
            if ((0xFF & (int) (myFileBitMap[i] | peerBitMap[i])) > (0xFF & (int) myFileBitMap[i])) {
                for (int j = 0; j < 8; j++) {
                    //if peer has the piece and I don't have it, request it
                    if ((myFileBitMap[i] & (1 << j)) == 0 && (peerBitMap[i] & (1 << j)) != 0) {
                        int attemptedPieceIndex = i * 8 + j;
                        desiredPieceID = findAndLogRequestedPiece(attemptedPieceIndex);
                        if (desiredPieceID != -1) {
                            possiblePieces.add(desiredPieceID);
                        }
                    }
                }
            }
        }
        int pieceSizep = possiblePieces.size();
        if (pieceSizep != 0) {
            Random rand = new Random();
            int idx = rand.nextInt(possiblePieces.size());

            // access that element from the possiblePieces list and return
            int pieceIndex = possiblePieces.get(idx);
            this.piecesAskedfor.add(pieceIndex);
            return pieceIndex;
        }
        return -1;
    }

    private synchronized int findAndLogRequestedPiece(int pieceIndex) {
        if (this.piecesAskedfor.contains(pieceIndex)) {
            return -1;
        }
        return pieceIndex;
    }

}
