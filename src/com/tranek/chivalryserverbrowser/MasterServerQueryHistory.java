package com.tranek.chivalryserverbrowser;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

/**
 * 
 * Controls the low level refreshing for recently joined (history) servers.
 *
 */
public class MasterServerQueryHistory extends MasterServerQuery {

	/**
	 * Creates a new MasterServerQueryHistory
	 * 
	 * @param mw the MainWindow
	 * @param sf the server filters for querying the servers
	 */
	public MasterServerQueryHistory(MainWindow mw, ServerFilters sf) {
		super(mw, sf);
	}
	
	/**
	 * Calls {@link #getHistory()} to get the server history.
	 * 
	 * @see #getHistory()
	 */
	@Override
	public Vector<ChivServer> getServers() throws IOException {
		return getHistory();
	}
	
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversHist;
	}
	
	/**
	 * Gets server history from the local database.
	 * 
	 * @return a {@link Vector} of the {@link ChivServer}
	 * @see ChivServer
	 * @see SQLiteConnection
	 * @see SQLiteStatement
	 * @see SQLiteException
	 */
	public Vector<ChivServer> getHistory() {
		Vector<ChivServer> serversHist = new Vector<ChivServer>();
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS server_history" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"name varchar(255) not null default ''," +
					"ip varchar(255) not null default ''," +
					"port varchar(255) not null default '' )");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			st = db.prepare("SELECT * FROM server_history");
			try {
				while (st.step()) {
					String name = st.columnString(1);
					String ip = st.columnString(2);
					String queryport = st.columnString(3);
					serversHist.add(new ChivServer(name, ip, queryport));
				}
			} finally {
				st.dispose();
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
		return serversHist;
	}
	
}
