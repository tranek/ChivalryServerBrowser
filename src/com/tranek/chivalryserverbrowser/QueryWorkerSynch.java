package com.tranek.chivalryserverbrowser;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class QueryWorkerSynch {
	private final DefaultTableModel dataModel;
	private final Vector<ChivServer> servers;
	private final MainWindow mw;
	
	public QueryWorkerSynch(DefaultTableModel datamodel, Vector<ChivServer> servs, MainWindow mw) {
		this.dataModel = datamodel;
		this.servers = servs;
		this.mw = mw;
	}
	
	public synchronized void addToTable(Object[] rowData) {
		dataModel.addRow(rowData);
	}
	
	public void addToList(ChivServer cs) {
		servers.add(cs);
	}
	
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
	
	public synchronized void addToMap(ChivServer cs) {
		if ( mw.chckbxNormalServers.isSelected() ) {
			mw.addMarker(mw.serverListTab, cs);
		}
	}
}