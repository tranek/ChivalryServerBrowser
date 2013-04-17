package com.tranek.chivalryserverbrowser;

public class RefresherBeta extends Refresher {
	
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