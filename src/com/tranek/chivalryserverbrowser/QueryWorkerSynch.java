package com.tranek.chivalryserverbrowser;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * 
 * Synchronization object for {@link QueryWorker} threads. It synchronizes access to the
 * server list tab's data model and the MainWindow's list of servers for that server list.
 *
 */
public class QueryWorkerSynch {
	
	/** The server list tab's data model. */
	private final DefaultTableModel dataModel;
	/** The MainWindow's list of currently queried servers for this server list. */
	private final Vector<ChivServer> servers;
	/** A reference to the MainWindow. */
	private final MainWindow mw;
	
	/**
	 * Creates a new QueryWorkerSynch.
	 * 
	 * @param datamodel the server list tab's data model
	 * @param servs the {@link MainWindow}'s list of currently queried servers for this server list
	 * @param mw the MainWindow
	 */
	public QueryWorkerSynch(DefaultTableModel datamodel, Vector<ChivServer> servs, MainWindow mw) {
		this.dataModel = datamodel;
		this.servers = servs;
		this.mw = mw;
	}
	
	/**
	 * Adds a row of data to the server list tab's server table.
	 * 
	 * @param rowData the row data to add to the table
	 * @see DefaultTableModel#addRow(Object[])
	 */
	public synchronized void addToTable(Object[] rowData) {
		dataModel.addRow(rowData);
	}
	
	/**
	 * Adds a server to the {@link MainWindow}'s list of currently queried servers for this server list tab.
	 * {@link Vector} is inherently thread safe, so this method does not have the synchronized keyword.
	 * 
	 * @param cs the ChivServer to add to the list
	 * @see Vector#add(Object)
	 */
	public void addToList(ChivServer cs) {
		servers.add(cs);
	}
	
	/**
	 * Gets the latitude and longitude of a server that has a location but no latitude and longitude. RIPE
	 * sometimes only provides a country code but no latitude or longitude. This method compares the location
	 * string to other servers that may have the same exact location string and a latitude and longitude returns
	 * those values.
	 * 
	 * @param location the location string to search other servers for
	 * @return a HashMap of the latitude and longitude
	 */
	public synchronized HashMap<String, String> getLocFromOtherServer(String location) {
		for ( ChivServer c : servers ) {
			if ( !c.mLatitude.equals("") && !c.mLongitude.equals("") && c.mLocation.equals(location)) {
				HashMap<String, String> result = new HashMap<String, String>();
				result.put("latitude", c.mLatitude);
				result.put("longitude", c.mLongitude);
				return result;
			}
		}
		return null;
	}
	
	/**
	 * Adds a {@link ChivServer} marker to the {@link MainWindow#mapTab}.
	 * 
	 * @param cs the ChivServer to add
	 */
	public synchronized void addToMap(ChivServer cs) {
		if ( mw.chckbxNormalServers.isSelected() ) {
			mw.addMarker(mw.serverListTab, cs);
		}
	}
}