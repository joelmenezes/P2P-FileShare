package p2p;

/*
    A Class with the getter and setter for properties from the Common.cfg file
*/
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class GetCommon {
    InputStream inputStream;
    private int NumberOfPreferredNeighbors;
    private int UnchokingInterval;
    private int OptimisticUnchokingInterval;
    private String FileName;
    private int FileSize;
    private int PieceSize;

    public String getFileName(){
        return FileName;
    }
    
    public int getNumberOfPreferredNeighbors(){
        return NumberOfPreferredNeighbors;
    }
    
    public int getUnchokingInterval(){
    return UnchokingInterval;
    }
    
    public int getOptimisticUnchokingInterval(){
    return OptimisticUnchokingInterval;
    } 
    
    public int getFileSize(){
    return FileSize;
    }
    
    public int getPieceSize(){
    return PieceSize;
    }

    
    public GetCommon() throws IOException{
        try{
            Properties config = new Properties();
            String configFileName = "Common.cfg";
            inputStream = getClass().getResourceAsStream(configFileName);
            
            if (inputStream != null){
                config.load(inputStream);    
            }
            else{
                throw new FileNotFoundException ("Config file " + configFileName + " not found");
            }
        
            this.FileName = config.getProperty("FileName");
            this.NumberOfPreferredNeighbors = Integer.parseInt(config.getProperty("NumberOfPreferredNeighbors"));
            this.UnchokingInterval = Integer.parseInt(config.getProperty("UnchokingInterval"));
            this.OptimisticUnchokingInterval = Integer.parseInt(config.getProperty("OptimisticUnchokingInterval"));
            this.FileSize = Integer.parseInt(config.getProperty("FileSize"));
            this.PieceSize = Integer.parseInt(config.getProperty("PieceSize"));
           }
        
        catch(Exception e){
            System.out.println("Exception" + e);
        }
        
        finally{
              inputStream.close();
        }
    }
}
   