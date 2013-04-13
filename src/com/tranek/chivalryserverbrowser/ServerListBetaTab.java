package com.tranek.chivalryserverbrowser;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

// B at the end of variable names denotes Beta
@SuppressWarnings("serial")
public class ServerListBetaTab extends JPanel implements ServerList {
	private final MainWindow mw;
	public ServerFilters sfB = new ServerFilters();
	public RefresherBeta refresherB;
	public FiltersPanel fp;
	public ServerTable st;

	public ServerListBetaTab(final MainWindow mw) {
		super();
		this.mw = mw;
		initialize();
	}
		
	public void initialize() {
		setLayout(new BorderLayout(0, 0));
		st = new ServerTable(this);
		add(st, BorderLayout.NORTH);
		
		fp = new FiltersPanel(this);
		add(fp);
	}
	
	// Don't want this on the UI thread
	@SuppressWarnings("deprecation")
	public void RefreshServers() {
		
		if ( refresherB != null) {
			refresherB.stop();
			refresherB.stopRefreshing(mw);
		}
		
		updateFilters();
		
		((DefaultTableModel)st.dataModel).setRowCount(0);
		
		mw.serversB = new Vector<ChivServer>();
		
		refresherB = new RefresherBeta(mw, this);
		refresherB.start();
	}

	public void updateFilters() {
		sfB.name = fp.serverNameFilter.getText();
		sfB.type = (String) fp.gameModeList.getSelectedItem();
		if ( !fp.maxPingFilter.getText().equals("") ) {
			sfB.maxPing = Integer.parseInt(fp.maxPingFilter.getText());
		} else {
			sfB.maxPing = -1;
		}
		sfB.hidePassword = fp.chckbxHidePasswordedServers.isSelected();
		sfB.hideEmpty = fp.chckbxHideEmptyServers.isSelected();
		sfB.hideFull = fp.chckbxHideFullServers.isSelected();
		if ( !fp.minRankFilter.getText().equals("") ) {
			sfB.minRank = Integer.parseInt(fp.minRankFilter.getText());
		} else {
			sfB.minRank = -1;
		}
		if ( !fp.maxRankFilter.getText().equals("") ) {
			sfB.maxRank = Integer.parseInt(fp.maxRankFilter.getText());
		} else {
			sfB.maxRank = -1;
		}
		sfB.officialservers = fp.chckbxOfficialServersOnly.isSelected();
		sfB.perspective = fp.cBPerspective.getSelectedIndex();
		sfB.numThreads = (int) fp.spNumThreads.getValue();
	}
	
	public void setUIFilters(String name, String type, int minrank, int maxrank,
			int maxping, boolean hidepassword, boolean hideempty, boolean hidefull,
			boolean officialonly, int perspective, int numthreads) {
		fp.serverNameFilter.setText(name);
		if ( type.equals("All") ) {
			type = "ALL";
		}
		fp.gameModeList.setSelectedItem(type);
		if ( minrank > -1 ) {
			fp.minRankFilter.setText("" + minrank);
		}
		if ( maxrank > -1 ) {
			fp.maxRankFilter.setText("" + maxrank);
		}
		if ( maxping > -1 ) {
			fp.maxPingFilter.setText("" + maxping);
		}
		if ( hidepassword ) {
			fp.chckbxHidePasswordedServers.setSelected(true);
		}
		if ( hideempty ) {
			fp.chckbxHideEmptyServers.setSelected(true);
		}
		if ( hidefull ) {
			fp.chckbxHideFullServers.setSelected(true);
		}
		if ( officialonly ) {
			fp.chckbxOfficialServersOnly.setSelected(true);
			fp.serverNameFilter.setText("");
			fp.serverNameFilter.setEnabled(false);
		}
		fp.cBPerspective.setSelectedIndex(perspective);
		fp.spNumThreads.setValue(numthreads);
	}
	
	public void loadFilters() {
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS filters_beta" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"name varchar(255) not null default ''," +
					"type varchar(255) not null default ''," +
					"minrank int not null default -1," +
					"maxrank int not null default -1," +
					"maxping int not null default -1," +
					"hidepassword int not null default 0," +
					"hideempty int not null default 0," +
					"hidefull int not null default 0," +
					"officialonly int not null default 0," +
					"perspective int not null default 0," +
					"numthreads int not null default 0" +
					")");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			boolean hasEntry = false;
			st = db.prepare("SELECT COUNT(*) FROM filters_beta");
			try {
				st.step();
				if ( st.columnInt(0) > 0 ) {
					hasEntry = true;
				}
			} finally {
				st.dispose();
			}

			if ( hasEntry ) {
				String name = "";
				String type = "";
				int minrank = -1;
				int maxrank = -1;
				int maxping = -1;
				boolean hidepassword = false;
				boolean hideempty = false;
				boolean hidefull = false;
				boolean officialonly = false;
				int perspective = 0;
				int numthreads = 8;
				st = db.prepare("SELECT * FROM filters_beta WHERE id=1");
				try {
					st.step();
					name = st.columnString(1);
					type = st.columnString(2);
					minrank = st.columnInt(3);
					maxrank = st.columnInt(4);
					maxping = st.columnInt(5);
					if ( st.columnInt(6) == 1 ) {
						hidepassword = true;
					}
					if ( st.columnInt(7) == 1 ) {
						hideempty = true;
					}
					if ( st.columnInt(8) == 1 ) {
						hidefull = true;
					}
					if ( st.columnInt(9) == 1 ) {
						officialonly = true;
					}
					perspective = st.columnInt(10);
					numthreads = st.columnInt(11);
					
					setUIFilters(name, type, minrank, maxrank, maxping, hidepassword, hideempty, hidefull,
							officialonly, perspective, numthreads);
				} finally {
					st.dispose();
				}
			} else {
				System.out.println("No entries");
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
	}

	public void saveFilters() {
		updateFilters();
		
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS filters_beta" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"name varchar(255) not null default ''," +
					"type varchar(255) not null default ''," +
					"minrank int not null default -1," +
					"maxrank int not null default -1," +
					"maxping int not null default -1," +
					"hidepassword int not null default 0," +
					"hideempty int not null default 0," +
					"hidefull int not null default 0," +
					"officialonly int not null default 0," +
					"perspective int not null default 0," +
					"numthreads int not null default 0" +
					")");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			int password = 0;
			if ( sfB.hidePassword ) {
				password = 1;
			}
			int empty = 0;
			if ( sfB.hideEmpty ) {
				empty = 1;
			}
			int full = 0;
			if ( sfB.hideFull ) {
				full = 1;
			}
			int official = 0;
			if ( sfB.officialservers ) {
				official = 1;
			}
			
			st = db.prepare("INSERT OR REPLACE INTO filters_beta (id, name, type, minrank, maxrank, maxping, " +
					"hidepassword, hideempty, hidefull, officialonly, perspective, numthreads) " +
					"VALUES (  1, '" + sfB.name + "', '" + sfB.type + "', " + sfB.minRank + ", " + sfB.maxRank +
					", " + sfB.maxPing + ", " + password + ", " + empty + ", " + full + ", " + official + ", " +
					sfB.perspective + ", " + sfB.numThreads + ")");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
	}

	@Override
	public MainWindow getMW() {
		return mw;
	}

	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversB;
	}

	@Override
	public boolean isRefreshing() {
		return refresherB.isRefreshing();
	}
	
}
