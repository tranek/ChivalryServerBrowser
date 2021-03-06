package com.tranek.chivalryserverbrowser;

import java.util.Vector;

/**
 * 
 * Controls the high level refreshing for normal servers.
 *
 */
public class RefresherNormal extends Refresher {
	
	/**
	 * Creates a new RefresherNormal.
	 * 
	 * @param mw the MainWindow
	 * @param sl the ServerListNormalTab that this RefresherNormal belongs to
	 */
	public RefresherNormal(MainWindow mw, ServerListNormalTab sl) {
		super(mw, sl);
		msq = new MasterServerQueryNormal(mw, sl.sf);
	}
	
	@Override
	protected void printRetrievedServers(int servers, int players) {
		System.out.println("Retrieved " + servers + " servers with " + players + " players playing.");
		mw.printlnMC("Retrieved " + servers + " servers with " + players + " players playing.");
	}
	
	@Override
	public void updateServerDB(Vector<ChivServer> servers) {
		mw.updateServerDB(servers);
	}
	
}