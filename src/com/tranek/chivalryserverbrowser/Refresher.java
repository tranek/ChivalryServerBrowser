package com.tranek.chivalryserverbrowser;

import java.util.Vector;


public abstract class Refresher extends Thread {
	protected MainWindow mw;
	private ServerListInterface sl;
	protected MasterServerQuery msq;
	
	public Refresher(MainWindow mw, ServerListInterface sl) {
		this.mw = mw;
		this.sl = sl;
	}
	
	public void run() {
		try {
			System.out.println("Refreshing servers...");
			String mctext = mw.messageCenter.getText();
			if ( mctext.equals("") ) {
				mw.printMC("Refreshing servers...");
			} else {
				mw.printlnMC("Refreshing servers...");
			}

			msq.queryServers(sl.getFilters(), sl.getTableModel());
			
			Vector<ChivServer> servers = sl.getServerList();
			
			if ( servers != null ) {
				int count = 0;
				for (ChivServer cs : servers){
					count += Integer.parseInt(cs.mCurrentPlayers);
				}
				printRetrievedServers(servers.size(), count);
			} else {
				System.out.println("Retrieved 0 servers.");
				mw.printlnMC("Retrieved 0 servers.");
			}
			
			System.out.println("Finished refreshing servers.");
			mw.printlnMC("Finished refreshing servers.");
			mw.updateServerDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopRefreshing() {
		if ( isRefreshing() ) {
			Vector<ChivServer> serversClone;
			msq.stopRefreshing(mw);
			if ( sl.getServerList() != null ) {
				serversClone = mw.deepCopyCSVector(sl.getServerList());
				int count = 0;
				for (ChivServer cs : serversClone){
					count += Integer.parseInt(cs.mCurrentPlayers);
				}
				printRetrievedServers(serversClone.size(), count);
				updateServerDB(serversClone);
			} else {
				System.out.println("Retrieved 0 servers.");
				mw.printlnMC("Retrieved 0 servers.");
			}
		}
	}
	
	protected void printRetrievedServers(int servers, int players) {
		System.out.println("Retrieved " + servers + " servers with " + players + " players playing.");
		mw.printlnMC("Retrieved " + servers + " servers with " + players + " players playing.");
	}
	
	public void updateServerDB(Vector<ChivServer> servers) {
	}
	
	public boolean isRefreshing() {
		return !msq.pool.isShutdown();
	}
	
}
