package com.tranek.chivalryserverbrowser;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;

import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.github.koraktor.steamcondenser.steam.community.XMLData;

@SuppressWarnings("serial")
public class FindPlayerTab extends JPanel {

	private final MainWindow mw;
	private final FindPlayerTab fpt;
	private JTextField tfURL;
	protected Vector<ChivServer> servers;
	private JTable tblServers;
	protected TableModel dataModel;
	private final String[] serverListColumnHeaders = {"Server Name", "IP Address:Port", "Type", "Map", "Players", "Ping",
			"Location", "Pass", "Min Rank", "Max Rank"};
	private FindPlayerQuerier fpq;
	protected JLabel lblNickName;
	protected JLabel lblSteamId;
	
	public FindPlayerTab(MainWindow mw) { 
		super();
		this.mw = mw;
		fpt = this;
		fpq = new FindPlayerQuerier(this);
		initialize();
	}
	
	public void initialize() {
		setLayout(null);
		
		JLabel lblSteamCommunityUrl = new JLabel("Steam Community URL");
		lblSteamCommunityUrl.setBounds(57, 13, 146, 16);
		add(lblSteamCommunityUrl);
		
		tfURL = new JTextField();
		tfURL.setBounds(196, 10, 619, 22);
		add(tfURL);
		tfURL.setColumns(10);
		tfURL.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ( e.getButton() == MouseEvent.BUTTON3 ) {
					JPopupMenu rmbURLPopup = new JPopupMenu();
					JMenuItem popPaste = new JMenuItem("Paste");
					popPaste.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							Transferable contents = clipboard.getContents(null);
						    boolean hasTransferableText =
						      (contents != null) &&
						      contents.isDataFlavorSupported(DataFlavor.stringFlavor);
						    if ( hasTransferableText ) {
						    	try {
									String result = (String)contents.getTransferData(DataFlavor.stringFlavor);
									tfURL.setText(result);
								} catch (UnsupportedFlavorException
										| IOException e1) {
									e1.printStackTrace();
								} 
						    }
						}
					});
					rmbURLPopup.add(popPaste);
					rmbURLPopup.show(tfURL, e.getPoint().x, e.getPoint().y);
				}
			}
		});
		
		JButton btnFindPlayer = new JButton("Find Player");
		btnFindPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				findPlayer();
			}
		});
		btnFindPlayer.setBounds(848, 9, 97, 25);
		add(btnFindPlayer);
		
		JLabel lblNickNameLabel = new JLabel("Nickname:");
		lblNickNameLabel.setBounds(57, 45, 75, 16);
		add(lblNickNameLabel);
		
		lblNickName = new JLabel();
		lblNickName.setBounds(144, 45, 56, 16);
		add(lblNickName);
		
		dataModel = new DefaultTableModel(serverListColumnHeaders, 0) {
			public boolean isCellEditable(int row, int column){  
				return false;  
			}
			// Treats the Player count, Ping, Min Rank, and Max Rank column as a number so that it is not sorted as a String
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 4 || columnIndex == 5 || columnIndex == 8 || columnIndex == 9) {
					return Integer.class;
				}
				return super.getColumnClass(columnIndex);
			}
		};
		
		tblServers = new JTable(dataModel);
		JScrollPane spServers = new JScrollPane(tblServers);
		spServers.setBounds(0, 144, 1015, 190);
		spServers.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		spServers.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		tblServers.getColumnModel().getColumn(0).setPreferredWidth(200); // Server name
		tblServers.getColumnModel().getColumn(1).setPreferredWidth(90); // IP address
		//serverListTable.getColumnModel().getColumn(2).setPreferredWidth(20); // Game type
		tblServers.getColumnModel().getColumn(2).setMaxWidth(50); // Game type
		tblServers.getColumnModel().getColumn(3).setPreferredWidth(130); // Map name
		tblServers.getColumnModel().getColumn(4).setPreferredWidth(30); // Players
		//serverListTable.getColumnModel().getColumn(5).setPreferredWidth(20); // Ping
		tblServers.getColumnModel().getColumn(5).setMaxWidth(50); // Ping
		tblServers.getColumnModel().getColumn(6).setPreferredWidth(80); // Location
		//serverListTable.getColumnModel().getColumn(7).setPreferredWidth(30); // Password
		tblServers.getColumnModel().getColumn(7).setMaxWidth(40); // Location
		tblServers.getColumnModel().getColumn(8).setPreferredWidth(30); // Min rank
		tblServers.getColumnModel().getColumn(9).setPreferredWidth(30); // Max rank
		tblServers.setAutoCreateRowSorter(true);
		
		tblServers.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int row = tblServers.rowAtPoint(e.getPoint());
				int col = tblServers.columnAtPoint(e.getPoint());
				
				if ( col == 1 && ((String)dataModel.getValueAt(row, col)) != null ) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		tblServers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				final int tableRow = tblServers.rowAtPoint(e.getPoint());
				final int tableCol = tblServers.columnAtPoint(e.getPoint());
				
				// Fix by gregcau http://www.chivalrythegame.com/forums/viewtopic.php?f=69&t=10664&start=30#p99728
				final int row = tblServers.convertRowIndexToModel(tableRow);
	            final int col = tblServers.convertColumnIndexToModel(tableCol);
				
				if ( e.getButton() == MouseEvent.BUTTON1 && col == 1 &&
						((String)dataModel.getValueAt(row, col)) != null ) {
					
					String stripped = ((String)dataModel.getValueAt(row, col)).substring(26);
					stripped = stripped.split("<")[0];
					String[] ipaddress = stripped.split(":");
					String ip = ipaddress[0];
					String port = ipaddress[1];
					String serverName = (String)dataModel.getValueAt(row, 0);
					
					if ( ((String)dataModel.getValueAt(row, 7)).equals("Yes") ) {
						String password = javax.swing.JOptionPane.showInputDialog("Please enter a password");
						if ( password != null ) {
							String urlstring = "steam://run/219640/en/" + ip + ":" + port + "%3fpassword=" + password;
							mw.joinServer(urlstring, ip, port, serverName, getServerFromList(ip, port));
						}
					} else {	
						String urlstring = "steam://run/219640/en/" + ip + ":" + port;
						mw.joinServer(urlstring, ip, port, serverName, getServerFromList(ip, port));
					}
				}
				
				// Double click server name
				if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && col == 0 ) {
					String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
					stripped = stripped.split("<")[0];
					String[] ipaddress = stripped.split(":");
					String ip = ipaddress[0];
					String port = ipaddress[1];
					mw.addServerTab(getServerFromList(ip, port), true);
				}
				
				// Double click location
				if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && col == 6 ) {
					String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
					stripped = stripped.split("<")[0];
					String[] ipaddress = stripped.split(":");
					String ip = ipaddress[0];
					String port = ipaddress[1];
					final ChivServer cs = getServerFromList(ip, port);
					mw.showInMap(cs);
				}

				if ( e.getButton() == MouseEvent.BUTTON3 ) {
					tblServers.setRowSelectionInterval(tableRow, tableRow);
					JPopupMenu rmbServerPopup = new JPopupMenu();
					String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
					stripped = stripped.split("<")[0];
					String[] ipaddress = stripped.split(":");
					String ip = ipaddress[0];
					String port = ipaddress[1];
					final ChivServer cs = getServerFromList(ip, port);
					JMenuItem popFav = new JMenuItem("Add to favorites");
					popFav.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							mw.addFavorite(cs);
						}
					});
					rmbServerPopup.add(popFav);
					JMenuItem popInfo = new JMenuItem("Server details");
					popInfo.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
							stripped = stripped.split("<")[0];
							mw.addServerTab(cs, true);
						}
					});
					rmbServerPopup.add(popInfo);
					JMenuItem popCopy = new JMenuItem("Copy server name to Clipboard");
					popCopy.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							StringSelection stringSelection = new StringSelection( (String)dataModel.getValueAt(row, 0) );
							clipboard.setContents(stringSelection, new ClipboardOwner() {
								@Override
								public void lostOwnership(Clipboard clipboard, Transferable contents) {}
							});
						}
					});
					rmbServerPopup.add(popCopy);
					JMenuItem popShowInMap = new JMenuItem("Show server in map");
					popShowInMap.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
							stripped = stripped.split("<")[0];
							String[] ipaddress = stripped.split(":");
							String ip = ipaddress[0];
							String port = ipaddress[1];
							final ChivServer cs = getServerFromList(ip, port);
							mw.showInMap(cs);
						}
					});
					rmbServerPopup.add(popShowInMap);
					rmbServerPopup.show(tblServers, e.getPoint().x, e.getPoint().y);
				}
			}
		});
		
		add(spServers);
		
		JLabel lblServersFound = new JLabel("Servers found:");
		lblServersFound.setBounds(12, 119, 97, 16);
		add(lblServersFound);
		
		JLabel lblSteamIdLabel = new JLabel("Steam ID:");
		lblSteamIdLabel.setBounds(57, 74, 75, 16);
		add(lblSteamIdLabel);
		
		lblSteamId = new JLabel();
		lblSteamId.setBounds(144, 74, 281, 16);
		add(lblSteamId);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		btnStop.setBounds(848, 47, 97, 25);
		add(btnStop);
	}
	
	public void findPlayer() {
		new Thread() {
			public void run() {
				mw.stopAllRefreshing();
				servers = new Vector<ChivServer>();
				mw.printlnMC("Getting Steam Community info...");
				if ( querySteamCommunity(tfURL.getText()) ) {
					if ( !lblNickName.getText().equals("") ) {
						mw.printlnMC("Retrieved Steam Community info.");
						mw.printlnMC("Querying Master Server to find player...");
						if ( fpq != null && fpq.isRefreshing() ) {
							stopRefreshing();
						}
						fpq = new FindPlayerQuerier(fpt);
						fpq.start();
					} else {
						mw.printlnMC("Missing player nickname! Getting the Steam Community info must have failed.");
					}
				} else {
					mw.printlnMC("Failed to get Steam Community info.");
				}
			}
		}.start();
	}
	
	public boolean querySteamCommunity(String url) {
		boolean success = false;
		try {
			XMLData profile = new XMLData(url + "?xml=1");
			
			if(profile.hasElement("error")) {
				System.out.println("Error!");
				return false;
			}
			String nickname  = profile.getUnescapedString("steamID");
			Long steamID = profile.getLong("steamID64");
			
			if ( nickname != null && steamID != null ) {
				lblNickName.setText(nickname);
				lblSteamId.setText("" + steamID);
				success = true;
			}
		} catch ( IOException | ParserConfigurationException | SAXException e ) {
			e.printStackTrace();
		}
		return success;
	}
	
	public void stop() {
		mw.stopAllRefreshing();
	}
	
	public void stopRefreshing() {
		if ( fpq != null ) {
			fpq.terminate();
			mw.printlnMC("Canceled search for player.");
		}
	}
	
	public MainWindow getMW() {
		return mw;
	}
	
	public ChivServer getServerFromList(String ip, String gameport) {
		for ( ChivServer cs : servers ) {
			if ( cs.mIP.equals(ip) && cs.mGamePort.equals(gameport) ) {
				return cs;
			}
		}
		return null;
	}
	
}
