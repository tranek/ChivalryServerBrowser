package com.tranek.chivalryserverbrowser;

/**
 * 
 * Controls the high level refreshing for beta servers.
 *
 */
public class RefresherBeta extends Refresher {
	
	/**
	 * Creates a new RefresherBeta.
	 * 
	 * @param mw the MainWindow
	 * @param sl the ServerListBetaTab that this RefresherBeta belongs to
	 */
	public RefresherBeta(MainWindow mw, ServerListBetaTab sl) {
		super(mw, sl);
		msq = new MasterServerQueryBeta(this.mw, sl.sf);
	}
	
	@Override
	protected void printRetrievedServers(int servers, int players) {
		System.out.println("Retrieved " + servers + " beta servers with " + players + " players playing.");
		mw.printlnMC("Retrieved " + servers + " beta servers with " + players + " players playing.");
	}
	
}