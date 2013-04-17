package com.tranek.chivalryserverbrowser;
import java.util.Vector;

/**
 * 
 * The server list tab for recently joined (history) Chivalry: Medieval Warfare servers.
 *
 */
@SuppressWarnings("serial")
public class ServerListHistoryTab extends ServerListTab {
	
	/**
	 * Creates a new {@link ServerListHistoryTab}.
	 * 
	 * @param mw the MainWindow
	 */
	public ServerListHistoryTab(final MainWindow mw) {
		super(mw);
		filterTableName = "filters_hist";
	}
	
	/**
	 * Initializes and starts this tab's {@link RefresherHistory}.
	 */
	@Override
	protected void startRefresher() {
		mw.serversHist = new Vector<ChivServer>();
		refresher = new RefresherHistory(mw, this);
		refresher.start();
	}
	
	/**
	 * Returns the list of currently queried history servers from the {@link MainWindow}.
	 * 
	 * @see ChivServer
	 */
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversHist;
	}
	
}
