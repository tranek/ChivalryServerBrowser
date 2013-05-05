package com.tranek.chivalryserverbrowser;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 
 * Controls the low level refreshing for normal servers.
 *
 */
public class MasterServerQueryNormal extends MasterServerQuery {
	
	/**
	 * Creates a new MasterServerQueryNormal
	 * 
	 * @param mw the MainWindow
	 * @param sf the server filters for querying the servers
	 */
	public MasterServerQueryNormal(MainWindow mw, ServerFilters sf) {
		super(mw, sf);
	}
	
	@Override
	public void applyFiltersAndQueryServers(ChivServer server, Set<Future<ChivServer>> set) {
		if ( server.mName == null ) {
			return;
		}
		String gametype = ChivServer.getGameMode(server.mMap);
		String serverNameFilter = sf.name.toLowerCase();
		String sName = server.mName.toLowerCase();
		
		if ( sf.officialservers && ( sf.type.equals("All") || sf.type.equals(gametype) ) ) {
			if ( 
				(sName.length() > 23 && sName.substring(0, 24).equals("official duel ping limit") ) ||
				(sName.length() > 19 && sName.substring(0, 20).equals("official duel server") ) ||
				(sName.length() > 26 && sName.substring(0, 27).equals("official classic ping limit") ) ||
				(sName.length() > 25 && sName.substring(0, 26).equals("official classic rank 1-20") ) ||
				(sName.length() > 22 && sName.substring(0, 23).equals("official classic server") ) ||
				(sName.length() > 22 && sName.substring(0, 23).equals("official ffa ping limit") ) ||
				(sName.length() > 18 && sName.substring(0, 19).equals("official ffa server") ) ||
				(sName.length() > 22 && sName.substring(0, 23).equals("official lts ping limit") ) ||
				(sName.length() > 18 && sName.substring(0, 19).equals("official lts server") ) ||
				(sName.length() > 22 && sName.substring(0, 23).equals("official tdm ping limit") ) ||
				(sName.length() > 18 && sName.substring(0, 19).equals("official tdm server") ) ||
				(sName.length() > 21 && sName.substring(0, 22).equals("official to ping limit") ) ||
				(sName.length() > 17 && sName.substring(0, 18).equals("official to server") ) ) {
					Callable<ChivServer> callable = new QueryWorker(server.mIP, Integer.parseInt(server.mQueryPort), sf, synch, pool, mw);
					Future<ChivServer> future = pool.submit(callable);
					set.add(future);
			}
		} else if ( server.mName != null && server.mName.toLowerCase().contains(serverNameFilter)
				&& ( sf.type.equals("All") || sf.type.equals(gametype) ) ) {
			Callable<ChivServer> callable = new QueryWorker(server.mIP, Integer.parseInt(server.mQueryPort), sf, synch, pool, mw);
			Future<ChivServer> future = pool.submit(callable);
			set.add(future);
		}
	}
	
}
