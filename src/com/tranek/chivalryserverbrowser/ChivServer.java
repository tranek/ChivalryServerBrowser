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

	/** The server's name. */
	protected String mName;
	/** The server's IP address. */
	protected String mIP;
	/** The server's queryport. */
	protected String mQueryPort;
	/** The server's gameport. */
	protected String mGamePort;
	/** The server's map. */
	protected String mMap;
	/** The server's game mode. */
	protected String mGameMode;
	/** The user's ping to the server. */
	protected String mPing;
	/** The server's maximum players. */
	protected String mMaxPlayers;
	/** The server's current players. */
	protected String mCurrentPlayers;
	/** Whether or not the server has a password. */
	protected String mHasPassword;
	/** The server's minimum rank. */
	protected String mMinRank;
	/** The server's maximum rank. */
	protected String mMaxRank;
	/** The server's location. */
	protected String mLocation;
	/** The server's allowed player perspective. */
	protected String mPerspective;
	/** The server's latitude. */
	protected String mLatitude;
	/** The server's longitude. */
	protected String mLongitude;
	
	/**
	 * Creates a new {@link ChivServer} by directly specifying all of the member fields.
	 * 
	 * @param name the server's name
	 * @param ip the server's IP address
	 * @param queryport the server's queryport
	 * @param gameport the server's gameport
	 * @param map the server's map
	 * @param gamemode the server's game mode
	 * @param ping the user's ping to the server
	 * @param maxplayers the server's max ping
	 * @param currentplayers the server's current players
	 * @param haspassword whether or not the server has a password
	 * @param minrank the server's minimum rank
	 * @param maxrank the server's maximum rank
	 * @param location the server's location
	 * @param perspective the server's allowed player perspective
	 * @param lat the server's latitude
	 * @param lon the server's longitude
	 */
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
	
	/**
	 * Creates a new {@link ChivServer} with only a name, ip address, and queryport.
	 * 
	 * @param name the server's name
	 * @param ip the server's ip address
	 * @param queryport the server's queryport
	 */
	public ChivServer(String name, String ip, String queryport) {
		mName = name;
		mIP = ip;
		mQueryPort = queryport;
	}
	
	/**
	 * Creates a new {@link ChivServer} with only a name, ip address, queryport, and map.
	 * 
	 * @param name the server's name
	 * @param ip the server's ip address
	 * @param queryport the server's queryport
	 * @param map the server's map
	 */
	public ChivServer(String name, String ip, String queryport, String map) {
		mName = name;
		mIP = ip;
		mQueryPort = queryport;
		mMap = map;
	}
	
	/**
	 * Static method to create a new {@link ChivServer} by querying the server directly
	 * from its IP address and queryport.
	 * 
	 * @param mw reference to the MainWindow to have access to queried servers
	 * @param ip the server's IP address
	 * @param queryport the server's queryport
	 * @return a new {@link ChivServer}
	 * @see QueryServerCondenser#getInfo()
	 * @see LocationRIPE#getLocation(String)
	 * @see #getLocFromOtherServer(String, Vector)
	 */
	public static ChivServer createChivServer(MainWindow mw, String ip, int queryport) {
		QueryServerCondenser qsc = new QueryServerCondenser(ip, queryport);
		HashMap<String, String> info = qsc.getInfo();
		String location = "";
		String lat = "";
		String lon = "";		
		HashMap<String, String> loc = getLocation(mw, ip);
		location = loc.get("location");
		lat = loc.get("latitude");
		lon = loc.get("longitude");
		
		String gamemode = getGameMode(info.get("map"));
		
		return new ChivServer(info.get("name"), ip, "" + queryport, info.get("gameport"),
				info.get("map"), gamemode, info.get("ping"), info.get("maxplayers"),
				info.get("currentplayers"), info.get("haspassword"), info.get("minrank"),
				info.get("maxrank"), location, info.get("perspective"), lat, lon);
	}
	
	/**
	 * Creates a new clone of this {@link ChivServer}.
	 */
	@Override
	public ChivServer clone() {
		return new ChivServer(mName, mIP, mQueryPort, mGamePort, mMap, mGameMode, mPing,
				mMaxPlayers, mCurrentPlayers, mHasPassword, mMinRank, mMaxRank, mLocation,
				mPerspective, mLatitude, mLongitude);
	}

	/**
	 * Gets the location for an IP address. It also adds a slight bit of randomness to its
	 * latitude and longitude so that multiple servers in a single data center don't overlap
	 * their markers on the map.
	 * 
	 * @param ip the IP address for the server
	 * @return a HashMap of the location, latitude, and longitude
	 */
	public static HashMap<String, String> getLocation(MainWindow mw, String ip) {
		HashMap<String, String> result = new HashMap<String, String>();
		
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
			LocationARIN l2 = new LocationARIN();
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
		
		result.put("location", location);
		result.put("latitude", lat);
		result.put("longitude", lon);
		return result;
	}
	
	/**
	 * Checks if another server with the same location has values for latitude and longitude.
	 * If another such server exists, it copies those values from it for itself. This is
	 * needed because the RIPE geolocation service sometimes doesn't return a latitude and
	 *  longitude for some addresses.
	 * 
	 * @param location the {@link ChivServer} location
	 * @param servers the list of {@link ChivServer} to check
	 * @return a {@link HashMap} of the latitude and longitude; or null if no other server
	 * with the same location exists with latitude and longitude data
	 */
	public synchronized static HashMap<String, String> getLocFromOtherServer(String location, Vector<ChivServer> servers) {
		for ( ChivServer c : servers ) {
			if ( !c.mLatitude.equals("") && !c.mLongitude.equals("") && c.mLocation.equals(location)) {
				HashMap<String, String> result = new HashMap<String, String>();
				result.put("latitude", c.mLatitude);
				result.put("longitude", c.mLongitude);
				return result;
			}
		}
		return null;
	}
	
	/**
	 * Gets the game mode for a map based on its prefix.
	 * 
	 * @param mapname the map name
	 * @return the game mode as a string
	 */
	public static String getGameMode(String mapname) {
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
	
}
