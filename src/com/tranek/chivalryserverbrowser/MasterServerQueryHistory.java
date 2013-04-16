package com.tranek.chivalryserverbrowser;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.table.DefaultTableModel;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

public class MasterServerQueryHistory {
	
	private final MainWindow mw;
	private ServerFilters sf;
	public ExecutorService pool;
	private Synchronizer synch;
	public boolean terminated = false;
	public Vector<ChivServer> slist;
	
	public MasterServerQueryHistory(MainWindow mw, ServerFilters sf) {
		this.sf = sf;
		this.mw = mw;
	};
	
	public Vector<ChivServer> queryMasterServer(ServerFilters sf, DefaultTableModel dataModel) throws IOException, InterruptedException {
		this.sf = sf;
		slist = new Vector<ChivServer>();
		synch = new Synchronizer(dataModel, mw.serversHist, mw);
		pool = Executors.newFixedThreadPool(sf.numThreads);
		
		Vector<ChivServer> servers = getHistory();
		
		mw.printlnMC("Querying individual recent servers...");
		Set<Future<ChivServer>> set = new HashSet<Future<ChivServer>>();
		for (ChivServer cs : servers) {
			Callable<ChivServer> callable = new QueryWorker(cs.mIP, Integer.parseInt(cs.mQueryPort));
			Future<ChivServer> future = pool.submit(callable);
			set.add(future);
		}

		
		// Iterate through the results and compile into one list
	    for ( Future<ChivServer> future : set ) {

	    	if ( terminated ) {
	    		//break;
	    		if ( !future.cancel(true) ) {
	    			try {
						if ( future.get() != null ) {
							ChivServer cs = future.get();

							if ( cs.mName == null ) {
								continue;
							}
							
							String gametype = getGameType(cs.mMap);
							String serverNameFilter = sf.name.toLowerCase();
							String sName = cs.mName.toLowerCase();
							
							if ( sf.officialservers && ( sf.type.equals("All") || sf.type.equals(gametype) ) ) {
								if ( (sName.length() >  23) && ( sName.substring(0, 20).equals("official duel server") ||
									sName.substring(0, 23).equals("official classic server") ||
									sName.substring(0, 19).equals("official ffa server") ||
									sName.substring(0, 19).equals("official lts server") ||
									sName.substring(0, 19).equals("official tdm server") ||
									sName.substring(0, 18).equals("official to server") ) ) {
									
									slist.add(cs);
								}
							} else if ( cs.mName != null && cs.mName.toLowerCase().contains(serverNameFilter)
									&& ( sf.type.equals("All") || sf.type.equals(gametype) ) ) {
								slist.add(cs);
							}		
						}
					} catch (ExecutionException e) {
						//e.printStackTrace();
					}
	    		} else {
	    			continue;
	    		}	
	    	}
	    	try {
				if ( future.get() != null ) {		
					ChivServer cs = future.get();
					
					if ( cs.mName == null ) {
						continue;
					}
					
					String gametype = getGameType(cs.mMap);
					String serverNameFilter = sf.name.toLowerCase();
					String sName = cs.mName.toLowerCase();
					
					if ( sf.officialservers && ( sf.type.equals("All") || sf.type.equals(gametype) ) ) {
						if ( (sName.length() >  23) && ( sName.substring(0, 20).equals("official duel server") ||
							sName.substring(0, 23).equals("official classic server") ||
							sName.substring(0, 19).equals("official ffa server") ||
							sName.substring(0, 19).equals("official lts server") ||
							sName.substring(0, 19).equals("official tdm server") ||
							sName.substring(0, 18).equals("official to server") ) ) {
							
							slist.add(cs);
						}
					} else if ( cs.mName != null && cs.mName.toLowerCase().contains(serverNameFilter)
							&& ( sf.type.equals("All") || sf.type.equals(gametype) ) ) {
						slist.add(cs);
					}
				}
	    	}
	    	catch ( Exception e ) {
	    		//e.printStackTrace();
	    	}
	    }

		return slist;
	}
	
	// call the queryservercondenser, create the chivserver, add row to datamodel
	// returns to main thread to be added to a set, iterate set to add to slist
	// or just add to slist?
	private class QueryWorker implements Callable<ChivServer> {
		
		//private DefaultTableModel dataModel;
		//private SourceServer server;
		private ChivServer cs;
		private String sip;
		private int sport;

		/*public QueryWorker(DefaultTableModel datamodel, SourceServer server) {
			this.dataModel = datamodel;
			this.server = server;
		}*/
		
		/*public QueryWorker(SourceServer server) {
			this.server = server;
			sip = server.getIP();
			sport = server.getPort();
		}*/
		
		public QueryWorker(String ip, int port) {
			sip = ip;
			sport = port;
		}
		
		@Override
		public ChivServer call() throws Exception {
			QueryServerCondenser qsc = new QueryServerCondenser(sip, sport);
			HashMap<String, String> info = qsc.getInfo();

			// Check server filters, return null if it does not match
			// -1 is default (not set) for numbers
			if ( sf.hidePassword && info.get("haspassword").equals("1") ) {
				return null;
			}
			
			if ( sf.hideEmpty && Integer.parseInt(info.get("currentplayers")) <= 0 ) {
				return null;
			}
			
			if ( sf.hideFull && Integer.parseInt(info.get("currentplayers")) >=
					Integer.parseInt(info.get("maxplayers")) ) {
				return null;
			}
			
			if ( sf.maxPing > -1 && Integer.parseInt(info.get("ping")) > sf.maxPing ) {
				return null;
			}
			
			if ( sf.minRank > -1 && Integer.parseInt(info.get("minrank")) <= sf.minRank ) {
				return null;
			}
			
			if ( sf.maxRank > -1 && Integer.parseInt(info.get("maxrank")) >= sf.maxRank ) {
				return null;
			}
			
			String map = info.get("map");
			String name = info.get("name");
			
			// First person
			if ( sf.perspective == 1 && Integer.parseInt(info.get("perspective")) != 1 ) {
				return null;
			}
			
			//TODO check this.
			// Third person
			if ( sf.perspective == 2 && Integer.parseInt(info.get("perspective")) != 2 ) {
				return null;
			}
			
			// If we can get a game port, add it to our last (otherwise it might be down?)
			if(!info.get("gameport").equals("")) {
				
				String gamemode = getGameType(map);
				
				String location = "";
				String lat = "";
				String lon = "";
				LocationRIPE l = new LocationRIPE();
				HashMap<String, String> loc = l.getLocation(sip);
				if (loc != null) {
					String city = loc.get("city");
					String state = loc.get("state");
					String country = loc.get("country");
					if ( !country.equals("USA") ) {
						if ( !city.equals("") ) {
							city += ", ";
						}
					} else if ( !city.equals("") ) {
						city += " ";
					}
					if ( !state.equals("") ) {
						state += ", ";
					}
					location = city + state + country;
					lat = loc.get("latitude");
					lon = loc.get("longitude");
				}
				if ( location.equals("") ) {
					LocationARIN l2 = new LocationARIN();
					loc = l2.getLocation(sip);
					String city = loc.get("city");
					String state = loc.get("state");
					String country = loc.get("country");
					if ( !country.equals("USA") ) {
						if ( !city.equals("") ) {
							city += ", ";
						}
					} else if ( !city.equals("") ) {
						city += " ";
					}
					if ( !state.equals("") ) {
						state += ", ";
					}
					location = city + state + country;
				}
				
				//TODO propagate this to other master server queries
				if ( lat.equals("") || lon.equals("") ) {
					HashMap<String, String> lfos = synch.getLocFromOtherServer(cs);
					if ( lfos != null ) {
						lat = lfos.get("latitude");
						lon = lfos.get("longitude");
					}
				}
				
				if ( lat.equals("") || lon.equals("") ) {
					//TODO check db
				}
				
				if ( lat.equals("") || lon.equals("") ) {
					//TODO geolocate from google and store in db
					// "DE country"
				}
				
				if ( !lat.equals("") && !lon.equals("") ) {
					double latd = Double.parseDouble(lat);
					double lond = Double.parseDouble(lon);
					
					Random generator = new Random(System.currentTimeMillis());
					int signlat = generator.nextInt(1);
					int signlon = generator.nextInt(1);
					double latran = (generator.nextDouble() * 999 + 1) / 10000;
					double lonran = (generator.nextDouble() * 999 + 1) / 10000;
					
					if ( signlat == 1 ) {
						latd += latran;
					} else {
						latd -= latran;
					}
					
					if ( signlon == 1 ) {
						lond += lonran;
					} else {
						lond -= lonran;
					}
					
					lat = "" + latd;
					lon = "" + lond;
				}
				
				cs = new ChivServer(name, sip, "" + sport, info.get("gameport"), 
						map, gamemode, info.get("ping"), info.get("maxplayers"),
						info.get("currentplayers"), info.get("haspassword"), info.get("minrank"), 
						info.get("maxrank"), location, info.get("perspective"), lat, lon);
						
				String haspassword = "";
				if ( cs.mHasPassword != null && cs.mHasPassword.equals("1") ) {
					haspassword = "Yes";
				}
				
				// bandaid for possible null values from failed queries (can't Integer.parse on null)
				int ping = 0;
				if (cs.mPing != null && !cs.mPing.equals("")) {
					ping = Integer.parseInt(cs.mPing);
				}
				int minrank = 0;
				if (cs.mMinRank != null && !cs.mMinRank.equals("")) {
					minrank = Integer.parseInt(cs.mMinRank);
				}
				int maxrank = 0;
				if (cs.mMaxRank != null && !cs.mMaxRank.equals("")) {
					maxrank = Integer.parseInt(cs.mMaxRank);
				}
				
				Object[] rowData = {cs.mName, "<html><U><FONT COLOR=BLUE>" + cs.mIP + ":" + cs.mGamePort + "</FONT></U></html>",
						cs.mGameMode, cs.mMap, cs.mCurrentPlayers + " / " + cs.mMaxPlayers,
						ping, location, haspassword, minrank, maxrank};
							
				String serverNameFilter = sf.name.toLowerCase();
				String sName = cs.mName.toLowerCase();
				
				//Solution to stopping refreshing! yay.
				if ( pool.isShutdown() ) {
					return null;
				}
				
				if ( sf.officialservers && ( sf.type.equals("All") || sf.type.equals(gamemode) ) ) {
					if ( (sName.length() >  23) && ( sName.substring(0, 20).equals("official duel server") ||
						sName.substring(0, 23).equals("official classic server") ||
						sName.substring(0, 19).equals("official ffa server") ||
						sName.substring(0, 19).equals("official lts server") ||
						sName.substring(0, 19).equals("official tdm server") ||
						sName.substring(0, 18).equals("official to server") ) ) {
						
						synch.addToTable(rowData);
						synch.addToList(cs);
						synch.addToMap(cs);
					}
				} else if ( cs.mName != null && cs.mName.toLowerCase().contains(serverNameFilter)
						&& ( sf.type.equals("All") || sf.type.equals(gamemode) ) ) {
					synch.addToTable(rowData);
					synch.addToList(cs);
					synch.addToMap(cs);
				}
				
				return cs;
			}
			return null;
		}
	}
	
	public void stopRefreshing(MainWindow mw) {
		mw.printlnMC("Refreshing beta stopped.");
		if (pool != null) {
			pool.shutdownNow();
		}
		//pool.shutdown();
		terminated = true;
	}
	
	public String getGameType(String mapname) {
		String gamemode = "";
		String prefix = mapname.split("-")[0];
		if ( prefix.equals("aocffa") ) {
			gamemode = "FFA";
		} else if( prefix.equals("aocduel") ) {
			gamemode = "DUEL";
		} else if( prefix.equals("aockoth") ) {
			gamemode = "KOTH";
		} else if( prefix.equals("aocctf") ) {
			gamemode = "CTF";
		} else if( prefix.equals("aoclts") ) {
			gamemode = "LTS";
		} else if ( prefix.equals("aocto") ) {
			gamemode = "TO";
		} else if ( prefix.equals("aoctd") ) {
			gamemode = "TDM";
		}
		return gamemode;
	}
	
	public class Synchronizer {
		private final DefaultTableModel dataModel;
		private final Vector<ChivServer> servers;
		private final MainWindow mw;
		
		public Synchronizer(DefaultTableModel datamodel, Vector<ChivServer> servs, MainWindow mw) {
			this.dataModel = datamodel;
			this.servers = servs;
			this.mw = mw;
		}
		
		public synchronized void addToTable(Object[] rowData) {
			dataModel.addRow(rowData);
		}
		
		public void addToList(ChivServer cs) {
			servers.add(cs);
		}
		
		public synchronized HashMap<String, String> getLocFromOtherServer(ChivServer cs) {
			for ( ChivServer c : servers ) {
				if ( !c.mLatitude.equals("") && !c.mLongitude.equals("") && c.mLocation.equals(cs.mLocation)) {
					cs.mLatitude = c.mLatitude;
					cs.mLongitude = c.mLongitude;
					HashMap<String, String> result = new HashMap<String, String>();
					result.put("latitude", c.mLatitude);
					result.put("longitude", c.mLongitude);
					return result;
				}
			}
			return null;
		}
		
		public synchronized void addToMap(ChivServer cs) {
			if ( mw.chckbxServerHistory.isSelected() ) {
				mw.addMarker(mw.serverListHistoryTab, cs);
			}
		}
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
