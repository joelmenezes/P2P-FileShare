package p2p;

/*
    Class to read a HashMap with the peerID as the key and an object with the 
    three other attributes as the value.
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class getPeerInfo {
    private final String pname= "PeerInfo.cfg";
    InputStream inputStream;
    public Map<Integer, peerConfig> peerInfo () throws FileNotFoundException, IOException{
       
        inputStream = getClass().getResourceAsStream(pname);
            
        BufferedReader reader = null;
        
        reader = new BufferedReader(new InputStreamReader(inputStream));
            
        String st;
        Boolean b = false;
        
        Map<Integer, peerConfig> map = new HashMap<Integer, peerConfig>();
        try{
            while ((st = reader.readLine()) != null){
                String[] tokens = st.split(" ");
                
                if (tokens[3].equals("1")){
                    b = true;
                }
                //System.out.println(" " + tokens[0] + " " + tokens[1]);          //test
                map.put(Integer.parseInt(tokens[0]), new peerConfig(tokens[1], 
                        Integer.parseInt(tokens[2]), b));
            }
            reader.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }
}
