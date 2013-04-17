package com.tranek.chivalryserverbrowser;
import java.util.Vector;

/**
 * 
 * The server list tab for normal Chivalry: Medieval Warfare servers.
 *
 */
@SuppressWarnings("serial")
public class ServerListNormalTab extends ServerListTab {
	
	/**
	 * Creates a new {@link ServerListNormalTab}.
	 * 
	 * @param mw the MainWindow
	 */
	public ServerListNormalTab(final MainWindow mw) {
		super(mw);
		filterTableName = "filters";
	}
	
	/**
	 * Initializes and starts this tab's {@link RefresherNormal}.
	 */
	@Override
	protected void startRefresher() {
		mw.servers = new Vector<ChivServer>();
		refresher = new RefresherNormal(mw, this);
		refresher.start();
	}	

	/**
	 * Returns the list of currently queried normal servers from the {@link MainWindow}.
	 * 
	 * @see ChivServer
	 */
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.servers;
	}
	
}
