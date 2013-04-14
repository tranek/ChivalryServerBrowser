package com.tranek.chivalryserverbrowser;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 * 
 * Represents a Chivalry: Medieval Warfare server.
 *
 */
public class ChivServer {

	public String mName;
	public String mIP;
	public String mQueryPort;
	public String mGamePort;
	public String mMap;
	public String mGameMode;
	public String mPing;
	public String mMaxPlayers;
	public String mCurrentPlayers;
	public String mHasPassword;
	public String mMinRank;
	public String mMaxRank;
	public String mLocation;
	public String mPerspective;
	public String mLatitude;
	public String mLongitude;
	
	public ChivServer(String name, String ip, String queryport, String gameport, String map,
			String gamemode, String ping, String maxplayers, String currentplayers, 
			String haspassword, String minrank, String maxrank, String location, String perspective,
			String lat, String lon) {
		mName = name;
		mIP = ip;
		mQueryPort = queryport;
		mGamePort = gameport;
		mMap = map;
		mGameMode = gamemode;
		mPing = ping;
		mMaxPlayers = maxplayers;
		mCurrentPlayers = currentplayers;
		mHasPassword = haspassword;
		mMinRank = minrank;
		mMaxRank = maxrank;
		mLocation = location;
		mPerspective = perspective;
		mLatitude = lat;
		mLongitude = lon;
	}
	
	public ChivServer(String name, String ip, String queryport) {
		mName = name;
		mIP = ip;
		mQueryPort = queryport;
	}
	
	public static ChivServer createChivServer(MainWindow mw, String ip, int queryport) {
		QueryServerCondenser qsc = new QueryServerCondenser(ip, queryport);
		HashMap<String, String> info = qsc.getInfo();
		String location = "";
		String lat = "";
		String lon = "";
		LocationRIPE l = new LocationRIPE();
		HashMap<String, String> loc = l.getLocation(ip);
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
			Location l2 = new Location();
			loc = l2.getLocation(ip);
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
		
		if ( lat.equals("") || lon.equals("") ) {
			HashMap<String, String> lfos = getLocFromOtherServer(location, mw.servers);
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
		return new ChivServer(info.get("name"), ip, "" + queryport, info.get("gameport"),
				info.get("map"), info.get("gamemode"), info.get("ping"), info.get("maxplayers"),
				info.get("currentplayers"), info.get("haspassword"), info.get("minrank"),
				info.get("maxrank"), location, info.get("perspective"), lat, lon);
	}
	
	@Override
	public ChivServer clone() {
		return new ChivServer(mName, mIP, mQueryPort, mGamePort, mMap, mGameMode, mPing,
				mMaxPlayers, mCurrentPlayers, mHasPassword, mMinRank, mMaxRank, mLocation,
				mPerspective, mLatitude, mLongitude);
	}
	
	public synchronized static HashMap<String, String> getLocFromOtherServer(String location, Vector<ChivServer> servers) {
		for ( ChivServer c : servers ) {
			if ( !c.mLatitude.equals("") && !c.mLongitude.equals("") && c.mLocation.equals(location)) {
				//cs.mLatitude = c.mLatitude;
				//cs.mLongitude = c.mLongitude;
				HashMap<String, String> result = new HashMap<String, String>();
				result.put("latitude", c.mLatitude);
				result.put("longitude", c.mLongitude);
				return result;
			}
		}
		return null;
	}
	
}
