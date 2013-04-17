package com.tranek.chivalryserverbrowser;
import java.util.Vector;

@SuppressWarnings("serial")
public class ServerListFavTab extends ServerListTab {
	
	public ServerListFavTab(final MainWindow mw) {
		super(mw);
		filterTableName = "filters_fav";
	}
	
	@Override
	public void startRefresher() {
		mw.serversFav = new Vector<ChivServer>();
		refresher = new RefresherFav(mw, this);
		refresher.start();
	}
	
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversFav;
	}
	
}
