package p2p;

/*
    A class to create an object of the hostName, listeningPort and hasFile 
    corresponding to a given peerID. Contains both the getter and setter methods.
*/
public class peerConfig {
    private String hostName;
    private int listeningPort;
    private Boolean hasFile;
    
    peerConfig(String token) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public peerConfig(String hostName, int listeningPort, Boolean hasFile){
        this.hostName = hostName;
        this.listeningPort = listeningPort;
        this.hasFile = hasFile;
    }
    
    public String getHostName(){
	return hostName;
    }

    public int getListeningPort(){
        return listeningPort;
    }

    public boolean hasFile(){
	return hasFile;
    }

    protected void setHostName(String hostName){
	this.hostName = hostName;
    }

    protected void setListeningPort(int listeningPort){
        this.listeningPort = listeningPort;
    }

    protected void setHasFile(boolean hasFile){
	this.hasFile = hasFile;
    }    
}
