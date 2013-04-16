package com.tranek.chivalryserverbrowser;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;


public class QueryServerCondenser {
	
	public SourceServer server;
	public String port = "";
	public String ping = "";
	public String mIPaddress;
	public int mPort;
	public HashMap<String, SteamPlayer> mPlayers;
	public String mMaxPlayers;
	public String mCurrentPlayers;
	public String mHasPassword;
	public String mMinRank;
	public String mMaxRank;
	public String mMap;
	public String mName;
	public String mPerspective;
	
	public QueryServerCondenser(String ipaddr, int port) {
		mIPaddress = ipaddr;
		mPort = port;
	}
	
	public HashMap<String, String> getInfo() {
		
		try {
			server = new SourceServer(InetAddress.getAllByName(mIPaddress)[0], mPort);
			server.initialize();

			port = "" + server.getServerInfo().get("serverPort");					
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
		ret.put("gameport", port);
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
		ret.put("queryport", "" + mPort);
		
		return ret;
	}

}
