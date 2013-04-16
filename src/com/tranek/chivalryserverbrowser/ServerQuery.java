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
 * Queries a single Chivalry: Medieval Warfare server for its information,
 * game rules, or players.
 *
 */
public class ServerQuery {
	/** The IP address of the server. */
	private String ip;
	/** The queryport of the server. */
	private int queryport;
	/** The SteamCondenser SourceServer. */
	private SourceServer server;
	
	/**
	 * Creates a new ServerQuery from a ChivServer.
	 * 
	 * @param cs the ChivServer to get information about
	 */
	public ServerQuery(ChivServer cs) {
		ip = cs.mIP;
		queryport = Integer.parseInt(cs.mQueryPort);
	}
	
	/**
	 * Creates a new ServerQuery from an IP address and queryport.
	 * 
	 * @param ip the IP address of the server
	 * @param queryport the queryport of the server
	 */
	public ServerQuery(String ip, int queryport) {
		this.ip = ip;
		this.queryport = queryport;
	}
	
	/**
	 * Gets the currently connected players, their scores, and their time connected.
	 * 
	 * @return a HashMap of the {@link SteamPlayer} connected
	 */
	public HashMap<String, SteamPlayer> getPlayers() {
		try {
			server = new SourceServer(InetAddress.getAllByName(ip)[0], queryport);
			HashMap<String, SteamPlayer> players = server.getPlayers();
			return players;
		} catch (NumberFormatException | UnknownHostException
				| SteamCondenserException | TimeoutException e) {}
		return null;
	}
	
	/**
	 * Gets the information of the server.
	 * 
	 * @return a HashMap of the server's information
	 */
	public HashMap<String, Object> getInfo() {
		try {
			server = new SourceServer(InetAddress.getAllByName(ip)[0], queryport);
			HashMap<String, Object> info = server.getServerInfo();
			return info;
		} catch (NumberFormatException | UnknownHostException
				| SteamCondenserException | TimeoutException e) {}
		return null;
	}
	
	/**
	 * Gets the game rules for the server.
	 * 
	 * @return a HashMap of the game rules
	 */
	public HashMap<String, String> getRules() {
		try {
			server = new SourceServer(InetAddress.getAllByName(ip)[0], queryport);
			HashMap<String, String> rules = server.getRules();
			return rules;
		} catch (NumberFormatException | UnknownHostException
				| SteamCondenserException | TimeoutException e) {}
		return null;
	}
	
}
