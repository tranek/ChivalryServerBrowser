package com.tranek.chivalryserverbrowser;

public class RefresherFav extends Refresher {

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