
/**
 *
 * @author Joel
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class ClientConnection implements Runnable {

    private DataOutputStream dataOutput;
    private DataInputStream dataInput;
    private String serverAddress;
    private static final int TIMEOUT = 5000;
    private static final int RECEIVE_TIMEOUT = 1000;
    private Socket clientSocket;
    private static final String CONNECTION_NAME = "ClientConnection";
    private int serverPort;
    private PipedOutputStream pipedOutputStream = new PipedOutputStream();
    private Connection myConnection;
    WriteLog w = new WriteLog();

    public ClientConnection(String serverAddress, int serverPort, Connection myConnection) //throws SocketTimeoutException, IOException
    {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        this.clientSocket = new Socket();
        while (true) {
            try {
                this.clientSocket.connect(new InetSocketAddress(this.serverAddress, this.serverPort), TIMEOUT);

                this.dataOutput = new DataOutputStream(clientSocket.getOutputStream());
                this.dataInput = new DataInputStream(clientSocket.getInputStream());
                break;
            } catch (IOException e) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                }
            }
        }
        this.myConnection = myConnection;
        System.out.println("Client: connected to server now");

        try {
            w.TcpConnectionOutgoing(Integer.toString(myConnection.getMyPeerID()), this.serverAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ClientConnection(Socket aSocket, Connection myConnection) throws IOException {

        this.clientSocket = aSocket;
        this.dataOutput = new DataOutputStream(clientSocket.getOutputStream());
        this.dataInput = new DataInputStream(clientSocket.getInputStream());
        this.myConnection = myConnection;
        System.out.println("Client: connected to server now...");
    }

    //Sending data into output stream
    public void send(byte[] data) throws IOException {
        dataOutput.write(data);
        dataOutput.flush();
    }

    private void receive() throws IOException {
        //always read first 4 bytes, then read equivalent to the length indicated by those 4 bytes
        byte[] lengthBuffer = new byte[4];
        dataInput.readFully(lengthBuffer);
        int length = Utilities.getIntFromByte(lengthBuffer, 0);
        pipedOutputStream.write(Utilities.getBytes(length));

        //now read the data indicated by length and write it to buffer
        byte[] buffer = new byte[length];
        dataInput.readFully(buffer);
        pipedOutputStream.write(buffer);
        pipedOutputStream.flush();
        clientBlocker();
    }

    //Receieve handshaking message of length 32
    synchronized void receive(int preknownDataLength) throws EOFException, IOException {
        byte[] buffer = new byte[preknownDataLength];
        dataInput.readFully(buffer);
        pipedOutputStream.write(buffer);
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.receive();
            } catch (InterruptedIOException iioex) {
                Boolean quit = myConnection.getBitMap().canIQuit();
                if (quit == true) {
                    try {
                        System.out.println("Close Connection");
                        Thread.yield();
                        this.closeConnections();

                    } catch (Exception e) {
                    }
                    break;
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    protected void clientBlocker() {
        ArrayList<Integer> one = new ArrayList<Integer>();
        int i = 0;
        while (i < 10) {
            one.add(i);
            i++;
        }

        if (CONNECTION_NAME == "ClientConnection") {
            Collections.sort(one);
        }
    }

    //Close all Connections
    private void closeConnections() throws IOException {
        if (this.pipedOutputStream != null) {
            System.out.println("Close piped Output Stream");
            this.pipedOutputStream.close();
        }
        if (this.dataInput != null) {
            System.out.println("Close Data InputStream");
            this.dataInput.close();
        }
        if (this.dataOutput != null) {
            System.out.println("Close Data OutputStream");
            this.dataOutput.close();
        }
        if (this.clientSocket != null) {
            System.out.println("Close Client Socket");
            this.clientSocket.close();
        }
    }

    public PipedOutputStream getPipedOutputStream() {
        return this.pipedOutputStream;
    }

    public void setSoTimeout() throws SocketException {
        this.clientSocket.setSoTimeout(RECEIVE_TIMEOUT);
    }
}
