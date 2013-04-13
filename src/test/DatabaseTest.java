package test;
import java.io.File;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;


public class DatabaseTest {

	public static void main(String[] args) throws SQLiteException {

		SQLiteConnection db = new SQLiteConnection(new File("testdb"));
		db.open(true);
		
		SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS servers" +
				"(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"ipaddress varchar(255)," +
				"queryport int" +
				")");
		
		try {
			st.step();
		} finally {
			st.dispose();
		}
		
		//SQLiteStatement st2 = db.prepare("SHOW TABLES LIKE %servers%");
		SQLiteStatement st2 = db.prepare("SELECT name FROM sqlite_master WHERE type = \"table\"");
		String tables = "";
		try {
			//st2.bind(1, tables);
			st2.step();
			tables = st2.columnString(0);
		} finally {
			st2.dispose();
		}
		
		System.out.println(tables);
		
		st2 = db.prepare("INSERT INTO servers (ipaddress, queryport) VALUES ('123.456.789.123', 27015)");
		try {
			st2.step();
		} finally {
			st2.dispose();
		}
		
		st2 = db.prepare("SELECT COUNT(*) FROM servers");
		String count = "";
		try {
			st2.step();
			count = st2.columnString(0);
		} finally {
			st2.dispose();
		}
		
		System.out.println(count);
		
		db.dispose();
	}

}
