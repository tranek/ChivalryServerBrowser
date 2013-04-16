package com.tranek.chivalryserverbrowser;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class MasterServerQueryFav extends MasterServerQuery {
	
	public MasterServerQueryFav(MainWindow mw, ServerFilters sf) {
		super(mw, sf);
	}
	
	@Override
	public Vector<ChivServer> getServers() throws IOException {
		return getFavorites();
	}
	
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversFav;
	}
	
	public Vector<ChivServer> getFavorites() {
		Vector<ChivServer> serversFav = new Vector<ChivServer>();
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS favorite_servers" +
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
			
			st = db.prepare("SELECT * FROM favorite_servers");
			try {
				while (st.step()) {
					String name = st.columnString(1);
					String ip = st.columnString(2);
					String queryport = st.columnString(3);
					serversFav.add(new ChivServer(name, ip, queryport));
				}
			} finally {
				st.dispose();
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
		return serversFav;
	}
	
}
