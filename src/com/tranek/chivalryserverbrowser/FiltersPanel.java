package com.tranek.chivalryserverbrowser;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;


@SuppressWarnings("serial")
public class FiltersPanel extends JPanel {
	private MainWindow mw;
	public JTextField serverNameFilter;
	public JComboBox<String> gameModeList;
	public JTextField maxPingFilter;
	public JTextField minRankFilter;
	public JTextField maxRankFilter;
	public JCheckBox chckbxHidePasswordedServers;
	public JCheckBox chckbxHideEmptyServers;
	public JCheckBox chckbxHideFullServers;
	public JCheckBox chckbxOfficialServersOnly;
	public JComboBox<String> cBPerspective;
	public JSpinner spNumThreads;
	
	public FiltersPanel(final ServerList sl) {
		this.mw = sl.getMW();
		String[] gameModeCBModelList = {"All", "CTF", "DUEL", "FFA", "KOTH", "LTS", "TDM", "TO"};
		ComboBoxModel<String> gameModeCBModel = new DefaultComboBoxModel<String>(gameModeCBModelList);
		
		JLabel lblServerName = new JLabel("Server Name");
		
		serverNameFilter = new JTextField();
		serverNameFilter.setColumns(10);
		serverNameFilter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if ( e.getButton() == MouseEvent.BUTTON3 ) {
					JPopupMenu rmbPopup = new JPopupMenu();
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
									serverNameFilter.setText(result);
								} catch (UnsupportedFlavorException
										| IOException e1) {
									e1.printStackTrace();
								} 
						    }
						}
					});
					rmbPopup.add(popPaste);
					rmbPopup.show(serverNameFilter, e.getPoint().x, e.getPoint().y);
				}
			}
		});
		
		JLabel lblGameType = new JLabel("Game Type");
		
		gameModeList = new JComboBox<String>(gameModeCBModel);
		
		JButton btnRefreshServerList = new JButton("Refresh Server List");
		btnRefreshServerList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( sl.isRefreshing() ) {
					mw.stopAllRefreshing();
				}
				sl.RefreshServers();
				mw.removeMarkers(sl);
			}
		});
		
		JButton btnStopRefreshing = new JButton("Stop Refreshing");
		btnStopRefreshing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mw.stopAllRefreshing();
			}
		});
		
		chckbxHidePasswordedServers = new JCheckBox("Hide Passworded Servers");
		
		chckbxHideEmptyServers = new JCheckBox("Hide Empty Servers");
		
		chckbxHideFullServers = new JCheckBox("Hide Full Servers");
		
		JLabel lblMaxPing = new JLabel("Max Ping");
		
		maxPingFilter = new JTextField();
		Document doc = maxPingFilter.getDocument();
		if (doc instanceof AbstractDocument)
        {
            AbstractDocument abDoc  = (AbstractDocument) doc;
            abDoc.setDocumentFilter(new DocumentInputFilter());
        }
		maxPingFilter.setColumns(10);
		
		JLabel lblMinRank = new JLabel("Min Rank");
		
		minRankFilter = new JTextField();
		doc = minRankFilter.getDocument();
		if (doc instanceof AbstractDocument)
        {
            AbstractDocument abDoc  = (AbstractDocument) doc;
            abDoc.setDocumentFilter(new DocumentInputFilter());
        }
		minRankFilter.setColumns(10);
		
		JLabel lblMaxRank = new JLabel("Max Rank");
		
		maxRankFilter = new JTextField();
		doc = maxRankFilter.getDocument();
		if (doc instanceof AbstractDocument)
        {
            AbstractDocument abDoc  = (AbstractDocument) doc;
            abDoc.setDocumentFilter(new DocumentInputFilter());
        }
		maxRankFilter.setColumns(10);
		
		chckbxOfficialServersOnly = new JCheckBox("OFFICIAL SERVERS ONLY");
		chckbxOfficialServersOnly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ( chckbxOfficialServersOnly.isSelected() ) {
					serverNameFilter.setText("");
					serverNameFilter.setEnabled(false);
				} else {
					serverNameFilter.setEnabled(true);
				}
			}
		});
		
		JLabel lblPerspective = new JLabel("Perspective");
		
		String[] perspectiveCBModelList = {"Any", "First", "Third"};
		ComboBoxModel<String> perspectiveCBModel = new DefaultComboBoxModel<String>(perspectiveCBModelList);
		cBPerspective = new JComboBox<String>(perspectiveCBModel);
		
		spNumThreads = new JSpinner();
		spNumThreads.setToolTipText("Number of threads to query servers with. More threads can query more servers at a time (meaning they show up faster in your browser)." +
		" Too many threads will create latency and skew the reported pings. Min = 1, Max = 32.");
		spNumThreads.setModel(new SpinnerNumberModel(32, 1, 32, 1));
		
		JLabel lblQueryThreads = new JLabel("Query Threads");
		GroupLayout gl_serverListFilters = new GroupLayout(this);
		gl_serverListFilters.setHorizontalGroup(
			gl_serverListFilters.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_serverListFilters.createSequentialGroup()
					.addGroup(gl_serverListFilters.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_serverListFilters.createSequentialGroup()
							.addGap(94)
							.addComponent(lblServerName)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_serverListFilters.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_serverListFilters.createSequentialGroup()
									.addGroup(gl_serverListFilters.createParallelGroup(Alignment.LEADING)
										.addComponent(serverNameFilter, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)
										.addComponent(chckbxOfficialServersOnly))
									.addGap(23)
									.addGroup(gl_serverListFilters.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblGameType)
										.addComponent(lblMinRank)))
								.addComponent(chckbxHidePasswordedServers))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_serverListFilters.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_serverListFilters.createSequentialGroup()
									.addComponent(chckbxHideEmptyServers)
									.addGap(87)
									.addComponent(chckbxHideFullServers))
								.addGroup(gl_serverListFilters.createSequentialGroup()
									.addGroup(gl_serverListFilters.createParallelGroup(Alignment.LEADING, false)
										.addComponent(minRankFilter, 0, 0, Short.MAX_VALUE)
										.addComponent(gameModeList, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGap(74)
									.addGroup(gl_serverListFilters.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblMaxPing)
										.addComponent(lblMaxRank))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_serverListFilters.createParallelGroup(Alignment.LEADING, false)
										.addComponent(maxRankFilter, 0, 0, Short.MAX_VALUE)
										.addComponent(maxPingFilter, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
									.addGap(52)
									.addGroup(gl_serverListFilters.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblPerspective)
										.addComponent(lblQueryThreads))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_serverListFilters.createParallelGroup(Alignment.LEADING, false)
										.addComponent(spNumThreads)
										.addComponent(cBPerspective, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
						.addGroup(gl_serverListFilters.createSequentialGroup()
							.addGap(210)
							.addComponent(btnRefreshServerList)
							.addGap(311)
							.addComponent(btnStopRefreshing)))
					.addContainerGap(304, Short.MAX_VALUE))
		);
		gl_serverListFilters.setVerticalGroup(
			gl_serverListFilters.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_serverListFilters.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_serverListFilters.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_serverListFilters.createSequentialGroup()
							.addGroup(gl_serverListFilters.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblServerName)
								.addComponent(serverNameFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblGameType)
								.addComponent(gameModeList, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(14)
							.addGroup(gl_serverListFilters.createParallelGroup(Alignment.BASELINE)
								.addComponent(chckbxOfficialServersOnly)
								.addComponent(minRankFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblMinRank)))
						.addGroup(gl_serverListFilters.createSequentialGroup()
							.addGroup(gl_serverListFilters.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblMaxPing)
								.addComponent(maxPingFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPerspective)
								.addComponent(cBPerspective, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(14)
							.addGroup(gl_serverListFilters.createParallelGroup(Alignment.BASELINE)
								.addComponent(maxRankFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblMaxRank)
								.addComponent(spNumThreads, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblQueryThreads))))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_serverListFilters.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxHidePasswordedServers)
						.addComponent(chckbxHideEmptyServers)
						.addComponent(chckbxHideFullServers))
					.addPreferredGap(ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
					.addGroup(gl_serverListFilters.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnRefreshServerList)
						.addComponent(btnStopRefreshing))
					.addContainerGap())
		);
		this.setLayout(gl_serverListFilters);
	}
}
