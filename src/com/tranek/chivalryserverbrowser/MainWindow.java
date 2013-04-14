package com.tranek.chivalryserverbrowser;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

import netscape.javascript.JSObject;
import test.ButtonTabComponent;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;

/**
 * 
 *The main class that creates the frame and all other components.
 *It also contains useful utility functions used by it and other
 *classes.
 *
 *
 */
public class MainWindow {

	/** The JFrame for the application. */
	protected JFrame frmChivalryServers;
	/** A flag for the first startup of the application. */
	private boolean firstTimeShown = true;
	/** The message center at the bottom of the application to display messages to the user. */
	protected JTextArea messageCenter;
	/** The currently queried list of normal servers. */
	protected Vector<ChivServer> servers = new Vector<ChivServer>();
	/** The currently queried list of beta servers. */
	protected Vector<ChivServer> serversBeta = new Vector<ChivServer>();
	/** The currently queried list of favorite servers. */
	protected Vector<ChivServer> serversFav = new Vector<ChivServer>();
	/** The currently queried list of server history. */
	protected Vector<ChivServer> serversHist = new Vector<ChivServer>();
	/** The currently queried list of servers that the users friends are on. */
	protected Vector<ChivServer> serversFriends;
	/** The {@link ChivServer} currently shown as a white marker on the map. */
	protected ChivServer mapShownServer;
	/** A reference to the MainWindow object to pass to other objects. */
	private MainWindow mw;
	/** The JScrollPane for the message center. */
	private JScrollPane messageScrollPane;
	/** The JTabbedPane that holds all of the tabs. */
	protected JTabbedPane tabbedPane;
	/** Tab for normal server list. */
	protected ServerListTab serverListTab;
	/** Tab for beta server list. */
	protected ServerListBetaTab serverListBetaTab;
	/** Tab for favorite server list. */
	protected ServerListFavTab serverListFavTab;
	/** Tab for server history list. */
	protected ServerListHistoryTab serverListHistoryTab;
	/** Tab for friends list. */
	protected FriendsTab friendsTab;
	/** Tab for map. */
	protected JPanel mapTab;
	/** Tab for gamepad keybinds. */
	protected JPanel gamepadKeybindTab;
	/** Tab for settings. */
	protected SettingsTab settingsTab;
	/** The JFXPanel that hosts the embedded web browser for the map. */
	protected JFXPanel browserFxPanel;
	/** WebEngine for the map. */
	protected WebEngine webEngine;
	/** JCheckBox for whether or not to display normal server makers on the map. */
	protected JCheckBox chckbxNormalServers;
	/** JCheckBox for whether or not to display beta server makers on the map. */
	protected JCheckBox chckbxBetaServers;
	/** JCheckBox for whether or not to display favorite server makers on the map. */
	protected JCheckBox chckbxFavoriteServers;
	/** JCheckBox for whether or not to display server history makers on the map. */
	protected JCheckBox chckbxServerHistory;
	/** JToggleButton for the player heatmap on the map. */
	private JToggleButton tglbtnTogglePlayerHeat;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmChivalryServers.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {	
		mw = this;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChivalryServers = new JFrame();
		frmChivalryServers.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		frmChivalryServers.setTitle("Chivalry: Medieval Warfare Server Browser by ReMixx");
		frmChivalryServers.setBounds(100, 100, 1024, 768);
		frmChivalryServers.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmChivalryServers.setResizable(false);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmChivalryServers.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		serverListTab = new ServerListTab(this);
		tabbedPane.addTab("Server List", null, serverListTab, null);

		mapTab = new JPanel();
		JPanel mapSettingsPanel = new JPanel();
		mapSettingsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));		
		
		chckbxNormalServers = new JCheckBox("Normal Servers");
		chckbxNormalServers.setSelected(true);
		chckbxNormalServers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( chckbxNormalServers.isSelected() ) {
						for ( ChivServer cs : servers ) {
							addMarker(serverListTab, cs);
						}
				} else {
					removeMarkers(serverListTab);
				}
			}
		});
		mapSettingsPanel.add(chckbxNormalServers);
		
		chckbxBetaServers = new JCheckBox("Beta Servers");
		chckbxBetaServers.setSelected(true);
		chckbxBetaServers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( chckbxBetaServers.isSelected() ) {
					for ( ChivServer cs : serversBeta ) {
						addMarker(serverListBetaTab, cs);
					}
				} else {
					removeMarkers(serverListBetaTab);
				}
			}
		});
		mapSettingsPanel.add(chckbxBetaServers);
		
		chckbxFavoriteServers = new JCheckBox("Favorite Servers");
		chckbxFavoriteServers.setSelected(true);
		chckbxFavoriteServers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( chckbxFavoriteServers.isSelected() ) {
					for ( ChivServer cs : serversFav ) {
						addMarker(serverListFavTab, cs);
					}
				} else {
					removeMarkers(serverListFavTab);
				}
			}
		});
		mapSettingsPanel.add(chckbxFavoriteServers);
		
		chckbxServerHistory = new JCheckBox("Server History");
		chckbxServerHistory.setSelected(true);
		chckbxServerHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( chckbxServerHistory.isSelected() ) {
					for ( ChivServer cs : serversHist ) {
						addMarker(serverListHistoryTab, cs);
					}
				} else {
					removeMarkers(serverListHistoryTab);
				}
			}
		});
		mapSettingsPanel.add(chckbxServerHistory);
		browserFxPanel = new JFXPanel();
		browserFxPanel.setPreferredSize(new Dimension(1010, 535));
		mapTab.add(mapSettingsPanel, BorderLayout.NORTH);
		
		tglbtnTogglePlayerHeat = new JToggleButton("Toggle Player Heat Map");
		tglbtnTogglePlayerHeat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( tglbtnTogglePlayerHeat.isSelected() ) {
					enablePlayerHeatMap();
				} else {
					disablePlayerHeatMap();
				}
			}
		});
		mapSettingsPanel.add(tglbtnTogglePlayerHeat);
		mapTab.add(browserFxPanel, BorderLayout.CENTER);
		createScene();
		tabbedPane.addTab("Map", null, mapTab, null);
		
		serverListBetaTab = new ServerListBetaTab(this);
		tabbedPane.addTab("Beta Server List", null, serverListBetaTab, null);
		serverListFavTab = new ServerListFavTab(this);
		tabbedPane.addTab("Favorite Servers", null, serverListFavTab, null);
		serverListHistoryTab = new ServerListHistoryTab(this);
		tabbedPane.addTab("Server History", null, serverListHistoryTab, null);
		friendsTab = new FriendsTab(this);
		tabbedPane.addTab("Friends List", null, friendsTab, null);
		settingsTab = new SettingsTab(this);
		tabbedPane.addTab("Settings", null, settingsTab, null);
		gamepadKeybindTab = wrapInBackgroundImage(new GamepadKeybindTab(mw), 
				new ImageIcon(getClass().getResource("images/gamepad.png")));
		tabbedPane.addTab("Gamepad Keybinds", null, gamepadKeybindTab, null);
		
		JMenuBar menuBar = new JMenuBar();
		frmChivalryServers.getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		final JMenuItem mntmServerListBeta = new JMenuItem("Beta Servers");
		mntmServerListBeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( tabbedPane.indexOfComponent(serverListBetaTab) < 0 ) {
					tabbedPane.addTab("Beta Server List", null, serverListBetaTab, null);
					initTabComponent(tabbedPane, tabbedPane.getTabCount()-1);
				}
				tabbedPane.setSelectedComponent(serverListBetaTab);
			}
		});
		mnView.add(mntmServerListBeta);
		final JMenuItem mntmServerListFav = new JMenuItem("Favorite Servers");
		mntmServerListFav.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( tabbedPane.indexOfComponent(serverListFavTab) < 0 ) {
					tabbedPane.addTab("Favorite Servers", null, serverListFavTab, null);
					initTabComponent(tabbedPane, tabbedPane.getTabCount()-1);
				}
				tabbedPane.setSelectedComponent(serverListFavTab);
			}
		});
		mnView.add(mntmServerListFav);
		final JMenuItem mntmServerListHistory = new JMenuItem("Server History");
		mntmServerListHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( tabbedPane.indexOfComponent(serverListHistoryTab) < 0 ) {
					tabbedPane.addTab("Server History", null, serverListHistoryTab, null);
					initTabComponent(tabbedPane, tabbedPane.getTabCount()-1);
				}
				tabbedPane.setSelectedComponent(serverListHistoryTab);
			}
		});
		mnView.add(mntmServerListHistory);
		final JMenuItem mntmFriends = new JMenuItem("Friends List");
		mntmFriends.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( tabbedPane.indexOfComponent(friendsTab) < 0 ) {
					tabbedPane.addTab("Friends List", null, friendsTab, null);
					initTabComponent(tabbedPane, tabbedPane.getTabCount()-1);
				}
				tabbedPane.setSelectedComponent(friendsTab);
			}
		});
		mnView.add(mntmFriends);
		final JMenuItem mntmSettings = new JMenuItem("Settings");
		mntmSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( tabbedPane.indexOfComponent(settingsTab) < 0 ) {
					tabbedPane.addTab("Settings", null, settingsTab, null);
					initTabComponent(tabbedPane, tabbedPane.getTabCount()-1);
				}
				tabbedPane.setSelectedComponent(settingsTab);
			}
		});
		mnView.add(mntmSettings);
		final JMenuItem mntmGamepadKeybinds = new JMenuItem("Gamepad Keybinds");
		mntmGamepadKeybinds.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( tabbedPane.indexOfComponent(gamepadKeybindTab) < 0 ) {
					tabbedPane.addTab("Gamepapd Keybinds", null, gamepadKeybindTab, null);
					initTabComponent(tabbedPane, tabbedPane.getTabCount()-1);
				}
				tabbedPane.setSelectedComponent(gamepadKeybindTab);
			}
		});
		mnView.add(mntmGamepadKeybinds);
		
		JPanel messagePanel = new JPanel();
		frmChivalryServers.getContentPane().add(messagePanel, BorderLayout.SOUTH);
		int mpwidth = messagePanel.getParent().getWidth();
		messagePanel.setPreferredSize(new Dimension(mpwidth, 100));
		messagePanel.setLayout(new BorderLayout(0, 0));
		
		messageCenter = new JTextArea();
		messageCenter.setEditable(false);
		//these two lines make the jtextarea always update to the furthest position
		DefaultCaret caret = (DefaultCaret)messageCenter.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		messageScrollPane = new JScrollPane(messageCenter); // Adds messageCenter directly to scrollpane
		messageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		messageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		messagePanel.add(messageScrollPane);
		
		if( firstTimeShown ) {
			serverListTab.loadFilters();
			serverListBetaTab.loadFilters();
			serverListFavTab.loadFilters();
			serverListHistoryTab.loadFilters();
			serverListTab.RefreshServers();
			loadSettings();
			firstTimeShown = false;
			addMajorTabComponent();
		}
	}

	/**
	 * Prints a single line of text to the {@link MainWindow#messageCenter}
	 * without adding a new line before it.
	 * 
	 * @param msg the String to be printed
	 * @see #checkMCBuffer()
	 */
	public void printMC(String msg) {
		checkMCBuffer();
		messageCenter.append(msg);
	}
	
	/**
	 * Prints a single line of text to the {@link MainWindow#messageCenter}
	 * with adding a new line before it.
	 * 
	 * @param msg the String to be printed
	 * @see #checkMCBuffer()
	 */
	public void printlnMC(String msg) {
		checkMCBuffer();
		messageCenter.append("\n" + msg);
	}
	
	/**
	 * Keeps the {@link MainWindow#messageCenter} at no more than 30 lines of text.
	 */
	public void checkMCBuffer() {
		String mctext = messageCenter.getText();
		String[] numlines = mctext.split("\\r?\\n");
		if ( numlines.length > 30 ) {
			int index = mctext.indexOf('\n');
			if ( index > -1 ) {
				mctext = mctext.substring(index);
			}
			messageCenter.setText(mctext);
		}
	}
	
	/**
	 * Saves all tabs' filters and the application's settings before exiting
	 * the application.
	 */
	public void exit() {
		serverListTab.saveFilters();
		serverListBetaTab.saveFilters();
		serverListFavTab.saveFilters();
		serverListHistoryTab.saveFilters();
		saveGameConfig();
		saveSettings();
		System.exit(0);
	}
	
	/**
	 * Returns the {@link ChivServer} for the given ip address, gameport, and ServerListInterface.
	 * 
	 * @param ip the ip address of the {@link ChivServer}
	 * @param gameport the gameport of the {@link ChivServer}
	 * @param sl the {@link ServerListInterface} that requested the lookup
	 * @return	the {@link ChivServer} if found, null otherwise
	 * @see #findChivServer(String, String, Vector)
	 */
	public ChivServer findChivServer(String ip, String gameport, ServerListInterface sl) {
		Vector<ChivServer> s = null;
		if ( sl == serverListTab ) {
			s = servers;
		} else if ( sl == serverListBetaTab ) {
			s = serversBeta;
		} else if ( sl == serverListFavTab ) {
			s = serversFav;
		} else if ( sl == serverListHistoryTab ) {
			s = serversHist;
		}
		
		return findChivServer(ip, gameport, s);
	}
	
	/**
	 * Returns the {@link ChivServer} for the given ip address, gameport, and list of servers.
	 * 
	 * @param ip the ip address of the {@link ChivServer}
	 * @param gameport the gameport of the {@link ChivServer}
	 * @param servers the {@link Vector} of {@link ChivServer} to look in
	 * @return	the {@link ChivServer} if found, null otherwise
	 */
	public ChivServer findChivServer(String ip, String gameport, Vector<ChivServer> servers) {
		if ( servers != null ) {
			for ( ChivServer cs : servers ) {
				if ( cs.mIP.equals(ip) && cs.mGamePort.equals(gameport) ) {
					return cs;
				}
			}
		}
		return null;
	}

	/**
	 * Adds a {@link ButtonTabComponent} to a tab to close the tab.
	 * 
	 * @param pane the {@link JTabbedPane} with the tabs
	 * @param i	the index of the tab to add the button component to
	 */
	private void initTabComponent(JTabbedPane pane, int i) {
        pane.setTabComponentAt(i,
                 new ButtonTabComponent(pane));
    }
	
	/**
	 * Calls {@link #initTabComponent(JTabbedPane, int)} on all the standard tabs.
	 * 
	 * @see #initTabComponent(JTabbedPane, int)
	 */
	private void addMajorTabComponent() {
		int numtabs = tabbedPane.getTabCount();
		if ( numtabs > 2) {
			for (int i=2; i<numtabs; i++) {
				initTabComponent(tabbedPane, i);
			}
		}
	}
	
	/**
	 * Adds a new tab with details about a specific server.
	 * 
	 * @param ip_port a string of the "ipaddress:gameport" of the server
	 * @param sl the {@link ServerListInterface} that called this method
	 * @param switchToTab whether or not to automatically switch to the new tab
	 * @see #addServerTab(ChivServer, boolean)
	 */
	public void addServerTab(String ip_port, ServerListInterface sl, boolean switchToTab) {
		String stripped = ip_port;
		stripped = stripped.split("<")[0];
		String[] ipaddress = stripped.split(":");
		String ip = ipaddress[0];
		String gameport = ipaddress[1];
		ChivServer cs = findChivServer(ip, gameport, sl);
		if ( cs != null ) {
			addServerTab(cs, switchToTab);
		}
	}
	
	/**
	 * Adds a new tab with details about the server that is currently shown on the map (white marker).
	 * 
	 * @param switchToTab whether or not to automatically switch to the new tab
	 * @see #addServerTab(ChivServer, boolean)
	 */
	public void addServerTabShownServer(boolean switchToTab) {
		ChivServer cs = mapShownServer;
		if ( cs != null ) {
			addServerTab(cs, switchToTab);
		}
	}
	
	/**
	 * Adds a new tab with details about a specific server. It looks up the
	 * "ipaddress:gameport" combination from the database of previously seen
	 * servers. If the server is not found, it does nothing.
	 * 
	 * @param ip_port the "ipaddress:gameport" of the server
	 * @param switchToTab whether or not to automatically switch to the new tab
	 * @see #addServerTab(ChivServer, boolean)
	 */
	public void addServerTab(String ip_port, boolean switchToTab) {
		String ip = ip_port.split(":")[0];
		String gameport = ip_port.split(":")[1];
		ChivServer cs = getServerFromDB(ip, gameport);
		if ( cs != null ) {
			addServerTab(cs, switchToTab);
		} else {
			printlnMC("Server not found in database. Please refresh all servers to update the database.");
		}
	}
	
	/**
	 * Adds a new tab with details about a specific server.
	 * 
	 * @param cs the {@link ChivServer} of the new tab
	 * @param switchToTab whether or not to automatically switch to the new tab
	 * @see #initTabComponent(JTabbedPane, int)
	 */
	public void addServerTab(ChivServer cs, boolean switchToTab) {
		if ( cs != null) {
			boolean tabExists = false;
			for ( int i=0; i<tabbedPane.getTabCount(); i++ ) {
				if ( tabbedPane.getTitleAt(i).equals( cs.mName ) )
				{
					tabExists = true;
					if ( switchToTab ) {
						tabbedPane.setSelectedIndex(i);
					}
					break;
				}
			}
			if ( !tabExists ) {
				
				System.out.println("Chiv server found.");
				ServerTab st = new ServerTab(mw, cs);
				tabbedPane.addTab(cs.mName, null, st, null);
				initTabComponent(tabbedPane, tabbedPane.getTabCount() - 1);
				if ( switchToTab ) {
					tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
				}
			} else {
			}
		}
	}
	
	/**
	 * The method that the map javascript calls to add a new tab with details
	 * about a specific server. Since javascript can't send actual java objects,
	 * it includes a string parameter representing the server list tab where
	 * this server could be found in a lookup.
	 * 
	 * @param ip_port the "ipaddress:gameport" of the server
	 * @param sl a string denoting which server list tab that this server originally came from
	 * @see #addServerTab(ChivServer, boolean)
	 * @see #addServerTabShownServer(boolean)
	 */
	public void addServerTabJS(String ip_port, String sl) {
		if ( sl.equals("n") ) {
			addServerTab(ip_port, serverListTab, true);
		} else if ( sl.equals("b") ) {
			addServerTab(ip_port, serverListBetaTab, true);
		} else if ( sl.equals("f") ) {
			addServerTab(ip_port, serverListBetaTab, true);
		} else if ( sl.equals("h") ) {
			addServerTab(ip_port, serverListBetaTab, true);
		} else if ( sl.equals("s") ) {
			addServerTabShownServer(true);
		}
	}
	
	/**
	 * Adds a {@link ChivServer} as a favorite server to the database.
	 * 
	 * @param cs the {@link ChivServer} to add
	 * @see SQLiteConnection
	 * @see SQLiteStatement
	 * @see SQLiteException
	 */
	public void addFavorite(ChivServer cs) {
		
		printlnMC("Adding server to favorites...");
		
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS favorite_servers" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"name varchar(255) not null default ''," +
					"ip varchar(255) not null default ''," +
					"port varchar(255) not null default '' )");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			//check if exists
			int id = -1;
			st = db.prepare("SELECT id FROM favorite_servers WHERE ip = '" + cs.mIP + "' AND  port = '" + cs.mQueryPort + "'");
			try {
				st.step();
				if ( st.hasRow() ) {
					id = st.columnInt(0);
				}
			} finally {
				st.dispose();
			}
			
			if ( id > -1 ) {
				printlnMC("Server already a favorite.");
				st = db.prepare("INSERT OR REPLACE INTO favorite_servers (id, name, ip, port) " +
						"VALUES ( " + id + ",'" + cs.mName + "', '" + cs.mIP + "', '" + cs.mQueryPort + "' )");
				try {
					st.step();
				} finally {
					st.dispose();
				}
			} else {
				st = db.prepare("INSERT INTO favorite_servers (name, ip, port) " +
						"VALUES ('" + cs.mName + "', '" + cs.mIP + "', '" + cs.mQueryPort + "' )");
				try {
					st.step();
				} finally {
					st.dispose();
				}
				printlnMC("Added to favorites: " + cs.mName);
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
	}

	/**
	 * Removes a {@link ChivServer} from the favorites database.
	 * 
	 * @param cs the {@link ChivServer} to remove
	 * @return if the server was successfully found and removed
	 * @see SQLiteConnection
	 * @see SQLiteStatement
	 * @see SQLiteException
	 */
	public boolean removeFavorite(ChivServer cs) {
		boolean removed = false;
		System.out.println("Removing from favorites...");
		printlnMC("Removing server from favorites...");
		
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS favorite_servers" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"name varchar(255) not null default ''," +
					"ip varchar(255) not null default ''," +
					"port varchar(255) not null default '' )");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			//check if exists
			int id = -1;
			st = db.prepare("SELECT id FROM favorite_servers WHERE ip = '" + cs.mIP + "' AND  port = '" + cs.mQueryPort + "'");
			try {
				st.step();
				if ( st.hasRow() ) {
					id = st.columnInt(0);
				}
			} finally {
				st.dispose();
			}
			
			if ( id > -1 ) {
				st = db.prepare("DELETE FROM favorite_servers WHERE id = " + id);
				try {
					st.step();
					
					for ( int i=0; i<serversFav.size(); i++ ) {
						ChivServer c = serversFav.get(i);
						if ( cs.mIP.equals(c.mIP) && cs.mQueryPort.equals(c.mQueryPort) ) {
							serversFav.remove(i);
							break;
						}
					}
					
					printlnMC("Removed favorite: " + cs.mName);
					removed = true;
				} finally {
					st.dispose();
				}
			} else {
				printlnMC("Didn't find server in database. This is probably a bad thing.");
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
		
		return removed;
	}
	
	/**
	 * Stops all server list tabs from refreshing.
	 * 
	 * @see Refresher
	 * @see RefresherBeta
	 * @see RefresherFav
	 * @see RefresherHistory
	 */
	public void stopAllRefreshing() {
		serverListTab.refresher.stopRefreshing(mw);
		if ( serverListBetaTab.refresher != null ) {
			serverListBetaTab.refresher.stopRefreshing(mw);
		}
		if ( serverListFavTab.refresher != null ) {
			serverListFavTab.refresher.stopRefreshing(mw);
		}
		if ( serverListHistoryTab.refresher != null ) {
			serverListHistoryTab.refresher.stopRefreshing(mw);
		}
	}

	/**
	 * Adds a {@link ChivServer} to the server history database. This is typically called
	 * when the user joins a server.
	 * 
	 * @param cs the {@link ChivServer} to add
	 * @see SQLiteConnection
	 * @see SQLiteStatement
	 * @see SQLiteException
	 */
	public void addServerToHistory(ChivServer cs) {
		System.out.println("Adding server to history list.");
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS server_history" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"name varchar(255) not null default ''," +
					"ip varchar(255) not null default ''," +
					"port varchar(255) not null default '' )");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			//check if exists
			int id = -1;
			st = db.prepare("SELECT id FROM server_history WHERE ip = '" + cs.mIP + "' AND  port = '" + cs.mQueryPort + "'");
			try {
				st.step();
				if ( st.hasRow() ) {
					id = st.columnInt(0);
				}
			} finally {
				st.dispose();
			}
			
			if ( id > -1 ) {
				st = db.prepare("INSERT OR REPLACE INTO server_history (id, name, ip, port) " +
						"VALUES ( " + id + ",'" + cs.mName + "', '" + cs.mIP + "', '" + cs.mQueryPort + "' )");
				try {
					st.step();
				} finally {
					st.dispose();
				}
			} else {
				st = db.prepare("INSERT INTO server_history (name, ip, port) " +
						"VALUES ('" + cs.mName + "', '" + cs.mIP + "', '" + cs.mQueryPort + "' )");
				try {
					st.step();
				} finally {
					st.dispose();
				}
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
	}
	
	/**
	 * Removes a {@link ChivServer} from the history database.
	 * 
	 * @param cs the {@link ChivServer} to remove
	 * @return if the {@link ChivServer} was successfully found and removed
	 * @see SQLiteConnection
	 * @see SQLiteStatement
	 * @see SQLiteException
	 */
	public boolean removeServerFromHistory(ChivServer cs) {
		boolean removed = false;
		System.out.println("Removing from history...");
		printlnMC("Removing server from history...");
		
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS server_history" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"name varchar(255) not null default ''," +
					"ip varchar(255) not null default ''," +
					"port varchar(255) not null default '' )");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			//check if exists
			int id = -1;
			st = db.prepare("SELECT id FROM server_history WHERE ip = '" + cs.mIP + "' AND  port = '" + cs.mQueryPort + "'");
			try {
				st.step();
				if ( st.hasRow() ) {
					id = st.columnInt(0);
				}
			} finally {
				st.dispose();
			}
			
			if ( id > -1 ) {
				st = db.prepare("DELETE FROM server_history WHERE id = " + id);
				try {
					st.step();
					
					for ( int i=0; i<serversFav.size(); i++ ) {
						ChivServer c = serversFav.get(i);
						if ( cs.mIP.equals(c.mIP) && cs.mQueryPort.equals(c.mQueryPort) ) {
							serversFav.remove(i);
							break;
						}
					}
					
					printlnMC("Removed history: " + cs.mName);
					removed = true;
				} finally {
					st.dispose();
				}
			} else {
				printlnMC("Didn't find server in database. This is probably a bad thing.");
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
		
		return removed;
	}

	/**
	 * Launches the game and joins a specific server via the Steam URL.
	 * 
	 * @param urlstring the Steam URL to launch the game and join the server
	 * @param ip the ip address of the server
	 * @param port the gameport of the server
	 * @param serverName the name of the server
	 * @param sl the {@link ServerListInterface} where this server is found for lookup
	 * @see ChivServer
	 * @see #findChivServer(String, String, ServerListInterface)
	 * @see #joinServer(String, String, String, String, ChivServer)
	 */
	public void joinServer(String urlstring, String ip, String port, String serverName, ServerListInterface sl) {
		ChivServer cs = findChivServer(ip, port, sl);
		joinServer(urlstring, ip, port, serverName, cs);
	}
	
	/**
	 * Launches the game ad joins a specific server via the Steam URL.
	 * 
	 * @param urlstring the Steam URL to launch the game and join the server
	 * @param ip the ip address of the server
	 * @param port the gameport of the server
	 * @param serverName the name of the server
	 * @param cs the {@link ChivServer} to join
	 * @see Desktop
	 * @see URI
	 * @see #addServerToHistory(ChivServer)
	 * @see #saveGameConfig()
	 * @see #getLaunchOptions()
	 */
	public void joinServer(String urlstring, String ip, String port, String serverName, ChivServer cs) {
		saveGameConfig();
		Desktop dt = Desktop.getDesktop();
		URI url = null;
		try {
			url = new URI(urlstring + getLaunchOptions());
			System.out.println(urlstring + getLaunchOptions());
			dt.browse(url);
			mw.printlnMC("Joining server: " + serverName + " " + ip + ":" + port + " ...");
			mw.stopAllRefreshing();
			addServerToHistory(cs);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Launches the game and joins a specific server via the Steam URL that a Steam friend is playing on.
	 * 
	 * @param urlstring	the Steam URL to launch the game and join the server
	 * @param ip the ip address of the server
	 * @param port the gameport of the server
	 * @param friendName the name of the Steam friend on the server
	 * @see Desktop
	 * @see URI
	 * @see #saveGameConfig()
	 * @see #getLaunchOptions()
	 */
	public void joinServer(String urlstring, String ip, String port, String friendName) {
		saveGameConfig();
		Desktop dt = Desktop.getDesktop();
		URI url = null;
		try {
			url = new URI(urlstring + getLaunchOptions());
			dt.browse(url);
			mw.printlnMC("Joining friend: " + friendName + " at " + ip + ":" + port + " ...");
			mw.stopAllRefreshing();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Method called by the map's javascript to join a specific server via the Steam URL.
	 * 
	 * @param ip the ip of the server
	 * @param queryport the queryport of the server
	 * @see ChivServer
	 * @see #joinServer(String, String, String, String, ChivServer)
	 */
	public void joinServerJS(String ip, String queryport) {
		ChivServer cs = ChivServer.createChivServer(mw, ip, Integer.parseInt(queryport));
		String urlstring = "";
		if ( cs.mHasPassword.equals("1") ) {
			String password = javax.swing.JOptionPane.showInputDialog("Please enter a password");
			if ( !password.equals("") ) {
				urlstring = "steam://run/219640/en/" + ip + ":" + cs.mGamePort + "%3fpassword=" + password;
				joinServer(urlstring, ip, cs.mGamePort, cs.mName, cs);
			}
		} else {
			urlstring = "steam://run/219640/en/" + ip + ":" + cs.mGamePort;
			joinServer(urlstring, ip, cs.mGamePort, cs.mName, cs);
		}
	}
	
	/**
	 * Loads the application settings from the database.
	 * 
	 * @see SQLiteConnection
	 * @see SQLiteStatement
	 * @see SQLiteException
	 */
	public void loadSettings() {
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS settings" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"steam_url varchar(255) not null default ''," +
					"show_chiv_only int not null default 1," +
					"servers_beta_visible int not null default 1," +
					"servers_fav_visible int not null default 1," +
					"servers_hist_visible int not null default 1," +
					"friends_visible int not null default 1," +
					"map_visible int not null default 1," +
					"map_normal_chckbox int not null default 1," +
					"map_beta_chckbox int not null default 1," +
					"map_favorite_chckbox int not null default 1," +
					"map_history_chckbox int not null default 1," +
					"settings_visible int not null default 1," +
					"launch_opt_window int not null default 0," +
					"launch_opt_resx varchar (255) not null default ''," +
					"launch_opt_resy varchar (255) not null default ''," +
					"launch_opt_enable int not null default 0," +
					"gamepad_visible int not null default 1" +
					")");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			st = db.prepare("SELECT * FROM settings WHERE id = 1 ");
			try {
				st.step();
				if ( st.hasRow() ) {
					friendsTab.urlField.setText(st.columnString(1));
					settingsTab.tfSteamCommunityUrl.setText(st.columnString(1));
					if ( st.columnInt(2) == 1 ) {
						friendsTab.chckbxInChiv.setSelected(true);
					} else {
						friendsTab.chckbxInChiv.setSelected(false);
					}
					if ( st.columnInt(3) == 0 ) {
						tabbedPane.remove(serverListBetaTab);
					}
					if ( st.columnInt(4) == 0 ) {
						tabbedPane.remove(serverListFavTab);
					}
					if ( st.columnInt(5) == 0 ) {
						tabbedPane.remove(serverListHistoryTab);
					}
					if ( st.columnInt(6) == 0 ) {
						tabbedPane.remove(friendsTab);
					}
					if ( st.columnInt(8) == 1 ) {
						chckbxNormalServers.setSelected(true);
					} else {
						chckbxNormalServers.setSelected(false);
					}
					if ( st.columnInt(9) == 1 ) {
						chckbxBetaServers.setSelected(true);
					} else {
						chckbxBetaServers.setSelected(false);
					}
					if ( st.columnInt(10) == 1 ) {
						chckbxFavoriteServers.setSelected(true);
					} else {
						chckbxFavoriteServers.setSelected(false);
					}
					if ( st.columnInt(11) == 1 ) {
						chckbxServerHistory.setSelected(true);
					} else {
						chckbxServerHistory.setSelected(false);
					}
					if ( st.columnInt(12) == 0 ) {
						tabbedPane.remove(settingsTab);
					}
					settingsTab.cbScreen.setSelectedIndex(st.columnInt(13));
					settingsTab.tfLaunchResX.setText(st.columnString(14));
					settingsTab.tfLaunchResY.setText(st.columnString(15));
					if ( st.columnInt(16) == 1 ) {
						settingsTab.tglbtnEnableLaunchOptions.setSelected(true);
					} else {
						settingsTab.tglbtnEnableLaunchOptions.setSelected(false);
					}
					if ( st.columnInt(17) == 0 ) {
						tabbedPane.remove(gamepadKeybindTab);
					}
				}
			} finally {
				st.dispose();
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
	}

	/**
	 * Saves the application settings to the database.
	 * 
	 * @see SQLiteConnection
	 * @see SQLiteStatement
	 * @see SQLiteException
	 */
	public void saveSettings() {
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS settings" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"steam_url varchar(255) not null default ''," +
					"show_chiv_only int not null default 1," +
					"servers_beta_visible int not null default 1," +
					"servers_fav_visible int not null default 1," +
					"servers_hist_visible int not null default 1," +
					"friends_visible int not null default 1," +
					"map_visible int not null default 1," +
					"map_normal_chckbox int not null default 1," +
					"map_beta_chckbox int not null default 1," +
					"map_favorite_chckbox int not null default 1," +
					"map_history_chckbox int not null default 1," +
					"settings_visible int not null default 1," +
					"launch_opt_window int not null default 0," +
					"launch_opt_resx varchar (255) not null default ''," +
					"launch_opt_resy varchar (255) not null default ''," +
					"launch_opt_enable int not null default 0," +
					"gamepad_visible int not null default 1" +
					")");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			String steamURL = friendsTab.urlField.getText();
			int showChivOnly = 1;
			if ( !friendsTab.chckbxInChiv.isSelected() ) {
				showChivOnly = 0;
			}
			int beta_visible = 1;
			if ( tabbedPane.indexOfComponent(serverListBetaTab) < 0 ) {
				beta_visible = 0;
			}
			int fav_visible = 1;
			if ( tabbedPane.indexOfComponent(serverListFavTab) < 0 ) {
				fav_visible = 0;
			}
			int hist_visible = 1;
			if ( tabbedPane.indexOfComponent(serverListHistoryTab) < 0 ) {
				hist_visible = 0;
			}
			int friends_visible = 1;
			if ( tabbedPane.indexOfComponent(friendsTab) < 0 ) {
				friends_visible = 0;
			}
			int settings_visible = 1;
			if ( tabbedPane.indexOfComponent(settingsTab) < 0 ) {
				settings_visible = 0;
			}
			int map_visible = 1;
			if ( tabbedPane.indexOfComponent(mapTab) < 0 ) {
				//map_visible = 0;
			}
			int gamepad_visible = 1;
			if ( tabbedPane.indexOfComponent(gamepadKeybindTab) < 0 ) {
				gamepad_visible = 0;
			}
			int map_normal_chckbox = 1;
			if ( !chckbxNormalServers.isSelected() ) {
				map_normal_chckbox = 0;
			}
			int map_beta_chckbox = 1;
			if ( !chckbxBetaServers.isSelected() ) {
				map_beta_chckbox = 0;
			}
			int map_favorite_chckbox = 1;
			if ( !chckbxFavoriteServers.isSelected() ) {
				map_favorite_chckbox = 0;
			}
			int map_history_chckbox = 1;
			if ( !chckbxServerHistory.isSelected() ) {
				map_history_chckbox = 0;
			}
			int launch_opt_enable = 0;
			if ( settingsTab.tglbtnEnableLaunchOptions.isSelected() ) {
				launch_opt_enable = 1;
			}
			
			st = db.prepare("INSERT OR REPLACE INTO settings (id, steam_url, show_chiv_only, " +
					"servers_beta_visible, servers_fav_visible, servers_hist_visible, friends_visible, " +
					"settings_visible, map_visible, map_normal_chckbox, map_beta_chckbox, " +
					"map_favorite_chckbox, map_history_chckbox, launch_opt_window, launch_opt_resx, " +
					"launch_opt_resy, launch_opt_enable, gamepad_visible) " +
					"VALUES (  1, '" + steamURL + "', " + showChivOnly + ", " + beta_visible + 
					", " + fav_visible + ", " + hist_visible + ", " + friends_visible + ", " + 
					settings_visible + ", " + map_visible + ", " + map_normal_chckbox + ", " + 
					map_beta_chckbox + ", " + map_favorite_chckbox + ", " + map_history_chckbox +
					", " + settingsTab.cbScreen.getSelectedIndex() + ", '" + settingsTab.tfLaunchResX.getText() +
					"', '" + settingsTab.tfLaunchResY.getText() + "', " + launch_opt_enable + ", " +
					gamepad_visible + ")");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
	}

	/**
	 * Creates the JavaFX scene for the Map tab. It creates a new {@link WebEngine} and loads the
	 * map.html resource.
	 * 
	 * @see WebView
	 * @see JSObject#setMember(String, Object)
	 * @see #browserFxPanel
	 */
	private void createScene() {	
		Platform.runLater(new Runnable() {		
			@Override
			public void run() {
				WebView view = new WebView();
		        webEngine = view.getEngine();
		        
		        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
		            public void handle(WebEvent<String> ev) {
		            	System.out.println(ev.getData());
		            	if ( ev.getData().equals("jquery works!") ) {
		            		webEngine.executeScript("javacall('helloworld')");
		            	}
		            	if ( ev.getData().equals("loaded") ) {
		            		HashMap<String, String> loc = getLoc();
		            		String lat = loc.get("latitude");
		            		String lon = loc.get("longitude");
		            		String func = "addUserLoc('" + lat + "', '" + lon + "')";
		            		if ( !lat.equals("") && !lon.equals("") ) {
		            			webEngine.executeScript(func);
		            		}
		            	}
		            }
		        });
		        
		        String url = getClass().getResource("map.html").toExternalForm();
		        webEngine.load(url);
		        
		        webEngine.getLoadWorker().stateProperty().addListener(
			        new ChangeListener<State>(){
			            
			            @Override
			            public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
			                if(newState == State.SUCCEEDED){
			                    JSObject window = (JSObject)webEngine.executeScript("window");
			                    window.setMember("app", mw);
			                }
			            }
			        });
		        JSObject window = (JSObject)webEngine.executeScript("window");
		        window.setMember("app", mw);
		        
		        browserFxPanel.setScene(new Scene(view));
			}
		});
	}
	
	/**
	 * Gets the location of the user by querying http://checkip.amazonaws.com.
	 * 
	 * @return the latitude and longitude of the user
	 * @see URL
	 * @see LocationRIPE#getLocation(String)
	 * @see HashMap
	 */
	public HashMap<String, String> getLoc() {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(
			                whatismyip.openStream()));
			String ip = in.readLine(); //you get the IP as a String
			LocationRIPE l = new LocationRIPE();
    		HashMap<String, String> loc = l.getLocation(ip);
    		String lat = loc.get("latitude");
    		String lon = loc.get("longitude");
    		HashMap<String, String> result = new HashMap<String, String>();
    		result.put("latitude", lat);
    		result.put("longitude", lon);
    		return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Removes all markers from the map from servers found in the server list tab.
	 * 
	 * @param sl the {@link ServerListInterface} with the markers to remove from the map
	 * @see WebEngine#executeScript(String)
	 */
	public void removeMarkers(final ServerListInterface sl) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String func = "";
				if ( sl == serverListTab ) {
					func = "clearMarkers('n')";
				} else if ( sl == serverListBetaTab ) {
					func = "clearMarkers('b')";
				} else if ( sl == serverListFavTab ) {
					func = "clearMarkers('f')";
				} else if ( sl == serverListHistoryTab ) {
					func = "clearMarkers('h')";
				}
				if ( !func.equals("") ) {
					webEngine.executeScript(func);
				}
			}
		});
	}
	
	/**
	 * Adds a marker for a given {@link ChivServer} to the map.
	 * 
	 * @param sl the {@link ServerListInterface} that the {@link ChivServer} came from
	 * @param cs the {@link ChivServer} to add to the map as a marker
	 * @see WebEngine#executeScript(String)
	 */
	public void addMarker(final ServerListInterface sl, final ChivServer cs) {
		if ( !cs.mLatitude.equals("") && !cs.mLongitude.equals("") ) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					String title = cs.mName;
					String hasPassword = "No";
					if ( cs.mHasPassword.equals("1") ) {
						hasPassword = "Yes";
					}
					String html = "<p>" + cs.mName + "</p>" +
							"<a href='javascript:joinServer(\\\"" + cs.mIP + "\\\", \\\"" + cs.mQueryPort + "\\\")'>" +
							"Connect to server: " + cs.mIP + ":" + cs.mGamePort + "</a>" +
							"<p>Map: " + cs.mMap + "</p>" +
							"<p>Players: " + cs.mCurrentPlayers + " / " + cs.mMaxPlayers + "</p>" +
							"<p>Ping: " + cs.mPing + "<span style='float:right'>Password: " + hasPassword + "</span></p>";
					String func = "";
					if ( sl == serverListTab ) {
						html += "<a href='javascript:moreInfoTab(\\\"" + cs.mIP + ":" + cs.mGamePort +
								"\\\", \\\"n\\\")'>More info</a>";
						func = "addMarker('" + cs.mLatitude + "', '" + cs.mLongitude + "', '" + "n" +
								"', \"" + title + "\", \"" + html + "\", " + cs.mCurrentPlayers + ")";
					} else if ( sl == serverListBetaTab ) {
						html += "<a href='javascript:moreInfoTab(\\\"" + cs.mIP + ":" + cs.mGamePort +
								"\\\", \\\"b\\\")'>More info</a>";
						func = "addMarker('" + cs.mLatitude + "', '" + cs.mLongitude + "', '" + "b" +
								"', \"" + title + "\", \"" + html + "\", " + cs.mCurrentPlayers + ")";
					} else if ( sl == serverListFavTab ) {
						html += "<a href='javascript:moreInfoTab(\\\"" + cs.mIP + ":" + cs.mGamePort +
								"\\\", \\\"f\\\")'>More info</a>";
						func = "addMarker('" + cs.mLatitude + "', '" + cs.mLongitude + "', '" + "f" +
								"', \"" + title + "\", \"" + html + "\", " + cs.mCurrentPlayers + ")";
					} else if ( sl == serverListHistoryTab ) {
						html += "<a href='javascript:moreInfoTab(\\\"" + cs.mIP + ":" + cs.mGamePort +
								"\\\", \\\"h\\\")'>More info</a>";
						func = "addMarker('" + cs.mLatitude + "', '" + cs.mLongitude + "', '" + "h" +
								"', \"" + title + "\", \"" + html + "\", " + cs.mCurrentPlayers + ")";
					}
					if ( !func.equals("") ) {
						webEngine.executeScript(func);
					}
				}
			});
		}
	}
	
	/**
	 * Shows a specific {@link ChivServer} on the map as a white marker.
	 * 
	 * @param cs the {@link ChivServer} to show
	 * @see WebEngine#executeScript(String)
	 */
	public void showInMap(final ChivServer cs) {
		mapShownServer = cs;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String title = cs.mName;
				String hasPassword = "No";
				if ( cs.mHasPassword.equals("1") ) {
					hasPassword = "Yes";
				}
				String html = "<p>" + cs.mName + "</p>" +
						"<a href='javascript:joinServer(\\\"" + cs.mIP + "\\\", \\\"" + cs.mQueryPort + "\\\")'>" +
						"Connect to server: " + cs.mIP + ":" + cs.mGamePort + "</a>" +
						"<p>Map: " + cs.mMap + "</p>" +
						"<p>Players: " + cs.mCurrentPlayers + " / " + cs.mMaxPlayers + "</p>" +
						"<p>Ping: " + cs.mPing + "<span style='float:right'>Password: " + hasPassword + "</span></p>" +
						"<a href='javascript:moreInfoTab(\\\"" + cs.mIP + ":" + cs.mGamePort +
						"\\\", \\\"s\\\")'>More info</a>";
				String func = "addMarker('" + cs.mLatitude + "', '" + cs.mLongitude + "', '" + "s" +
						"', \"" + title + "\", \"" + html + "\", " + cs.mCurrentPlayers + ")";
				webEngine.executeScript(func);
				func = "showIW()";
				webEngine.executeScript(func);
			}
		});
		tabbedPane.setSelectedComponent(mapTab);
	}
	
	/**
	 * Enables the player heatmap on the map.
	 * 
	 * @see WebEngine#executeScript(String)
	 * @see "https://developers.google.com/maps/documentation/javascript/reference#HeatmapLayer"
	 */
	public void enablePlayerHeatMap() {
		System.out.println("enable player heat map called");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webEngine.executeScript("enablePlayerHeatMap()");
			}
		});
	}
	
	/**
	 * Disables the player heatmap on the map.
	 * 
	 * @see WebEngine#executeScript(String)
	 * @see "https://developers.google.com/maps/documentation/javascript/reference#HeatmapLayer"
	 */
	public void disablePlayerHeatMap() {
		System.out.println("disable player heat map called");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webEngine.executeScript("disablePlayerHeatMap()");
			}
		});
	}
	
	/**
	 * Gets the launch options from the Settings tab to be applied to joining a server.
	 * 
	 * @return the launch options as a String
	 */
	public String getLaunchOptions() {
		if ( !settingsTab.tglbtnEnableLaunchOptions.isSelected() ) {
			return "";
		}
		String opts = "";
		if ( !settingsTab.tfLaunchResX.getText().equals("") ) {
			opts += "&-resx=" + settingsTab.tfLaunchResX.getText();
		}
		if ( !settingsTab.tfLaunchResY.getText().equals("") ) {
			opts += "&-resy=" + settingsTab.tfLaunchResY.getText();
		}
		String window = (String) settingsTab.cbScreen.getSelectedItem();
		if ( window.equals("Fullscreen") ) {
			opts += "&-fullscreen";
		} else if ( window.equals("Windowed") ) {
			opts += "&-windowed";
		} else if ( window.equals("Borderless Window") ) {
			opts += "&-borderless";
		}
		return opts;
	}
	
	/**
	 * Adds a server found during a refresh to the database. If the server already exists, this does nothing.
	 * 
	 * @param cs the {@link ChivServer} to add to the database
	 * @see SQLiteConnection
	 * @see SQLiteStatement
	 * @see SQLiteException
	 */
	public void addServerToDatabase(ChivServer cs) {
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS servers" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"ip varchar(255) not null default ''," +
					"queryport varchar(255) not null default ''," +
					"gameport varchar(255) not null default ''" +
					")");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			
			st = db.prepare("SELECT id FROM servers WHERE ip = '" + cs.mIP + "' AND queryport = '" + cs.mQueryPort +
					"' AND gameport = '" + cs.mGamePort + "'");
			boolean exists = false;
			try {
				st.step();
				if ( st.hasRow() ) {
					exists = true;
				}
			} finally {
				st.dispose();
			}
			
			if ( !exists ) {
				st = db.prepare("INSERT INTO servers (ip, queryport, gameport) VALUES ( '" + cs.mIP + "', '" +
						cs.mQueryPort + "', '" + cs.mGamePort + "')");
				try {
					st.step();
				} finally {
					st.dispose();
				}
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
	}
	
	/**
	 * Updates the database of servers found during a refresh. These are used for looking up server
	 * ip addresses and gameports from the Friends tab to find the queryport to query the server
	 * for more information. This method uses {@link MainWindow#servers} to update the database.
	 */
	public void updateServerDB() {
		if ( mw.servers != null ) {
			new Thread() {
				@Override
				public void run() {
					@SuppressWarnings("unchecked")
					Vector<ChivServer> serversCopy = (Vector<ChivServer>) mw.servers.clone();
					for ( ChivServer cs : serversCopy ) {
						addServerToDatabase(cs);
					}
				}
			}.start();
		}
	}
	
	/**
	 * Updates the database of servers found during a refresh. These are used for looking up server
	 * ip addresses and gameports from the Friends tab to find the queryport to query the server
	 * for more information.
	 * 
	 * @param s the {@link Vector} of {@link ChivServer} to update into the database
	 */
	public void updateServerDB(final Vector<ChivServer> s) {
		if ( s != null ) {
			new Thread() {
				@Override
				public void run() {
					for ( ChivServer cs : s ) {
						addServerToDatabase(cs);
					}
				}
			}.start();
		}
	}
	
	/**
	 * Searches the database for a server matching the ip address and gameport to find the
	 * associated queryport.
	 * 
	 * @param ip the ip address of the server
	 * @param gameport the gameport of the server
	 * @return returns the {@link ChivServer}; or null if not found
	 * @see SQLiteConnection
	 * @see SQLiteStatement
	 * @see SQLiteException
	 */
	public ChivServer getServerFromDB(String ip, String gameport) {
		String queryport = "";
		SQLiteConnection db = new SQLiteConnection(new File("browserdb"));
		try {
			db.open(true);		
			SQLiteStatement st = db.prepare("CREATE TABLE IF NOT EXISTS servers" +
					"(" +
					"id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"ip varchar(255) not null default ''," +
					"queryport varchar(255) not null default ''," +
					"gameport varchar(255) not null default ''" +
					")");
			try {
				st.step();
			} finally {
				st.dispose();
			}
			st = db.prepare("SELECT queryport FROM servers WHERE ip = '" + ip + "' AND gameport = '" + gameport + "'");	
			try {
				st.step();
				if ( st.hasRow() ) {
					queryport = st.columnString(0);
				}
			} finally {
				st.dispose();
			}
			
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
		db.dispose();
		if ( !queryport.equals("") ) {
			return ChivServer.createChivServer(mw, ip, Integer.parseInt(queryport));
		}
		return null;
	}

	/**
	 * Deep copies a {@link Vector} of {@link ChivServer}.
	 * 
	 * @param s the {@link Vector} of {@link ChivServer} to be copied
	 * @return a deep copy of the {@link Vector} of {@link ChivServer}
	 */
	public Vector<ChivServer> deepCopyCSVector(Vector<ChivServer> s) {
		Vector<ChivServer> result = new Vector<ChivServer>();
		for (ChivServer cs : s) {
			result.add(cs.clone());
		}
		return s;
	}

	/**
	 * Saves all of the game settings from the Settings Tab to the config
	 * files.
	 * 
	 * @see SettingsTab#writeUDKConfigSetting(String, String, String)
	 */
	public void saveGameConfig() {
		settingsTab.writeUDKConfigSetting("UDKSystemSettings.ini", "AmbientOcclusion", (String)settingsTab.tblGameSettings.getValueAt(0, 1));
		settingsTab.writeUDKConfigSetting("UDKSystemSettings.ini", "Bloom", (String)settingsTab.tblGameSettings.getValueAt(1, 1));
		settingsTab.writeUDKConfigSetting("UDKSystemSettings.ini", "DynamicLights", (String)settingsTab.tblGameSettings.getValueAt(2, 1));
		settingsTab.writeUDKConfigSetting("UDKSystemSettings.ini", "DynamicShadows", (String)settingsTab.tblGameSettings.getValueAt(3, 1));
		settingsTab.writeUDKConfigSetting("UDKSystemSettings.ini", "MotionBlur", (String)settingsTab.tblGameSettings.getValueAt(4, 1));
		settingsTab.writeUDKConfigSetting("UDKSystemSettings.ini", "ResX", (String)settingsTab.tblGameSettings.getValueAt(5, 1));
		settingsTab.writeUDKConfigSetting("UDKSystemSettings.ini", "ResY", (String)settingsTab.tblGameSettings.getValueAt(6, 1));
		settingsTab.writeUDKConfigSetting("UDKSystemSettings.ini", "UseVsync", (String)settingsTab.tblGameSettings.getValueAt(7, 1));
	}
	
	/**
	 * Set up constraints so that the user supplied component and the
     * background image label overlap and resize identically
     * 
     * @see "http://www.java-tips.org/java-se-tips/javax.swing/wrap-a-swing-jcomponent-in-a-background-image.html"
	 */
    private static final GridBagConstraints gbc;
    static {
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
    }
    
    /**
     * Wraps a Swing {@link JComponent} in a background image. Simply invokes the overloded
     * variant with Top/Leading alignment for background image.
     *
     * @param component to wrap in the a background image
     * @param backgroundIcon the background image (Icon)
     * @return the wrapping JPanel
     * @see "http://www.java-tips.org/java-se-tips/javax.swing/wrap-a-swing-jcomponent-in-a-background-image.html"
     */
    public static JPanel wrapInBackgroundImage(JComponent component,
            Icon backgroundIcon) {
        return wrapInBackgroundImage(
                component,
                backgroundIcon,
                JLabel.TOP,
                JLabel.LEADING);
    }
    
    /**
     * Wraps a Swing {@link JComponent} in a background image. The vertical and horizontal
     * alignment of background image can be specified using the alignment
     * constants from {@link JLabel}.
     *
     * @param component to wrap in the a background image
     * @param backgroundIcon the background image (Icon)
     * @param verticalAlignment vertical alignment. See constants in JLabel.
     * @param horizontalAlignment horizontal alignment. See constants in JLabel.
     * @return the wrapping JPanel
     * @see "http://www.java-tips.org/java-se-tips/javax.swing/wrap-a-swing-jcomponent-in-a-background-image.html"
     */
    public static JPanel wrapInBackgroundImage(JComponent component,
            Icon backgroundIcon,
            int verticalAlignment,
            int horizontalAlignment) {
        
        // make the passed in swing component transparent
        component.setOpaque(false);
        
        // create wrapper JPanel
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        
        // add the passed in swing component first to ensure that it is in front
        backgroundPanel.add(component, gbc);
        
        // create a label to paint the background image
        JLabel backgroundImage = new JLabel(backgroundIcon);
        
        // set minimum and preferred sizes so that the size of the image
        // does not affect the layout size
        backgroundImage.setPreferredSize(new Dimension(1,1));
        backgroundImage.setMinimumSize(new Dimension(1,1));
        
        // align the image as specified.
        backgroundImage.setVerticalAlignment(verticalAlignment);
        backgroundImage.setHorizontalAlignment(horizontalAlignment);
        
        // add the background label
        backgroundPanel.add(backgroundImage, gbc);
        
        // return the wrapper
        return backgroundPanel;
    }
    
}