package com.tranek.chivalryserverbrowser;
import java.util.ArrayList;


public class SteamProfile {

	public String url;
	public String nickname;
	public String onlineStatus;
	public ArrayList<SteamProfile> friends = new ArrayList<SteamProfile>();
	
	public SteamProfile(String url, String nickname) {
		this.url = url;
		this.nickname = nickname;
	}
	
	public SteamProfile(String url, String nickname, String onlineStatus) {
		this.url = url;
		this.nickname = nickname;
		this.onlineStatus = onlineStatus;
	}
	
}
