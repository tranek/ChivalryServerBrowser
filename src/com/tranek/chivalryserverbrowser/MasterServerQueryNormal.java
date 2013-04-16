package com.tranek.chivalryserverbrowser;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MasterServerQueryNormal extends MasterServerQuery {
	
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
			if ( (sName.length() >  23) && ( sName.substring(0, 20).equals("official duel server") ||
				sName.substring(0, 23).equals("official classic server") ||
				sName.substring(0, 19).equals("official ffa server") ||
				sName.substring(0, 19).equals("official lts server") ||
				sName.substring(0, 19).equals("official tdm server") ||
				sName.substring(0, 18).equals("official to server") ) ) {
					Callable<ChivServer> callable = new QueryWorker(server.mIP, Integer.parseInt(server.mQueryPort), sf, synch, pool);
					Future<ChivServer> future = pool.submit(callable);
					set.add(future);
			}
		} else if ( server.mName != null && server.mName.toLowerCase().contains(serverNameFilter)
				&& ( sf.type.equals("All") || sf.type.equals(gametype) ) ) {
			Callable<ChivServer> callable = new QueryWorker(server.mIP, Integer.parseInt(server.mQueryPort), sf, synch, pool);
			Future<ChivServer> future = pool.submit(callable);
			set.add(future);
		}
	}
	
}
