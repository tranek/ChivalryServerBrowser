package com.tranek.chivalryserverbrowser;
import javax.swing.table.DefaultTableModel;

public class RefresherFav extends Thread {
		
	private MainWindow mw;
	private MasterServerQueryFav msq;
	private ServerListFavTab sl;
	
	public RefresherFav(MainWindow mw, ServerListFavTab slt) {
		this.mw = mw;
		this.sl = slt;
		msq = new MasterServerQueryFav(this.mw, sl.sf);
	}
	
	public void run() {
		try {
			System.out.println("Refreshing favorite servers...");
			String mctext = mw.messageCenter.getText();
			if ( mctext.equals("") ) {
				mw.printMC("Refreshing favorite servers...");
			} else {
				mw.printlnMC("Refreshing favorite servers...");
			}
			
			msq.queryMasterServer(sl.sf, (DefaultTableModel)sl.st.dataModel);
			
			if ( mw.serversFav != null ) {
				int count = 0;
				for (ChivServer cs : mw.serversFav){
					count += Integer.parseInt(cs.mCurrentPlayers);
				}
				System.out.println("Retrieved " + mw.serversFav.size() + " favorite servers with " + count + " players playing.");
				mw.printlnMC("Retrieved " + mw.serversFav.size() + " favorite servers with " + count + " players playing.");
			} else {
				System.out.println("Retrieved 0 favorite servers.");
				mw.printlnMC("Retrieved 0 favorite servers.");
			}
			
			System.out.println("Finished refreshing favorite servers.");
			mw.printlnMC("Finished refreshing favorite servers.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopRefreshing(MainWindow mw) {
		msq.stopRefreshing(mw);
		if ( mw.serversFav != null ) {
			int count = 0;
			for (ChivServer cs : mw.serversFav){
				count += Integer.parseInt(cs.mCurrentPlayers);
			}
			System.out.println("Retrieved " + mw.serversFav.size() + " favorite servers with " + count + " players playing.");
			mw.printlnMC("Retrieved " + mw.serversFav.size() + " favorite servers with " + count + " players playing.");
		} else {
			System.out.println("Retrieved 0 favorite servers.");
			mw.printlnMC("Retrieved 0 favorite servers.");
		}
	}
	
	public boolean isRefreshing() {
		return !msq.pool.isShutdown();
	}
	
}