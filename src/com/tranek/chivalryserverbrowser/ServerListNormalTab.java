package com.tranek.chivalryserverbrowser;
import java.util.Vector;

@SuppressWarnings("serial")
public class ServerListNormalTab extends ServerListTab {
	
	public ServerListNormalTab(final MainWindow mw) {
		super(mw);
		filterTableName = "filters";
	}
	
	@Override
	public void startRefresher() {
		mw.servers = new Vector<ChivServer>();
		refresher = new RefresherNormal(mw, this);
		refresher.start();
	}	

	@Override
	public Vector<ChivServer> getServerList() {
		return mw.servers;
	}
	
}
