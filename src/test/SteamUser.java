package test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.github.koraktor.steamcondenser.steam.community.XMLData;

public class SteamUser {

	public static void main(String[] args) {
		/*try {
			SteamId id = SteamId.create("ReMixx", true, true);
			SteamId[] friends = id.getFriends();
			for ( SteamId si : friends ) {
				System.out.println(si.getNickname());
			}
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		}*/
		
		//String url = "http://steamcommunity.com/id/ReMixx";
		String url = "http://steamcommunity.com/profiles/76561197984752886"; // Chuck!
		try {
			XMLData profile = new XMLData(url + "?xml=1");
			
			if(profile.hasElement("error")) {
				System.out.println("Error!");
				System.exit(1);
			}
			
			String nickname  = profile.getUnescapedString("steamID");
            Long steamId64 = profile.getLong("steamID64");
            String tradeBanState = profile.getUnescapedString("tradeBanState");
            boolean vacBanned = (profile.getString("vacBanned").equals("1"));
            
            String avatarIconUrl = profile.getString("avatarIcon");
            String imageUrl = avatarIconUrl.substring(0, avatarIconUrl.length() - 4);
            boolean limitedAccount = (profile.getString("isLimitedAccount").equals("1"));
            String onlineState = profile.getString("onlineState");
            String privacyState = profile.getString("privacyState");
            String stateMessage = profile.getString("stateMessage");
            int visibilityState = profile.getInteger("visibilityState");
            
            String customUrl = "";
            String headLine = "";
            float hoursPlayed = 0f;
            String location = "";
            Date memberSince = null;
            String realName = "";
            float steamRating = 0f;
            String summary = "";
            HashMap<String, Float> mostPlayedGames = null;
            Long[] groups = {};
            HashMap<String, String> links = null;
            
            
            if(privacyState.compareTo("public") == 0) {
                customUrl = profile.getString("customURL");
                if(customUrl.length() == 0) {
                    customUrl = "";
                }

                headLine = profile.getUnescapedString("headline");
                hoursPlayed = profile.getFloat("hoursPlayed2Wk");
                location = profile.getString("location");
                memberSince = DateFormat.getDateInstance(DateFormat.LONG,Locale.ENGLISH).parse(profile.getString("memberSince"));
                realName = profile.getUnescapedString("realname");
                steamRating = profile.getFloat("steamRating");
                summary = profile.getUnescapedString("summary");

                mostPlayedGames = new HashMap<String, Float>();
                for(XMLData mostPlayedGame : profile.getElements("mostPlayedGames", "mostPlayedGame")) {
                    mostPlayedGames.put(mostPlayedGame.getString("gameName"), mostPlayedGame.getFloat("hoursPlayed"));
                }

                List<XMLData> groupElements = profile.getElements("groups", "group");
                groups = new Long[groupElements.size()];
                for(int i = 0; i < groups.length; i++) {
                    XMLData group = groupElements.get(i);
                    groups[i] = group.getLong("groupID64");
                }

                links = new HashMap<String, String>();
                for(XMLData weblink : profile.getElements("weblinks", "weblink")) {
                    links.put(weblink.getUnescapedString("title"), weblink.getString("link"));
                }
            }
            
            
            url += "/friends";
            XMLData friendData = new XMLData(url + "?xml=1");
            if(friendData.hasElement("error")) {
				System.out.println("Error!");
				System.exit(1);
			}
            
            List<XMLData> friendElements = friendData.getElements("friends", "friend");
            Long[] friends = new Long[friendElements.size()];
            for(int i = 0; i < friends.length; i++) {
                XMLData friend = friendElements.get(i);
                Element root = friend.getRoot();
                Node firstchild = root.getFirstChild();
                friends[i] = Long.parseLong(firstchild.getTextContent());
            }
            
            System.out.println(nickname);
            
            for ( Long friend : friends ) {
            	url = "http://steamcommunity.com/profiles/";
            	profile = new XMLData(url + "/" + friend + "?xml=1");
            	
            	if(profile.hasElement("error")) {
    				System.out.println("Error!");
    				System.exit(1);
    			}
    			
    			nickname  = profile.getUnescapedString("steamID");
    			onlineState = profile.getString("onlineState");
    			stateMessage = profile.getString("stateMessage");
    			
    			System.out.println(nickname + ": " + stateMessage);
            }
			
		} catch (IOException | ParserConfigurationException | SAXException | ParseException e) {
			e.printStackTrace();
		}
	}

}
