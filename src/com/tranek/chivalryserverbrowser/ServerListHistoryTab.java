package com.tranek.chivalryserverbrowser;
import java.util.Vector;

@SuppressWarnings("serial")
public class ServerListHistoryTab extends ServerListTab {
	
	public ServerListHistoryTab(final MainWindow mw) {
		super(mw);
		filterTableName = "filters_hist";
	}
	
	@Override
	public void startRefresher() {
		mw.serversHist = new Vector<ChivServer>();
		refresher = new RefresherHistory(mw, this);
		refresher.start();
	}
	
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversHist;
	}
	
}
