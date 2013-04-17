package com.tranek.chivalryserverbrowser;
import java.util.Vector;

@SuppressWarnings("serial")
public class ServerListBetaTab extends ServerListTab {

	public ServerListBetaTab(final MainWindow mw) {
		super(mw);
		filterTableName = "filters_beta";
	}
	
	@Override
	public void startRefresher() {
		mw.serversBeta = new Vector<ChivServer>();
		refresher = new RefresherBeta(mw, this);
		refresher.start();
	}	
	
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversBeta;
	}

}
