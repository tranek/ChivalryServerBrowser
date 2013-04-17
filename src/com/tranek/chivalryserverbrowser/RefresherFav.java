package com.tranek.chivalryserverbrowser;

/**
 * 
 * Controls the high level refreshing for favorite servers.
 *
 */
public class RefresherFav extends Refresher {

	/**
	 * Creates a new RefresherFav.
	 * 
	 * @param mw the MainWindow
	 * @param sl the ServerListFavTab that this RefresherFav belongs to
	 */
	public RefresherFav(MainWindow mw, ServerListFavTab sl) {
		super(mw, sl);
		msq = new MasterServerQueryFav(this.mw, sl.sf);
	}
	
	@Override
	protected void printRetrievedServers(int servers, int players) {
		System.out.println("Retrieved " + servers + " favorite servers with " + players + " players playing.");
		mw.printlnMC("Retrieved " + servers + " favorite servers with " + players + " players playing.");
	}
	
}