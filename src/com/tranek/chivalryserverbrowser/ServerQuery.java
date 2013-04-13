package com.tranek.chivalryserverbrowser;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;


public class ServerQuery {
	private ChivServer cs;
	private SourceServer server;
	
	public ServerQuery(ChivServer cs) {
		this.cs = cs;
	}
	
	public HashMap<String, SteamPlayer> getPlayers() {
		try {
			server = new SourceServer(InetAddress.getAllByName(cs.mIP)[0], Integer.parseInt(cs.mQueryPort));
			HashMap<String, SteamPlayer> players = server.getPlayers();
			return players;
		} catch (NumberFormatException | UnknownHostException
				| SteamCondenserException | TimeoutException e) {}
		return null;
	}
	
	public HashMap<String, Object> getInfo() {
		try {
			server = new SourceServer(InetAddress.getAllByName(cs.mIP)[0], Integer.parseInt(cs.mQueryPort));
			HashMap<String, Object> info = server.getServerInfo();
			return info;
		} catch (NumberFormatException | UnknownHostException
				| SteamCondenserException | TimeoutException e) {}
		return null;
	}
	
	public HashMap<String, String> getRules() {
		try {
			server = new SourceServer(InetAddress.getAllByName(cs.mIP)[0], Integer.parseInt(cs.mQueryPort));
			HashMap<String, String> rules = server.getRules();
			return rules;
		} catch (NumberFormatException | UnknownHostException
				| SteamCondenserException | TimeoutException e) {}
		return null;
	}
	
}
