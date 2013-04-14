package com.tranek.chivalryserverbrowser;
import java.util.Vector;

/**
 * The interface for the server list tabs. It allows guarantees of certain methods
 * so that I can pass in a ServerListInterface as a parameter in other functions.
 */
public interface ServerListInterface {
	/** Refreshes this server list tab's servers. */
	public void RefreshServers();
	/** Gets the reference to the MainWindow. */
	public MainWindow getMW();
	/** Gets this server list tab's current list of servers. */
	public Vector<ChivServer> getServerList();
	/** Whether or not this server list tab is currently refreshing its servers. */
	public boolean isRefreshing();
}
