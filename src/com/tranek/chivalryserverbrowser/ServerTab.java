package com.tranek.chivalryserverbrowser;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import com.github.koraktor.steamcondenser.steam.SteamPlayer;

/**
 * 
 * The tab created for an individual server to show more details about that server.
 *
 */
@SuppressWarnings("serial")
public class ServerTab extends JPanel{
	
	/** The ChivServer for this tab. */
	private final ChivServer cs;
	/** The JTable for the list of players currently on the server. */
	private final JTable playerListTable;
	/** Column headers for the player list table. */
	private final String[] playerListColumnHeaders = {"Player Name", "Score", "Time Connected"};
	/** The data model for the player list table. */
	private final DefaultTableModel dataModel;
	/** Reference to the MainWindow for its utility methods. */
	private final MainWindow mw;
	/** The ServerQuerier for querying the server. */
	private ServerQuerier sq;
	/** JLabel for the server name. */
	private final JLabel lblServerName;
	/** JLabel for the players. */
	private final JLabel lblPlyrs;
	/** JLabel for the number of players. */
	private final JLabel lblPlayers;
	/** JLabel for the map name. */
	private final JLabel lblMapName;
	/** Reference to itself. */
	private final ServerTab st;
	
	/** 
	 * Creates a new ServerTab.
	 */
	public ServerTab(MainWindow MW, ChivServer CS) {
		super();
		this.cs = CS;
		this.mw = MW;
		st = this;
		
		JPanel panel = new JPanel();
		panel.setBounds(new Rectangle(0, 0, 1015, 140));
		panel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ( e.getButton() == MouseEvent.BUTTON3 ) {
					JPopupMenu rmbServerPopup = new JPopupMenu();
					JMenuItem popFav = new JMenuItem("Add to favorites");
					popFav.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							mw.addFavorite(cs);
						}
					});
					rmbServerPopup.add(popFav);
					JMenuItem popCopyName = new JMenuItem("Copy server name to Clipboard");
					popCopyName.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							StringSelection stringSelection = new StringSelection( cs.mName );
							clipboard.setContents(stringSelection, new ClipboardOwner() {
								@Override
								public void lostOwnership(Clipboard clipboard, Transferable contents) {}
							});
						}
					});
					rmbServerPopup.add(popCopyName);
					JMenuItem popCopyIP = new JMenuItem("Copy server IP:port to Clipboard");
					popCopyIP.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							String ip = cs.mIP + ":" + cs.mGamePort;
							StringSelection stringSelection = new StringSelection( ip );
							clipboard.setContents(stringSelection, new ClipboardOwner() {
								@Override
								public void lostOwnership(Clipboard clipboard, Transferable contents) {}
							});
						}
					});
					rmbServerPopup.add(popCopyIP);
					JMenuItem popShowInMap = new JMenuItem("Show server in map");
					popShowInMap.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							mw.showInMap(cs);
						}
					});
					rmbServerPopup.add(popShowInMap);
					rmbServerPopup.show(st, e.getPoint().x, e.getPoint().y);
				}
			}
		});
		setLayout(null);
		add(panel);
		
		JLabel lblServerNm = new JLabel("Server Name:");
		lblServerNm.setBounds(12, 5, 80, 16);
		
		lblServerName = new JLabel("");
		lblServerName.setBounds(100, 5, 609, 16);
		
		lblServerName.setText(cs.mName);
		
		lblPlyrs = new JLabel("Players:");
		lblPlyrs.setBounds(783, 51, 73, 16);
		
		lblPlayers = new JLabel(cs.mCurrentPlayers + " / " + cs.mMaxPlayers);
		lblPlayers.setBounds(841, 51, 135, 16);
		panel.add(lblPlayers);

		JButton btnJoinServer = new JButton("Join Server");
		btnJoinServer.setBounds(214, 102, 118, 25);
		btnJoinServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				joinServer();
			}
		});
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(626, 102, 95, 25);
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dataModel.setRowCount(0);
				refresh();
			}
		});
		
		JLabel lblLocation = new JLabel("Location:");
		lblLocation.setBounds(777, 28, 52, 16);
		JLabel lblLoc = new JLabel("<html><U><FONT COLOR=BLUE>" + cs.mLocation + "</FONT></U></html>");
		lblLoc.setBounds(841, 28, 142, 16);
		lblLoc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mw.showInMap(cs);
			}
		});
		lblLoc.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
		lblLoc.setToolTipText("Click to open server in the map!");
		
		JLabel lblMap = new JLabel("Map:");
		lblMap.setBounds(12, 28, 29, 16);
		
		lblMapName = new JLabel();
		lblMapName.setBounds(100, 28, 169, 16);
		lblMapName.setText(cs.mMap);
		
		JLabel lblGM = new JLabel("Game Mode:");
		lblGM.setBounds(12, 51, 73, 16);
		
		JLabel lblGameMode = new JLabel();
		lblGameMode.setBounds(100, 51, 101, 16);
		lblGameMode.setText(cs.mGameMode);
		
		JLabel lblPng = new JLabel("Ping:");
		lblPng.setBounds(799, 74, 29, 16);
		
		JLabel lblPing = new JLabel();
		lblPing.setBounds(841, 74, 86, 16);
		lblPing.setText(cs.mPing + " ms");
		
		JLabel lblIpAddress = new JLabel("IP address:");
		lblIpAddress.setBounds(764, 5, 80, 16);
		
		JLabel lblIp = new JLabel();
		lblIp.setBounds(841, 5, 135, 16);
		lblIp.setText(cs.mIP + ":" + cs.mGamePort);
		
		JLabel lblMinR = new JLabel("Min Rank:");
		lblMinR.setBounds(435, 28, 57, 16);
		
		JLabel lblMinRank = new JLabel();
		lblMinRank.setBounds(509, 28, 65, 16);
		lblMinRank.setText(cs.mMinRank);
		
		JLabel lblMaxR = new JLabel("Max Rank:");
		lblMaxR.setBounds(432, 51, 60, 16);
		
		JLabel lblMaxRank = new JLabel();
		lblMaxRank.setBounds(509, 51, 65, 16);
		lblMaxRank.setText(cs.mMaxRank);
		
		JLabel lblPersp = new JLabel("Perspective:");
		lblPersp.setBounds(422, 74, 85, 16);
		
		JLabel lblPerspective = new JLabel();
		lblPerspective.setBounds(509, 74, 101, 16);
		if ( cs.mPerspective.equals("0") ) {
			lblPerspective.setText("Any");
		} else if ( cs.mPerspective.equals("1") ) {
			lblPerspective.setText("First Person");
		} else if ( cs.mPerspective.equals("2") ) {
			lblPerspective.setText("Third Person");
		} else {
			lblPerspective.setText("Any");
		}
		
		JLabel lblPass = new JLabel("Password:");
		lblPass.setBounds(12, 74, 73, 16);
		
		JLabel lblPassword = new JLabel();
		lblPassword.setBounds(100, 74, 57, 17);
		if ( cs.mHasPassword.equals("1") ) {
			lblPassword.setText("Yes");
		} else {
			lblPassword.setText("No");
		}
		panel.setLayout(null);
		panel.add(lblMap);
		panel.add(lblMapName);
		panel.add(lblMinR);
		panel.add(lblMinRank);
		panel.add(lblMaxR);
		panel.add(lblMaxRank);
		panel.add(lblPlyrs);
		panel.add(btnJoinServer);
		panel.add(lblServerNm);
		panel.add(lblServerName);
		panel.add(lblPass);
		panel.add(lblPassword);
		panel.add(lblGM);
		panel.add(lblGameMode);
		panel.add(lblIpAddress);
		panel.add(lblIp);
		panel.add(lblPersp);
		panel.add(lblPerspective);
		panel.add(btnRefresh);
		panel.add(lblPng);
		panel.add(lblPing);
		panel.add(lblLocation);
		panel.add(lblLoc);
		
		dataModel = new DefaultTableModel(playerListColumnHeaders, 0) {
			public boolean isCellEditable(int row, int column){  
				return false;  
			}
			// Treats the Score column as a number so that it is not sorted as a String
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 1) {
					return Integer.class;
				}
				return super.getColumnClass(columnIndex);
			}
		};
		playerListTable = new JTable(dataModel);
		playerListTable.setBounds(437, 121, -426, 166);
		
		JScrollPane playerListScrollPane = new JScrollPane(playerListTable);
		playerListScrollPane.setBounds(0, 140, 1015, 442);
		playerListScrollPane.setPreferredSize(new Dimension(452, 380));
		playerListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		playerListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(playerListScrollPane);
		
		playerListTable.setAutoCreateRowSorter(true);
		TableRowSorter<?> trs = (TableRowSorter<?>) playerListTable.getRowSorter();
		trs.setComparator(1, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int p1 = Integer.parseInt(o1);
				int p2 = Integer.parseInt(o2);
				if ( p1 < p2 ) {
					return -1;
				} else if ( p1 > p2 ) {
					return 1;
				}
				return 0;
			}});
		trs.setComparator(2, new TimeConnectedComparator());
		
		
		refresh();
	}
	
	/** 
	 * Launches the game and connects to the server.
	 */
	public void joinServer() {
		boolean hasPassword = cs.mHasPassword.equals("1");
		String ip = cs.mIP;
		String port = cs.mGamePort;
		String serverName = cs.mName;
		if ( hasPassword ) {
			String password = javax.swing.JOptionPane.showInputDialog("Please enter a password");
			if ( password != null ) {
				String urlstring = "steam://run/219640/en/" + ip + ":" + port + "%3fpassword=" + password;
				mw.joinServer(urlstring, ip, port, serverName, cs);
			}
		} else {	
			String urlstring = "steam://run/219640/en/" + ip + ":" + port;
			mw.joinServer(urlstring, ip, port, serverName, cs);
		}
	}

	/**
	 * Refreshes the information about the server.
	 */
	public void refresh() {
		sq = new ServerQuerier();
		sq.start();
	}
	
	/**
	 * 
	 * Queries the server for its information.
	 *
	 */
	private class ServerQuerier extends Thread {
		public void run() {
			mw.printlnMC("Querying server for player list...");
			ServerQuery sq = new ServerQuery(cs);
			HashMap<String, Object> info = sq.getInfo();
			lblServerName.setText((String)info.get("serverName"));
			lblPlayers.setText(info.get("numberOfPlayers") + " / " +
					info.get("maxPlayers"));
			lblMapName.setText((String)info.get("mapName"));
			HashMap<String, SteamPlayer> players = sq.getPlayers();
			int numPlayers = 0;
			for ( Object value : players.values() ) {
				SteamPlayer player = (SteamPlayer)value;
				float connectedTime = player.getConnectTime();
				int hours = (int) Math.floor(connectedTime / 3600);
				int minutes = (int) Math.floor( (connectedTime - hours*3600) / 60 );
				int seconds = (int) Math.floor(connectedTime - hours*3600 - minutes*60);
				String connected = hours + " h " + minutes + " m " + seconds + " s";
				Object[] rowData = {player.getName(), "" + player.getScore(), connected};
				dataModel.addRow(rowData);
				numPlayers++;
			}
			mw.printlnMC("Found " + numPlayers + " players on the server.");
		}
	}
	
	/**
	 * 
	 * Comparator to compare the time connected for each connected player.
	 *
	 */
	private class TimeConnectedComparator implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			String[] tokens1 = o1.split(" ");
			String[] tokens2 = o2.split(" ");			
			int h1 = Integer.parseInt(tokens1[0]);
			int h2 = Integer.parseInt(tokens2[0]);
			int m1 = Integer.parseInt(tokens1[2]);
			int m2 = Integer.parseInt(tokens2[2]);
			int s1 = Integer.parseInt(tokens1[4]);
			int s2 = Integer.parseInt(tokens2[4]);
			if ( h1 < h2 ) {
				return -1;
			} else if ( h1 > h2 ) {
				return 1;
			} else {
				if ( m1 < m2 ) {
					return -1;
				} else if ( m1 > m2 ) {
					return 1;
				} else {
					if ( s1 < s2 ) {
						return -1;
					} else if ( s1 > s2 ) {
						return 1;
					}
				}
			}
			return 0;
		}	
	}
	
}
