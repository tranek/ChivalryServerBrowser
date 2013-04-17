package com.tranek.chivalryserverbrowser;

public class RefresherHistory extends Refresher {
	
	public RefresherHistory(MainWindow mw, ServerListHistoryTab sl) {
		super(mw, sl);
		msq = new MasterServerQueryHistory(this.mw, sl.sf);
	}
	
	
	//TODO wrong player counts!!!!
	@Override
	protected void printRetrievedServers(int servers, int players) {
		System.out.println("Retrieved " + servers + " servers (history) with " + players + " players playing.");
		mw.printlnMC("Retrieved " + servers + " servers (history) with " + players + " players playing.");
	}
	
}