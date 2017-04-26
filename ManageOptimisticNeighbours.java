/**
 *
 * @author Joel
 */

import java.io.IOException;
import java.util.*;

public class ManageOptimisticNeighbours implements Runnable
{
	private Connection myConnection;
	WriteLog w = new WriteLog();

	public ManageOptimisticNeighbours(Connection Connection)
	{
		this.myConnection = Connection;
	}

	@Override
	public void run()
	{
		try
		{
			this.optUnchokedPeer();
		} catch (Exception e)
		{
			e.printStackTrace();
		} 
	}

	//Unchoke a peer and choke peer with least upload rate
	private void optUnchokedPeer() throws IOException, InterruptedException
	{

		Integer prevPeer = myConnection.getUnchokedPeer_Prev();
		if (prevPeer != -1)
			myConnection.reportChokedPeer(myConnection.getUnchokedPeer_Prev());

		Set<Integer> chokedPeersSet = myConnection.getChokedPeers();
		List<Integer> interestedAndChoked = new LinkedList<Integer>();
		interestedAndChoked.addAll(myConnection.getmyInterestedNeighbours());
		interestedAndChoked.retainAll(chokedPeersSet);
		if(interestedAndChoked.size() > 0)
		{
			Random rand = new Random();
			int selectedPeer = interestedAndChoked.get(rand.nextInt(interestedAndChoked.size()));
			myConnection.reportUnchokedPeer(selectedPeer);
			myConnection.setUnchokedPeer_Prev(selectedPeer);
			w.OptUnchokedNeighbours(Integer.toString(myConnection.getMyPeerID()), Integer.toString(selectedPeer));
		}
	}
}
