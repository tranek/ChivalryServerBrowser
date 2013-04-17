package com.tranek.chivalryserverbrowser;

import java.util.Vector;

/**
 * 
 * Superclass for high level server refreshing.
 *
 */
public abstract class Refresher extends Thread {
	protected MainWindow mw;
	private ServerListInterface sl;
	protected MasterServerQuery msq;
	
	/**
	 * Creates a new Refresher.
	 * 
	 * @param mw the MainWindow
	 * @param sl the {@link ServerListInterface} that owns this Refresher
	 */
	public Refresher(MainWindow mw, ServerListInterface sl) {
		this.mw = mw;
		this.sl = sl;
	}
	
	/**
	 * Starts querying the servers.
	 * 
	 * @see MasterServerQuery#queryServers(ServerFilters, javax.swing.table.DefaultTableModel)
	 * @see Thread#run()
	 */
	public void run() {
		try {
			System.out.println("Refreshing servers...");
			String mctext = mw.messageCenter.getText();
			if ( mctext.equals("") ) {
				mw.printMC("Refreshing servers...");
			} else {
				mw.printlnMC("Refreshing servers...");
			}

			msq.queryServers(sl.getFilters(), sl.getTableModel());
			
			Vector<ChivServer> servers = sl.getServerList();
			
			if ( servers != null ) {
				int count = 0;
				for (ChivServer cs : servers){
					count += Integer.parseInt(cs.mCurrentPlayers);
				}
				printRetrievedServers(servers.size(), count);
			} else {
				System.out.println("Retrieved 0 servers.");
				mw.printlnMC("Retrieved 0 servers.");
			}
			
			System.out.println("Finished refreshing servers.");
			mw.printlnMC("Finished refreshing servers.");
			mw.updateServerDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops refreshing servers for this server list.
	 * 
	 * @see MasterServerQuery#stopRefreshing(MainWindow)
	 */
	public void stopRefreshing() {
		if ( isRefreshing() ) {
			Vector<ChivServer> serversClone;
			msq.stopRefreshing(mw);
			if ( sl.getServerList() != null ) {
				serversClone = mw.deepCopyCSVector(sl.getServerList());
				int count = 0;
				for (ChivServer cs : serversClone){
					count += Integer.parseInt(cs.mCurrentPlayers);
				}
				printRetrievedServers(serversClone.size(), count);
				updateServerDB(serversClone);
			} else {
				System.out.println("Retrieved 0 servers.");
				mw.printlnMC("Retrieved 0 servers.");
			}
		}
	}
	
	/**
	 * Prints out how many servers were retrieved and how many players are on those servers.
	 * 
	 * @param servers the number of servers retrieved
	 * @param players the number of players on the servers retrieved
	 */
	protected void printRetrievedServers(int servers, int players) {
		System.out.println("Retrieved " + servers + " servers with " + players + " players playing.");
		mw.printlnMC("Retrieved " + servers + " servers with " + players + " players playing.");
	}
	
	/**
	 * Updates the servers queried into the list of server IP, queryports, and gameports for later lookup.
	 * This function is only used by RefersherNormal.
	 * 
	 * @param servers the list of servers to add into the database
	 */
	public void updateServerDB(Vector<ChivServer> servers) {}
	
	/**
	 * Gets if this server is still refreshing.
	 * 
	 * @return true if the server is still refreshing; false if otherwise
	 */
	public boolean isRefreshing() {
		return !msq.pool.isShutdown();
	}
	
}
