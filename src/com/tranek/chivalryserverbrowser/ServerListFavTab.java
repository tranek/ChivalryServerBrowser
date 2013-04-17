package com.tranek.chivalryserverbrowser;
import java.util.Vector;

/**
 * 
 * The server list tab for favorite Chivalry: Medieval Warfare servers.
 *
 */
@SuppressWarnings("serial")
public class ServerListFavTab extends ServerListTab {
	
	/**
	 * Creates a new {@link ServerListFavTab}.
	 * 
	 * @param mw the MainWindow
	 */
	public ServerListFavTab(final MainWindow mw) {
		super(mw);
		filterTableName = "filters_fav";
	}
	
	/**
	 * Initializes and starts this tab's {@link RefresherFav}.
	 */
	@Override
	protected void startRefresher() {
		mw.serversFav = new Vector<ChivServer>();
		refresher = new RefresherFav(mw, this);
		refresher.start();
	}
	
	/**
	 * Returns the list of currently queried favorite servers from the {@link MainWindow}.
	 * 
	 * @see ChivServer
	 */
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversFav;
	}
	
}
