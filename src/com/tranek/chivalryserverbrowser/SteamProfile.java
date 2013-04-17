package com.tranek.chivalryserverbrowser;
import java.util.ArrayList;

/**
 * 
 * A data structure class containing information about a specific Steam profile.
 * It has no functionality other than containing data.
 *
 */
public class SteamProfile {
	
	/** The URL of the Steam Community profile page of the user. */
	protected String url;
	/** The Steam nickname of the user. */
	protected String nickname;
	/** The Steam online status message of the user. */
	protected String onlineStatus;
	/** The user's Steam friends. */
	protected ArrayList<SteamProfile> friends = new ArrayList<SteamProfile>();
	
	/**
	 * Creates a new SteamProfile without a status message. This is used
	 * for the user's SteamProfile since his/her status is irrelevant.
	 * 
	 * @param url the URL to the Steam Community page
	 * @param nickname the Steam nickname of the profile
	 */
	public SteamProfile(String url, String nickname) {
		this.url = url;
		this.nickname = nickname;
	}
	
	/**
	 * Creates a new SteamProfile.
	 * 
	 * @param url the URL to the Steam Community page
	 * @param nickname nickname the Steam nickname of the profile
	 * @param onlineStatus the Steam profile's online status
	 */
	public SteamProfile(String url, String nickname, String onlineStatus) {
		this.url = url;
		this.nickname = nickname;
		this.onlineStatus = onlineStatus;
	}
	
}
