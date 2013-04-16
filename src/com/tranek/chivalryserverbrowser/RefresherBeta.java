package com.tranek.chivalryserverbrowser;
import javax.swing.table.DefaultTableModel;

public class RefresherBeta extends Thread {
	
	private MainWindow mw;
	private MasterServerQueryBeta msq;
	private ServerListBetaTab sl;
	
	public RefresherBeta(MainWindow mw, ServerListBetaTab slt) {
		this.mw = mw;
		this.sl = slt;
		msq = new MasterServerQueryBeta(this.mw, sl.sf);
	}
	
	public void run() {
		try {
			System.out.println("Refreshing beta servers...");
			String mctext = mw.messageCenter.getText();
			if ( mctext.equals("") ) {
				mw.printMC("Refreshing beta servers...");
			} else {
				mw.printlnMC("Refreshing beta servers...");
			}
			
			msq.queryServers(sl.sf, (DefaultTableModel)sl.st.dataModel);
			
			if ( mw.serversBeta != null ) {
				int count = 0;
				for (ChivServer cs : mw.serversBeta){
					count += Integer.parseInt(cs.mCurrentPlayers);
				}
				System.out.println("Retrieved " + mw.serversBeta.size() + " beta servers with " + count + " players playing.");
				mw.printlnMC("Retrieved " + mw.serversBeta.size() + " beta servers with " + count + " players playing.");
			} else {
				System.out.println("Retrieved 0 beta servers.");
				mw.printlnMC("Retrieved 0 beta servers.");
			}
			
			System.out.println("Finished refreshing beta servers.");
			mw.printlnMC("Finished refreshing beta servers.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopRefreshing(MainWindow mw) {
		msq.stopRefreshing(mw);
		if ( mw.serversBeta != null ) {
			int count = 0;
			for (ChivServer cs : mw.serversBeta){
				count += Integer.parseInt(cs.mCurrentPlayers);
			}
			System.out.println("Retrieved " + mw.serversBeta.size() + " beta servers with " + count + " players playing.");
			mw.printlnMC("Retrieved " + mw.serversBeta.size() + " beta servers with " + count + " players playing.");
		} else {
			System.out.println("Retrieved 0 servers.");
			mw.printlnMC("Retrieved 0 servers.");
		}
	}
	
	public boolean isRefreshing() {
		return !msq.pool.isShutdown();
	}
	
}