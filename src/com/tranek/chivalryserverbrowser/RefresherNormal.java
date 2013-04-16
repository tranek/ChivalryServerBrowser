package com.tranek.chivalryserverbrowser;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class RefresherNormal extends Thread {
	
	private MainWindow mw;
	private MasterServerQueryNormal msq;
	private ServerListNormalTab sl;
	
	public RefresherNormal(MainWindow mw, ServerListNormalTab slt) {
		this.mw = mw;
		this.sl = slt;
		msq = new MasterServerQueryNormal(this.mw, sl.sf);
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

			msq.queryServers(sl.sf, (DefaultTableModel)sl.st.dataModel);
			
			if ( mw.servers != null ) {
				int count = 0;
				for (ChivServer cs : mw.servers){
					count += Integer.parseInt(cs.mCurrentPlayers);
				}
				System.out.println("Retrieved " + mw.servers.size() + " servers with " + count + " players playing.");
				mw.printlnMC("Retrieved " + mw.servers.size() + " servers with " + count + " players playing.");
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
	
	public void stopRefreshing(MainWindow mw) {
		Vector<ChivServer> serversClone;
		msq.stopRefreshing(mw);
		if ( mw.servers != null ) {
			serversClone = mw.deepCopyCSVector(mw.servers);
			int count = 0;
			for (ChivServer cs : serversClone){
				count += Integer.parseInt(cs.mCurrentPlayers);
			}
			System.out.println("Retrieved " + serversClone.size() + " servers with " + count + " players playing.");
			mw.printlnMC("Retrieved " + serversClone.size() + " servers with " + count + " players playing.");
			mw.updateServerDB(serversClone);
		} else {
			System.out.println("Retrieved 0 servers.");
			mw.printlnMC("Retrieved 0 servers.");
		}
	}
	
	public boolean isRefreshing() {
		return !msq.pool.isShutdown();
	}
	
}