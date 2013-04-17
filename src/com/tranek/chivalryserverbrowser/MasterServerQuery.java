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

/**
 * 
 * Superclass for the low level server refreshing.
 *
 */
public abstract class MasterServerQuery {
	/** A reference to the MainWindow. */
	protected final MainWindow mw;
	/** The server filters for querying the servers. */
	protected ServerFilters sf;
	/** The pool of threads to query the servers. */
	protected ExecutorService pool;
	/** The synchronization object for the threads. */
	protected QueryWorkerSynch synch;
	/** A unused list of the ChivServers. It may be used someday, but probably not. */
	protected Vector<ChivServer> slist;
	
	/**
	 * Creates a new MasterServerQuery.
	 * 
	 * @param mw the MainWindow
	 * @param sf the server filters for querying the servers
	 */
	public MasterServerQuery(MainWindow mw, ServerFilters sf) {
		this.sf = sf;
		this.mw = mw;
	}
	
	/**
	 * Gets the list of servers and queries them.
	 * 
	 * @param sf the server filters for querying the servers
	 * @param dataModel the server list table data model
	 * @throws IOException
	 * @throws InterruptedException
	 * @see QueryWorkerSynch
	 * @see #getServers()
	 * @see #queryIndividualServers(Vector)
	 */
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

	/**
	 * Gets the servers to query from the Steam Master Server.
	 * 
	 * @return a {@link Vector} of {@link ChivServer} objects
	 * @throws IOException
	 */
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
	
	/**
	 * Queries each individual server for its information.
	 * 
	 * @param serverList the list of {@link ChivServer} to query
	 * @throws InterruptedException
	 * @see ExecutionException
	 */
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
	
	/**
	 * Applies filters, creates the new {@link QueryWorker}, and adds to the thread pool.
	 * 
	 * @param server the server to query
	 * @param set the set of {@link Future} to get a return value (not used right now)
	 * @see QueryWorker
	 * @see Future
	 * @see ExecutorService
	 */
	public void applyFiltersAndQueryServers(ChivServer server, Set<Future<ChivServer>> set) {
		Callable<ChivServer> callable = new QueryWorker(server.mIP, Integer.parseInt(server.mQueryPort), sf, synch, pool, mw);
		Future<ChivServer> future = pool.submit(callable);
		set.add(future);
	}
	
	/**
	 * Stops refreshing.
	 * 
	 * @param mw the MainWindow
	 * @see ExecutorService#shutdownNow()
	 */
	public void stopRefreshing(MainWindow mw) {
		mw.printlnMC("Refreshing stopped.");
		if (pool != null) {
			pool.shutdownNow();
		}
	}
	
	/**
	 * Gets the list of currently queried servers for this server list from the {@link MainWindow}.
	 * 
	 * @return the list of currently queried servers
	 */
	public Vector<ChivServer> getServerList() {
		return mw.servers;
	}
	
}
