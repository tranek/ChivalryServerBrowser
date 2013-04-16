package com.tranek.chivalryserverbrowser;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class MasterServerQueryHistory extends MasterServerQuery {

	
	public MasterServerQueryHistory(MainWindow mw, ServerFilters sf) {
		super(mw, sf);
	}
	
	@Override
	public Vector<ChivServer> getServers() throws IOException {
		return getHistory();
	}
	
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversHist;
	}
	
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
