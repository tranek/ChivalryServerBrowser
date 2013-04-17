package com.tranek.chivalryserverbrowser;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;

/**
 * 
 * Queries an individual Chivalry server. This is somewhat duplicate code of ServerQuery.java.
 * This should really call ServerQuery, but I had issues with making it work. Might try again
 * later.
 *
 */
public class QueryServerCondenser {
	
	/** The SourceServer to query. Just uses its IP address and queryport. */
	public SourceServer server;
	/** The gameport. */
	public String gameport = "";
	/** Ping to the server. */
	public String ping = "";
	/** Server's IP address. */
	public String mIPaddress;
	/** Server's queryport. */
	public int queryport;
	/** Players currently on the server. */
	public HashMap<String, SteamPlayer> mPlayers;
	/** The server's maximum players. */
	public String mMaxPlayers;
	/** The number of players currently on the server. */
	public String mCurrentPlayers;
	/** If the server has a password. */
	public String mHasPassword;
	/** The server's minimum rank. */
	public String mMinRank;
	/** The server's maximum rank. */
	public String mMaxRank;
	/** The server's current map. */
	public String mMap;
	/** The server's name. */
	public String mName;
	/** The server's allowed player perspectives. */
	public String mPerspective;
	
	/**
	 * Creates a new QueryServerCondenser.
	 * 
	 * @param ipaddr the server's IP address
	 * @param queryport the server's queryport
	 */
	public QueryServerCondenser(String ipaddr, int queryport) {
		mIPaddress = ipaddr;
		this.queryport = queryport;
	}
	
	/**
	 * Queries the server for its information and game rules.
	 * 
	 * @return a HashMap of all of the server's information and game rules
	 * @see TimeoutException
	 * @see SteamCondenserException
	 * @see UnknownHostException
	 */
	public HashMap<String, String> getInfo() {
		
		try {
			server = new SourceServer(InetAddress.getAllByName(mIPaddress)[0], queryport);
			server.initialize();

			gameport = "" + server.getServerInfo().get("serverPort");					
			ping = "" + server.getPing();

			HashMap<String, Object> sinfo = server.getServerInfo();
			mMaxPlayers = "" + sinfo.get("maxPlayers");
			mCurrentPlayers = "" + sinfo.get("numberOfPlayers");
			mMap = "" + sinfo.get("mapName");
			mName = "" + sinfo.get("serverName");
			
			HashMap<String, String> rules = server.getRules();
			mHasPassword = rules.get(ChivServer.RULE_PASSWORD);
			mMinRank = rules.get(ChivServer.RULE_MIN_RANK);
			mMaxRank = rules.get(ChivServer.RULE_MAX_RANK);
			mPerspective = rules.get(ChivServer.RULE_PERSPECTIVE);
		} catch (TimeoutException | SteamCondenserException | UnknownHostException e) {}

		HashMap<String, String> ret = new HashMap<String, String>();
		ret.put("gameport", gameport);
		ret.put("ping", ping);
		ret.put("maxplayers", mMaxPlayers);
		ret.put("currentplayers", mCurrentPlayers);
		ret.put("haspassword", mHasPassword);
		ret.put("minrank", mMinRank);
		ret.put("maxrank", mMaxRank);
		ret.put("map", mMap);
		ret.put("name", mName);
		ret.put("perspective", mPerspective);
		ret.put("ip", mIPaddress);
		ret.put("queryport", "" + queryport);
		
		return ret;
	}

}
