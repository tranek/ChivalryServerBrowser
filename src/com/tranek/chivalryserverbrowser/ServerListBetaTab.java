package com.tranek.chivalryserverbrowser;
import java.util.Vector;

/**
 * 
 * The server list tab for beta Chivalry: Medieval Warfare servers.
 *
 */
@SuppressWarnings("serial")
public class ServerListBetaTab extends ServerListTab {

	/**
	 * Creates a new {@link ServerListBetaTab}.
	 * 
	 * @param mw the MainWindow
	 */
	public ServerListBetaTab(final MainWindow mw) {
		super(mw);
		filterTableName = "filters_beta";
	}
	
	/**
	 * Initializes and starts this tab's {@link RefresherBeta}.
	 */
	@Override
	protected void startRefresher() {
		mw.serversBeta = new Vector<ChivServer>();
		refresher = new RefresherBeta(mw, this);
		refresher.start();
	}	
	
	/**
	 * Returns the list of currently queried beta servers from the {@link MainWindow}.
	 * 
	 * @see ChivServer
	 */
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversBeta;
	}

}
