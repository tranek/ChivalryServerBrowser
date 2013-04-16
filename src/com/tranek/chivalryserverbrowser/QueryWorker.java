package com.tranek.chivalryserverbrowser;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

class QueryWorker implements Callable<ChivServer> {
	private ChivServer cs;
	private String sip;
	private int sport;
	private ServerFilters sf;
	public ExecutorService pool;
	QueryWorkerSynch synch;
	
	public QueryWorker(String ip, int port, ServerFilters sf, QueryWorkerSynch synch, ExecutorService pool) {
		sip = ip;
		sport = port;
		this.sf = sf;
		this.synch = synch;
		this.pool = pool;
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
			
			String gamemode = ChivServer.getGameMode(map);
			
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
				HashMap<String, String> lfos = synch.getLocFromOtherServer(cs.mLocation);
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
					sName.substring(0, 18).equals("official to server") ||
					sName.substring(0, 20).equals("official beta server") ) ) {
					
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