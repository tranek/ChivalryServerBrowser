package com.tranek.chivalryserverbrowser;
import javax.swing.table.DefaultTableModel;

public class RefresherHistory extends Thread {
	
	private MainWindow mw;
	private MasterServerQueryHistory msq;
	private ServerListHistoryTab sl;
	
	public RefresherHistory(MainWindow mw, ServerListHistoryTab slt) {
		this.mw = mw;
		this.sl = slt;
		msq = new MasterServerQueryHistory(this.mw, sl.sf);
	}
	
	public void run() {
		try {
			System.out.println("Refreshing recently visited servers...");
			String mctext = mw.messageCenter.getText();
			if ( mctext.equals("") ) {
				mw.printMC("Refreshing recently visited servers...");
			} else {
				mw.printlnMC("Refreshing recently visited servers...");
			}
			
			msq.queryMasterServer(sl.sf, (DefaultTableModel)sl.st.dataModel);
			
			if ( mw.serversHist != null ) {
				int count = 0;
				for (ChivServer cs : mw.serversHist){
					count += Integer.parseInt(cs.mCurrentPlayers);
				}
				System.out.println("Retrieved " + mw.serversFav.size() + " recently visited servers with " + count + " players playing.");
				mw.printlnMC("Retrieved " + mw.serversFav.size() + " recently visited servers with " + count + " players playing.");
			} else {
				System.out.println("Retrieved 0 recently visited servers.");
				mw.printlnMC("Retrieved 0 recently visited servers.");
			}
			
			System.out.println("Finished refreshing recently visited servers.");
			mw.printlnMC("Finished refreshing recently visited servers.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopRefreshing(MainWindow mw) {
		msq.stopRefreshing(mw);
		if ( mw.serversHist != null ) {
			int count = 0;
			for (ChivServer cs : mw.serversHist){
				count += Integer.parseInt(cs.mCurrentPlayers);
			}
			System.out.println("Retrieved " + mw.serversFav.size() + " recently visited servers with " + count + " players playing.");
			mw.printlnMC("Retrieved " + mw.serversFav.size() + " recently visited servers with " + count + " players playing.");
		} else {
			System.out.println("Retrieved 0 recently visited servers.");
			mw.printlnMC("Retrieved 0 recently visited servers.");
		}
	}
	
	public boolean isRefreshing() {
		return !msq.pool.isShutdown();
	}
	
}