package com.tranek.chivalryserverbrowser;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.table.DefaultTableModel;

import net.barkerjr.gameserver.GameServer.Request;
import net.barkerjr.gameserver.valve.SourceServer;
import net.barkerjr.gameserver.valve.SourceServerList;
import net.barkerjr.gameserver.valve.ValveServerList;

public class MasterServerQuery {
	protected final MainWindow mw;
	protected ServerFilters sf;
	public ExecutorService pool;
	protected QueryWorkerSynch synch;
	protected Vector<ChivServer> slist;
	
	public MasterServerQuery(MainWindow mw, ServerFilters sf) {
		this.sf = sf;
		this.mw = mw;
	}
	
	public void queryServers(ServerFilters sf, DefaultTableModel dataModel) throws IOException, InterruptedException {
		this.sf = sf;
		slist = new Vector<ChivServer>();
		synch = new QueryWorkerSynch(dataModel, getServerList(), mw);
		pool = Executors.newFixedThreadPool(sf.numThreads);
		Vector<ChivServer> serverList = getServers();
		mw.printlnMC("Retrieved list of servers.");
		mw.printlnMC("Querying individual servers...");
		Thread.sleep(2000);
		queryIndividualServers(serverList);
	}

	
	public Vector<ChivServer> getServers() throws IOException {
		SourceServerList list = new SourceServerList();
		list.gameDir = "chivalrymedievalwarfare";
		HashSet<SourceServer> serverList = new HashSet<SourceServer>();
		Vector<ChivServer> cServers = new Vector<ChivServer>();
		ValveServerList<SourceServer>.ServerIterator servers = list.iterator(10000);
		try {
			while (servers.hasNext()) {
				SourceServer server = servers.next();
				serverList.add(server);
				server.load(Request.INFORMATION);
			}
		} finally {
			servers.close();
		}
		for ( SourceServer server : serverList ) {
			cServers.add(new ChivServer(server.getName(), server.getIP(), "" + server.getPort(), server.getMap()));
		}
		return cServers;
	}
	
	public void queryIndividualServers(Vector<ChivServer> serverList) throws InterruptedException {
		Set<Future<ChivServer>> set = new HashSet<Future<ChivServer>>();
		for (ChivServer server: serverList) {
			applyFiltersAndQueryServers(server, set);
		}
		
		// Iterate through the results and compile into one list
		// Synchronizes this thread with its spawned threads
	    for ( Future<ChivServer> future : set ) {
	    	try {
				if ( future.get() != null ) {
					slist.add(future.get());
				}
			} catch (ExecutionException e) {}
	    }
	}
	
	public void applyFiltersAndQueryServers(ChivServer server, Set<Future<ChivServer>> set) {
		Callable<ChivServer> callable = new QueryWorker(server.mIP, Integer.parseInt(server.mQueryPort), sf, synch, pool, mw);
		Future<ChivServer> future = pool.submit(callable);
		set.add(future);
	}
	
	public void stopRefreshing(MainWindow mw) {
		mw.printlnMC("Refreshing stopped.");
		if (pool != null) {
			pool.shutdownNow();
		}
	}
	
	public Vector<ChivServer> getServerList() {
		return mw.servers;
	}
	
}
