/**
 *
 * @author Joel
 */

import java.io.IOException;
import java.util.*;
import java.io.*;


public class PeerProcess
{

	// Reads config file with common properties
	public configProperties getCommon(String fname)
	{
		String st;
		String apps[];
		configProperties x=null;
		Vector<String> lines = new Vector<String>();

		try 
		{
			BufferedReader in = new BufferedReader(new FileReader(fname));
			while((st = in.readLine()) != null)
			{
				lines.addElement(st);
			}
			in.close();
			apps = new String[lines.size()];
			String common[][] = new String[apps.length][2];
			for (int i = 0; i < apps.length; i++) 
			{
				apps[i] = (String) lines.elementAt(i);
				String[] tokens = apps[i].split("\\s+");
				common[i][0] = tokens[0];
				common[i][1] = tokens[1];
			}

			x = new configProperties(Integer.parseInt(common[0][1]), Integer.parseInt(common[1][1]),
					Integer.parseInt(common[2][1]), common[3][1], Integer.parseInt(common[4][1]),
					Integer.parseInt(common[5][1]));

		}
		catch (Exception ex) 
		{
			System.out.println(ex.toString());
		}
		return x;

	}


	// Creates a map using the PeerInfo.cfg file with relevant properties like hostname,
        // port, hasFile
	public Map<Integer,PeerConfig> getPeerInfo(String fname) throws FileNotFoundException
	{
		BufferedReader br = new BufferedReader(new FileReader(fname));
		String st; Boolean b;
		Map<Integer,PeerConfig> map = new HashMap<Integer,PeerConfig>();
		try {
			while ((st = br.readLine()) != null){
				b = false;
				String[] tokens = st.split(" ");

				if(tokens[3].equals("1")) b= true;
				map.put(Integer.parseInt(tokens[0]), new PeerConfig(tokens[1], Integer.parseInt(tokens[2]), b));

			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}


	//Starts connections with all peers
	public void startAllPeers(configProperties com, Map<Integer,PeerConfig> map, Integer myPeerID){
		Set<Integer> s = map.keySet();
		ArrayList<Integer> myList = new ArrayList<Integer>(s);
		
		Iterator<Integer> it = myList.iterator();
		while(it.hasNext()){
			try{
				int i = it.next();

				if(myPeerID == i)
					new Connection(com, map,i);  

			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

	}

	
	public static void main(String []args) throws IOException 
	{
		Integer myPeerID = Integer.parseInt(args[0]);
		PeerProcess p = new PeerProcess();
		configProperties config = p.getCommon("Common.cfg");
		Map<Integer,PeerConfig> map = p.getPeerInfo("PeerInfo.cfg");
		System.out.println("Starting peers");

		p.startAllPeers(config,map,myPeerID);
	}
}
