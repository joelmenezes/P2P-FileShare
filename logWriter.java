package p2p;

/*
    Contains all the methods to write to the logs of individual peers (given by 
    'peer_[peerID]/log_peer_[peerID]'). Has a time stamp associated with each 
    log.
*/
import java.util.*;
import java.io.*;

public class logWriter {
    
    public String getDate() {
       Date date = new Date();
       return (date.toString());
    }
    
    public void initializeLog(String peerID){
        try{
            String fileName = "peer_" + peerID + "/log_peer_" + peerID + ".log";
            File dir = new File ("peer_" + peerID);
            dir.mkdir();
            
            File file = new File(fileName);
            
            if(!file.exists()){
                file.createNewFile();
            }
            
            FileWriter fWriter = new FileWriter(file.getAbsoluteFile(),true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = getDate()+": Peer[" + peerID + "] has initialized its log"; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void TCPIncoming (String peerID1, String peerID2){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = getDate()+": Peer[" + peerID1 + "] makes a connection to"
                    + "Peer[" + peerID2 + "]"; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void prefferedNeighbors (String peerID1, String prefPeerList){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = getDate()+": Peer[" + peerID1 + "] has the preferred neighbors"
                    + prefPeerList; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void optimisticallyUnchokedNeighbor (String peerID1, String peerID2){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = getDate()+": Peer[" + peerID1 + "] has the optimistically"
                    + "unchoked neighbor Peer[" + peerID2 + "]"; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void unchoked (String peerID1, String peerID2){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = getDate()+": Peer[" + peerID1 + "] is unchoked by"
                    + " Peer[" + peerID2 + "]"; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void choked (String peerID1, String peerID2){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = getDate()+": Peer[" + peerID1 + "] is choked by"
                    + " Peer[" + peerID2 + "]"; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void has (String peerID1, String peerID2, int index){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = getDate()+": Peer[" + peerID1 + "] received the 'have' message "
                    + "from Peer[" + peerID2 + "] for the piece [" + index + "]"; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void interested (String peerID1, String peerID2){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = getDate()+": Peer[" + peerID1 + "] received the 'interested' message "
                    + "from Peer[" + peerID2 + "]"; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void notInterested (String peerID1, String peerID2){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = getDate()+": Peer[" + peerID1 + "] received the 'not interested' message "
                    + "from Peer[" + peerID2 + "]"; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void pieceDownloaded (String peerID1, String peerID2, int index, int pieces){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = "]. " + getDate()+": Peer[" + peerID1 + "] has downloaded the" 
                    + " piece ["+ index + "] from Peer[" + peerID2 + "]. "+
                    "Now the number of pieces it has is: "+ pieces; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
    public void completeDownload (String peerID1){
        String fileName = "peer_" + peerID1 + "/log_peer_" + peerID1 + ".log";
        File file = new File(fileName);
        
        if(!file.exists()){
                initializeLog(peerID1);
            }
            
        try{
            FileWriter fWriter = new FileWriter(fileName,true);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            String logMessage;
            logMessage = "]. " + getDate()+": Peer[" + peerID1 + "] has downloaded the" 
                    + " compleete file."; 
            bWriter.write(logMessage);
            bWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    
}
