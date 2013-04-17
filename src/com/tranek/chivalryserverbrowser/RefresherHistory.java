package com.tranek.chivalryserverbrowser;

/**
 * 
 * Controls the high level refreshing for server history.
 *
 */
public class RefresherHistory extends Refresher {
	
	/**
	 * Creates a new RefresherHistory.
	 * 
	 * @param mw the MainWindow
	 * @param sl the ServerListHistoryTab that this RefresherHistory belongs to
	 */
	public RefresherHistory(MainWindow mw, ServerListHistoryTab sl) {
		super(mw, sl);
		msq = new MasterServerQueryHistory(this.mw, sl.sf);
	}
	
	@Override
	protected void printRetrievedServers(int servers, int players) {
		System.out.println("Retrieved " + servers + " servers (history) with " + players + " players playing.");
		mw.printlnMC("Retrieved " + servers + " servers (history) with " + players + " players playing.");
	}
	
}