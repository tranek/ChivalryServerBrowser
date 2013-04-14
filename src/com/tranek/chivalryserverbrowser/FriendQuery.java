package com.tranek.chivalryserverbrowser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.github.koraktor.steamcondenser.steam.community.XMLData;

public class FriendQuery extends Thread {

	public String url;
	public SteamProfile steamProfile;
	public FriendsTab ft;
	public MainWindow mw;
	public ExecutorService pool;
	private Synchronizer synch;
	private final boolean inChiv;
	
	public FriendQuery(MainWindow mw, FriendsTab ft, String url) {
		this.url = url;
		this.steamProfile = ft.steamProfile;
		this.ft = ft;
		this.mw = mw;
		inChiv = ft.chckbxInChiv.isSelected();
	}
	
	@Override
	public void run() {
		mw.printlnMC("Getting Steam profile...");
		if ( getProfile(url) ) {
			getFriends((DefaultTableModel)ft.dataModel);
		} else {
			mw.printlnMC("Failed to get Steam profile.");
		}
	}
	
	public boolean getProfile(String url) {
		boolean success = false;
		try {
			XMLData profile = new XMLData(url + "?xml=1");
			
			if(profile.hasElement("error")) {
				System.out.println("Error!");
				return false;
			}
			String nickname  = profile.getUnescapedString("steamID");
			ft.lblPlayerName.setText(nickname);
			
			if ( nickname != null ) {
				steamProfile = new SteamProfile(url, nickname);
				success = true;
			}
		} catch ( IOException | ParserConfigurationException | SAXException e ) {
			e.printStackTrace();
		}
		return success;
	}
	
	public void getFriends(DefaultTableModel dataModel) {
		steamProfile.friends = new ArrayList<SteamProfile>();
		Set<Future<?>> set = new HashSet<Future<?>>();
		pool = Executors.newFixedThreadPool(32);
		synch = new Synchronizer(dataModel, steamProfile.friends);
		
		XMLData friendData = null;
		try {
			friendData = new XMLData(url + "/friends?xml=1");
			if(friendData.hasElement("error")) {
				System.out.println("Error with friend xml parsing!");
			}
			
			List<XMLData> friendElements = friendData.getElements("friends", "friend");
            Long[] friends = new Long[friendElements.size()];
            for(int i = 0; i < friends.length; i++) {
                XMLData friend = friendElements.get(i);
                Element root = friend.getRoot();
                Node firstchild = root.getFirstChild();
                friends[i] = Long.parseLong(firstchild.getTextContent());
                FriendWorker fw = new FriendWorker(friends[i]);
                Future<?> future = pool.submit(fw);
                set.add(future);
            }
            try {
	            for (Future<?> future : set) {
	            	future.get();
	            }
            } catch ( StringIndexOutOfBoundsException | InterruptedException | ExecutionException e1 ) {
            	
            }
            if ( ft.chckbxInChiv.isSelected() ) {
            	int chivPlayers = dataModel.getRowCount();
            	mw.printlnMC("Finished getting Steam profile. Found " + steamProfile.friends.size() + " friends with " +
            			chivPlayers + " playing Chivlary: Medieval Warfare right now! " +
    					"Wow you're popular!");
            } else {
            	mw.printlnMC("Finished getting Steam profile. Found " + steamProfile.friends.size() + " friends! " +
					"Wow you're popular!");
            }
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}
	
	public class Synchronizer {
		private final DefaultTableModel dataModel;
		private final ArrayList<SteamProfile> friends;
		
		public Synchronizer(DefaultTableModel datamodel, ArrayList<SteamProfile> friends) {
			dataModel = datamodel;
			this.friends = friends;
		}
		
		public synchronized void addToTable(Object[] rowData) {
			dataModel.addRow(rowData);
		}
		
		public synchronized void addToList(SteamProfile friend) {
			friends.add(friend);
		}
	}
	
	private class FriendWorker implements Callable<String> {

		//private XMLData friend;
		private Long friendID;
		
		public FriendWorker(Long friendID) {
			super();
			this.friendID = friendID;
		}
		
		@Override
		public String call() {
			String nickname = "";
			try {
	            String friendURL = "http://steamcommunity.com/profiles/" + friendID;
	            XMLData2 profile = new XMLData2(friendURL + "?xml=1");
	            nickname  = profile.getUnescapedString("steamID");
	            if (nickname.equals("")) {
	            	return "";
	            }
	            String stateMessage = "";
	            try {
	            	stateMessage = profile.getString("stateMessage");
	            } catch (NullPointerException npe) {
	            	System.out.println("Null pointer exception on friend state message!");
	            }
	            SteamProfile friendProfile = new SteamProfile(friendURL, nickname, stateMessage);
	            synch.addToList(friendProfile);
	            //System.out.println(nickname + ": " + stateMessage);
	            
	            Object[] rowData = {nickname, stateMessage, "", "", "", ""};
	            if ( inChiv && checkIfInChivalry(stateMessage)) {
	            	// Currently playing on a Chivalry server and showing only those in Chivalry
	            	System.out.println("IN CHIV");
	            	String[] details = getChivDetails(stateMessage);
	            	rowData[1] = details[0];
	            	rowData[3] = details[1];
	            	String stripped = details[1].substring(26);
					stripped = stripped.split("<")[0];
					String[] ipaddress = stripped.split(":");
					String ip = ipaddress[0];
					String gameport = ipaddress[1];
	            	ChivServer cs = mw.getServerFromDB(ip, gameport);
	            	if ( cs != null ) {
	            		mw.serversFriends.add(cs);
	            		rowData[2] = cs.mName;
	            		rowData[4] = cs.mCurrentPlayers + " / " + cs.mMaxPlayers;
	            		rowData[5] = cs.mPing;
	            		if ( cs.mHasPassword.equals("1") ) {
	            			rowData[6] = "Yes";
	            		}
	            	} else {
	            		mw.printlnMC("Friend on Chivalry server not in database. Please refresh all servers to update database!");
	            	}
	            	synch.addToTable(rowData);
	            } else if ( !inChiv && stateMessage.equals("") ) {
	            	rowData[1] = "Private profile";
	            	synch.addToTable(rowData);
	            } else if ( !inChiv && checkIfInGame(stateMessage) && !stateMessage.equals("") && stateMessage.contains("<span") ) {
	            	// Currently playing on a Chivalry server and showing all friends
	            	String[] details = getGameDetails(stateMessage);
	            	rowData[1] = details[0];
	            	rowData[3] = details[1];
	            	String stripped = details[1].substring(26);
					stripped = stripped.split("<")[0];
					String[] ipaddress = stripped.split(":");
					String ip = ipaddress[0];
					String gameport = ipaddress[1];
	            	ChivServer cs = mw.getServerFromDB(ip, gameport);
	            	if ( cs != null ) {
	            		mw.serversFriends.add(cs);
	            		rowData[2] = cs.mName;
	            		rowData[4] = cs.mCurrentPlayers + " / " + cs.mMaxPlayers;
	            		rowData[5] = cs.mPing;
	            		if ( cs.mHasPassword.equals("1") ) {
	            			rowData[6] = "Yes";
	            		}
	            	} else {
	            		mw.printlnMC("Friend on Chivalry server not in database. Please refresh all servers to update database!");
	            	}
	            	synch.addToTable(rowData);
	            } else if ( !inChiv ) {
	            	if ( ((String) rowData[1]).contains("<span") ) {
	            		int index = ((String) rowData[1]).indexOf("<span");
	            		rowData[1] = ((String) rowData[1]).substring(0, index);
	            	}
	            	if ( ((String) rowData[1]).contains("<br />") ) {
	            		rowData[1] = ((String) rowData[1]).replace("<br />", " ");
	            	}
	            	synch.addToTable(rowData);
	            }
	            
	            if ( pool.isShutdown() ) {
	            	return null;
	            }
	            
			} catch ( IOException | ParserConfigurationException | SAXException e ) {
				e.printStackTrace();
			}
			
			return nickname;
		}
		
		public boolean checkIfInChivalry(String status) {
			boolean ingame = false;
			
			if ( status.substring(0, 13).equals("In-Game<br />") ) {
				String game = status.substring(13, status.indexOf("<span class")).trim();
				if (game.equals("Chivalry: Medieval Warfare")) {
					ingame = true;
				}
				System.out.println(game);
				String ipstr = status.substring(status.indexOf("href="));
				ipstr = ipstr.substring(22, ipstr.indexOf(">")-1);
				System.out.println(ipstr);
			}
			
			return ingame;
		}
		
		public boolean checkIfInGame(String status) {
			boolean ingame = false;
			if ( status.length() < 14 ) {
				return false;
			}
			if ( status.substring(0, 13).equals("In-Game<br />") ) {
				ingame = true;
			}
			return ingame;
		}
		
		public String[] getGameDetails(String stateMessage) {
			String[] details = new String[2];
			String game = stateMessage.substring(13, stateMessage.indexOf("<span class")).trim();
			String status = "Playing " + game;
			details[1] = "";
			if ( game.equals("Chivalry: Medieval Warfare") ) {
				String ipstr = stateMessage.substring(stateMessage.indexOf("href="));
				ipstr = ipstr.substring(22, ipstr.indexOf(">")-1);
				ipstr = "<html><U><FONT COLOR=BLUE>" + ipstr + "</FONT></U></html>";
				details[1] = ipstr;
			}
			details[0] = status;
			
			return details;
		}
		
		public String[] getChivDetails(String stateMessage) {
			String[] details = new String[2];
			String game = stateMessage.substring(13, stateMessage.indexOf("<span class")).trim();
			String status = "Playing " + game;
			String ipstr = stateMessage.substring(stateMessage.indexOf("href="));
			ipstr = ipstr.substring(22, ipstr.indexOf(">")-1);
			ipstr = "<html><U><FONT COLOR=BLUE>" + ipstr + "</FONT></U></html>";
			details[0] = status;
			details[1] = ipstr;
			return details;
		}
	}
	
}
