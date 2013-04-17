package com.tranek.chivalryserverbrowser;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * The interface for the server list tabs. It allows guarantees of certain methods
 * so that I can pass in a ServerListInterface as a parameter in other functions.
 */
public interface ServerListInterface {
	/** Refreshes this server list tab's servers. */
	public void RefreshServers();
	/** Gets the reference to the MainWindow. */
	public MainWindow getMW();
	/** Gets this server list tab's current list of servers held by the MainWindow. */
	public Vector<ChivServer> getServerList();
	/** Gets this server list tab's current filters. */
	public ServerFilters getFilters();
	/** Gets this server list tab's table model. */
	public DefaultTableModel getTableModel();
	/** Whether or not this server list tab is currently refreshing its servers. */
	public boolean isRefreshing();
}
