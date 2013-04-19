package com.tranek.chivalryserverbrowser;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.table.DefaultTableModel;

import net.barkerjr.gameserver.GameServer.Request;
import net.barkerjr.gameserver.valve.SourceServer;
import net.barkerjr.gameserver.valve.SourceServerList;
import net.barkerjr.gameserver.valve.ValveServerList;

import com.github.koraktor.steamcondenser.steam.SteamPlayer;

public class FindPlayerQuerier extends Thread {

	private final FindPlayerTab fpt;
	protected ExecutorService pool;
	/** A unused list of the ChivServers. It may be used someday, but probably not. */
	protected Vector<ChivServer> slist;
	/** The synchronization object for the threads. */
	protected Synchronizer synch;
	
	public FindPlayerQuerier(FindPlayerTab fpt) {
		this.fpt = fpt;
	}
	
	public void run() {
		pool = Executors.newFixedThreadPool(32);
		synch = new Synchronizer((DefaultTableModel)fpt.dataModel, fpt.servers);
		slist = new Vector<ChivServer>();
		
		SourceServerList list = new SourceServerList();
		list.gameDir = "chivalrymedievalwarfare";
		HashSet<SourceServer> serverList = new HashSet<SourceServer>();
		ValveServerList<SourceServer>.ServerIterator servers = list.iterator(10000);
		try {
			while (servers.hasNext()) {
				SourceServer server = servers.next();
				serverList.add(server);
				server.load(Request.INFORMATION);
			}
		} catch (IOException e) {
		} finally {
			servers.close();
		}
		
		fpt.getMW().printlnMC("Retrieved servers from Steam Master Server.");
		fpt.getMW().printlnMC("Querying individual servers.");
		
		Set<Future<ChivServer>> set = new HashSet<Future<ChivServer>>();
		for ( SourceServer server : serverList ) {
			Callable<ChivServer> callable = new Worker(server.getIP(), server.getPort(), fpt.getMW());
			Future<ChivServer> future = pool.submit(callable);
			set.add(future);
		}
		
		// Iterate through the results and compile into one list
		// Synchronizes this thread with its spawned threads
	    /*for ( Future<ChivServer> future : set ) {
	    	try {
				if ( future.get() != null ) {
					slist.add(future.get());
				}
			} catch (ExecutionException | InterruptedException e) {}
	    }*/
		
		while ( !pool.isTerminated() ) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
	    
	    if ( fpt.servers.size() < 1 ) {
	    	fpt.getMW().printlnMC("Did not find player on any server.");
	    } else {
	    	fpt.getMW().printlnMC("Search finished.");
	    }
	}
	
	public boolean isRefreshing() {
		if ( pool == null) {
			return false;
		}
		return !pool.isShutdown();
	}
	
	public void terminate() {
		if ( pool != null ) {
			pool.shutdownNow();
		}
	}
	
	private class Worker implements Callable<ChivServer> {

		private String ip;
		private int queryport;
		/** The ChivServer that is created after the server is queried. */
		private ChivServer cs;
		/** A reference to the MainWindow. */
		private MainWindow mw;
		
		public Worker(String ip, int queryport, MainWindow mw) {
			this.ip = ip;
			this.queryport = queryport;
			this.mw = mw;
		}
		
		@Override
		public ChivServer call() throws Exception {
			ServerQuery sq = new ServerQuery(ip, queryport);
			HashMap<String, SteamPlayer> players = sq.getPlayers();
			boolean foundPlayer = false;
			for ( Object value : players.values() ) {
				SteamPlayer player = (SteamPlayer)value;
				if ( fpt != null && fpt.lblNickName.getText().equals(player.getName()) ) {
					mw.printlnMC("Found player on server!");
					foundPlayer = true;
					break;
				}
			}
			
			if ( foundPlayer ) {
				QueryServerCondenser qsc = new QueryServerCondenser(ip, queryport);
				HashMap<String, String> info = qsc.getInfo();
	
				String map = info.get("map");
				String name = info.get("name");
		
				String gamemode = ChivServer.getGameMode(map);
				
				String location = "";
				String lat = "";
				String lon = "";		
				HashMap<String, String> loc = ChivServer.getLocation(mw, ip);
				location = loc.get("location");
				lat = loc.get("latitude");
				lon = loc.get("longitude");
				
				cs = new ChivServer(name, ip, "" + queryport, info.get("gameport"), 
						map, gamemode, info.get("ping"), info.get("maxplayers"),
						info.get("currentplayers"), info.get("haspassword"), info.get("minrank"), 
						info.get("maxrank"), location, info.get("perspective"), lat, lon);
						
				String haspassword = "";
				if ( cs.mHasPassword != null && cs.mHasPassword.equals("1") ) {
					haspassword = "Yes";
				}
				
				// bandaid for possible null values from failed queries (can't Integer.parse on null)
				int ping = 0;
				if (cs.mPing != null && !cs.mPing.equals("")) {
					ping = Integer.parseInt(cs.mPing);
				}
				int minrank = 0;
				if (cs.mMinRank != null && !cs.mMinRank.equals("")) {
					minrank = Integer.parseInt(cs.mMinRank);
				}
				int maxrank = 0;
				if (cs.mMaxRank != null && !cs.mMaxRank.equals("")) {
					maxrank = Integer.parseInt(cs.mMaxRank);
				}
				
				Object[] rowData = {cs.mName, "<html><U><FONT COLOR=BLUE>" + cs.mIP + ":" + cs.mGamePort + "</FONT></U></html>",
						cs.mGameMode, cs.mMap, cs.mCurrentPlayers + " / " + cs.mMaxPlayers,
						ping, location, haspassword, minrank, maxrank};
							
				
				//Solution to stopping refreshing! yay.
				if ( pool.isShutdown() ) {
					return null;
				}
	
				synch.addToTable(rowData);
				synch.addToList(cs);
				return cs;
			}
			return null;
		}
		
	}
	
	private class Synchronizer {
		/** The server list tab's data model. */
		private final DefaultTableModel dataModel;
		/** The MainWindow's list of currently queried servers for this server list. */
		private final Vector<ChivServer> servers;
		
		/**
		 * Creates a new QueryWorkerSynch.
		 * 
		 * @param datamodel the server list tab's data model
		 * @param servs the {@link MainWindow}'s list of currently queried servers for this server list
		 * @param mw the MainWindow
		 */
		public Synchronizer(DefaultTableModel datamodel, Vector<ChivServer> servs) {
			this.dataModel = datamodel;
			this.servers = servs;
		}
		
		/**
		 * Adds a row of data to the server list tab's server table.
		 * 
		 * @param rowData the row data to add to the table
		 * @see DefaultTableModel#addRow(Object[])
		 */
		public synchronized void addToTable(Object[] rowData) {
			dataModel.addRow(rowData);
		}
		
		/**
		 * Adds a server to the {@link MainWindow}'s list of currently queried servers for this server list tab.
		 * {@link Vector} is inherently thread safe, so this method does not have the synchronized keyword.
		 * 
		 * @param cs the ChivServer to add to the list
		 * @see Vector#add(Object)
		 */
		public void addToList(ChivServer cs) {
			servers.add(cs);
		}
		
	}
	
}
