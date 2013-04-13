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
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		}
		try {
			server.initialize();
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {}
		try {
			port = "" + server.getServerInfo().get("serverPort");					
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {}
		try {
			ping = "" + server.getPing();
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {}
		
		try {
			HashMap<String, Object> sinfo = server.getServerInfo();
			mMaxPlayers = "" + sinfo.get("maxPlayers");
			mCurrentPlayers = "" + sinfo.get("numberOfPlayers");
			mMap = "" + sinfo.get("mapName");
			mName = "" + sinfo.get("serverName");
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {}
		
		try {
			HashMap<String, String> rules = server.getRules();
			mHasPassword = rules.get("p1342177286");
			mMinRank = rules.get("p1342177292");
			mMaxRank = rules.get("p1342177293");
			mPerspective = rules.get("p1342177291");
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {}

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
