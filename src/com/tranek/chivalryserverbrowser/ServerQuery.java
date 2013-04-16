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
	/** 
	 * The ChivServer to be queried. This can be used for the basic ChivServer data structure
	 * or used to refresh this ChivServer's info.
	 */
	private ChivServer cs;
	/** The SteamCondenser SourceServer. */
	private SourceServer server;
	
	/**
	 * Creates a new ServerQuery.
	 * 
	 * @param cs the ChivServer to get information about
	 */
	public ServerQuery(ChivServer cs) {
		this.cs = cs;
	}
	
	/**
	 * Gets the currently connected players, their scores, and their time connected.
	 * 
	 * @return a HashMap of the {@link SteamPlayer} connected
	 */
	public HashMap<String, SteamPlayer> getPlayers() {
		try {
			server = new SourceServer(InetAddress.getAllByName(cs.mIP)[0], Integer.parseInt(cs.mQueryPort));
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
			server = new SourceServer(InetAddress.getAllByName(cs.mIP)[0], Integer.parseInt(cs.mQueryPort));
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
			server = new SourceServer(InetAddress.getAllByName(cs.mIP)[0], Integer.parseInt(cs.mQueryPort));
			HashMap<String, String> rules = server.getRules();
			return rules;
		} catch (NumberFormatException | UnknownHostException
				| SteamCondenserException | TimeoutException e) {}
		return null;
	}
	
}
