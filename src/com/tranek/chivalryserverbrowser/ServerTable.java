package com.tranek.chivalryserverbrowser;
import java.awt.Cursor;
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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * 
 * The {@link JTable} wrapped in a {@link JScrollPane} that displays the servers queried
 * on each server list tab.
 *
 */
@SuppressWarnings("serial")
public class ServerTable extends JScrollPane {
	
	/** The JTable that contains all of the queried servers. */
	private JTable serverListTable;
	/** The data model for the server list table. */
	protected TableModel dataModel;
	/** The column headers for the server list table. */
	private final String[] serverListColumnHeaders = {"Server Name", "IP Address:Port", "Type", "Map", "Players", "Ping",
			"Location", "Pass", "Min Rank", "Max Rank"};
	/** A reference to the MainWindow. */
	private MainWindow mw;
	
	/**
	 * Creates a new ServerTable.
	 * 
	 * @param sl the ServerListInterface that this ServerTable belongs to
	 */
	public ServerTable(final ServerListInterface sl) {
		mw = sl.getMW();
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
		
		serverListTable = new JTable(dataModel);
		serverListTable.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int row = serverListTable.rowAtPoint(e.getPoint());
				int col = serverListTable.columnAtPoint(e.getPoint());
				
				if ( col == 1 && ((String)dataModel.getValueAt(row, col)) != null ) {
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
		serverListTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				final int tableRow = serverListTable.rowAtPoint(e.getPoint());
				final int tableCol = serverListTable.columnAtPoint(e.getPoint());
				
				// Fix by gregcau http://www.chivalrythegame.com/forums/viewtopic.php?f=69&t=10664&start=30#p99728
				final int row = serverListTable.convertRowIndexToModel(tableRow);
	            final int col = serverListTable.convertColumnIndexToModel(tableCol);
				
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
							mw.joinServer(urlstring, ip, port, serverName, sl);
						}
					} else {	
						String urlstring = "steam://run/219640/en/" + ip + ":" + port;
						mw.joinServer(urlstring, ip, port, serverName, sl);
					}
				}
				
				// Double click server name
				if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && col == 0 ) {
					//String name = (String)dataModel.getValueAt(row, col);
					String ip_port = ((String)dataModel.getValueAt(row, 1)).substring(26);
					mw.addServerTab(ip_port, sl, false);
				}
				
				// Double click location
				if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && col == 6 ) {
					String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
					stripped = stripped.split("<")[0];
					String[] ipaddress = stripped.split(":");
					String ip = ipaddress[0];
					String port = ipaddress[1];
					final ChivServer cs = mw.findChivServer(ip, port, sl);
					mw.showInMap(cs);
				}

				if ( e.getButton() == MouseEvent.BUTTON3 ) {
					serverListTable.setRowSelectionInterval(tableRow, tableRow);
					JPopupMenu rmbServerPopup = new JPopupMenu();
					if ( sl == mw.serverListTab || sl == mw.serverListHistoryTab ) {
						String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
						stripped = stripped.split("<")[0];
						String[] ipaddress = stripped.split(":");
						String ip = ipaddress[0];
						String port = ipaddress[1];
						final ChivServer cs = mw.findChivServer(ip, port, sl);
						JMenuItem popFav = new JMenuItem("Add to favorites");
						popFav.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								mw.addFavorite(cs);
							}
						});
						rmbServerPopup.add(popFav);
					}
					JMenuItem popInfo = new JMenuItem("Server details");
					popInfo.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
							stripped = stripped.split("<")[0];
							mw.addServerTab(stripped, sl, true);
						}
					});
					rmbServerPopup.add(popInfo);
					JMenuItem popCopyName = new JMenuItem("Copy server name to Clipboard");
					popCopyName.addActionListener(new ActionListener() {
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
					rmbServerPopup.add(popCopyName);
					JMenuItem popCopyIP = new JMenuItem("Copy server IP:port to Clipboard");
					popCopyIP.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
							stripped = stripped.split("<")[0];
							StringSelection stringSelection = new StringSelection( stripped );
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
							String stripped = ((String)dataModel.getValueAt(row, 1)).substring(26);
							stripped = stripped.split("<")[0];
							String[] ipaddress = stripped.split(":");
							String ip = ipaddress[0];
							String port = ipaddress[1];
							final ChivServer cs = mw.findChivServer(ip, port, sl);
							mw.showInMap(cs);
						}
					});
					rmbServerPopup.add(popShowInMap);
					rmbServerPopup.show(serverListTable, e.getPoint().x, e.getPoint().y);
				}
			}
		});
		serverListTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Server name
		serverListTable.getColumnModel().getColumn(1).setPreferredWidth(90); // IP address
		//serverListTable.getColumnModel().getColumn(2).setPreferredWidth(20); // Game type
		serverListTable.getColumnModel().getColumn(2).setMaxWidth(50); // Game type
		serverListTable.getColumnModel().getColumn(3).setPreferredWidth(130); // Map name
		serverListTable.getColumnModel().getColumn(4).setPreferredWidth(30); // Players
		//serverListTable.getColumnModel().getColumn(5).setPreferredWidth(20); // Ping
		serverListTable.getColumnModel().getColumn(5).setMaxWidth(50); // Ping
		serverListTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Location
		//serverListTable.getColumnModel().getColumn(7).setPreferredWidth(30); // Password
		serverListTable.getColumnModel().getColumn(7).setMaxWidth(40); // Location
		serverListTable.getColumnModel().getColumn(8).setPreferredWidth(30); // Min rank
		serverListTable.getColumnModel().getColumn(9).setPreferredWidth(30); // Max rank
		serverListTable.setAutoCreateRowSorter(true);
		TableRowSorter<?> trs = (TableRowSorter<?>) serverListTable.getRowSorter();
		trs.setComparator(4, new PlayerComparator());
		
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		setViewportView(serverListTable);
	}
	
}
