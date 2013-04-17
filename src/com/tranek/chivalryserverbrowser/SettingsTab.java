package com.tranek.chivalryserverbrowser;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;

/**
 * 
 * The settings tab where the user can change settings of the application
 * and video settings of Chivalry Medieval Warfare.
 *
 */
@SuppressWarnings("serial")
public class SettingsTab extends JPanel {
	
	/** A reference to the MainWindow for its utility methods. */
	protected MainWindow mw;
	/** The JPanel for settings relating to the Steam Community. */
	protected JPanel pnlSteamCommunity;
	/** The URL to the user's Steam Community page. This is linked to the URL field on the
	 * friends tab. Any changes to this will be reflected there and vice versa.
	 */
	protected JTextField tfSteamCommunityUrl;
	/** The X resolution value launch option. */
	protected JTextField tfLaunchResX;
	/** The Y resolution value launch option. */
	protected JTextField tfLaunchResY;
	/** The JTable for the game settings. */
	protected JTable tblGameSettings;
	/** Column headers for the game settings table. */
	private final String[] gameSettingsColumnHeaders = {"Setting", "Value"};
	/** The data model for the game settings table. */
	protected TableModel gameSettingsDataModel;
	/** The JComboBox for the possible screen types launch option. */
	protected JComboBox<String> cbScreen;
	/** Toggle button for enabling the launch options when joining a server. */
	protected JToggleButton tglbtnEnableLaunchOptions;
	
	/**
	 * Creates a new SettingsTab. Calls its {@link #initialize()} method.
	 * 
	 * @param MW the MainWindow
	 */
	public SettingsTab(MainWindow MW) {
		super();
		setLayout(null);
		
		mw = MW;
		initialize();
	}
	
	/**
	 * Sets up the {@link SettingsTab} and its children components.
	 */
	public void initialize() {
		JLabel lblSteamCommunity = new JLabel("Steam Community");
		lblSteamCommunity.setBounds(12, 13, 113, 16);
		add(lblSteamCommunity);
		
		pnlSteamCommunity = new JPanel();
		pnlSteamCommunity.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlSteamCommunity.setBounds(22, 42, 966, 51);
		add(pnlSteamCommunity);
		pnlSteamCommunity.setLayout(null);
		
		JLabel lblSteamCommunityUrl = new JLabel("Steam Community URL:");
		lblSteamCommunityUrl.setBounds(12, 13, 143, 16);
		pnlSteamCommunity.add(lblSteamCommunityUrl);
		
		tfSteamCommunityUrl = new JTextField();
		tfSteamCommunityUrl.getDocument().addDocumentListener(new DocumentListener() {			
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLabel(e);
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLabel(e);
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLabel(e);
			}
			private void updateLabel(DocumentEvent e) {
				
				if ( !tfSteamCommunityUrl.getText().equals(mw.friendsTab.urlField.getText()) ) {
				
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							mw.friendsTab.urlField.setText(tfSteamCommunityUrl.getText());
						}
					});
				}
			}
		});
		tfSteamCommunityUrl.setBounds(152, 10, 802, 22);
		pnlSteamCommunity.add(tfSteamCommunityUrl);
		tfSteamCommunityUrl.setColumns(10);
		
		JLabel lblLaunchOptions = new JLabel("Launch Options");
		lblLaunchOptions.setBounds(12, 128, 100, 16);
		add(lblLaunchOptions);
		
		JPanel pnlLaunchOptions = new JPanel();
		pnlLaunchOptions.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlLaunchOptions.setBounds(22, 157, 966, 51);
		add(pnlLaunchOptions);
		pnlLaunchOptions.setLayout(null);
		
		JLabel lblScreen = new JLabel("Screen:");
		lblScreen.setBounds(12, 13, 56, 16);
		pnlLaunchOptions.add(lblScreen);
		
		cbScreen = new JComboBox<String>();
		cbScreen.setModel(new DefaultComboBoxModel<String>(new String[] {"", "Fullscreen", "Windowed", "Borderless Window"}));
		cbScreen.setBounds(80, 10, 137, 22);
		pnlLaunchOptions.add(cbScreen);
		
		JLabel lblResolution = new JLabel("Resolution:");
		lblResolution.setBounds(294, 13, 76, 16);
		pnlLaunchOptions.add(lblResolution);
		
		tfLaunchResX = new JTextField();
		tfLaunchResX.setHorizontalAlignment(SwingConstants.RIGHT);
		tfLaunchResX.setBounds(382, 10, 56, 22);
		pnlLaunchOptions.add(tfLaunchResX);
		tfLaunchResX.setColumns(10);
		Document doc = tfLaunchResX.getDocument();
		if (doc instanceof AbstractDocument)
        {
            AbstractDocument abDoc  = (AbstractDocument) doc;
            abDoc.setDocumentFilter(new DocumentInputFilter());
        }
		
		JLabel lblX = new JLabel("x");
		lblX.setBounds(450, 13, 56, 16);
		pnlLaunchOptions.add(lblX);
		
		tfLaunchResY = new JTextField();
		tfLaunchResY.setHorizontalAlignment(SwingConstants.RIGHT);
		tfLaunchResY.setBounds(471, 10, 56, 22);
		pnlLaunchOptions.add(tfLaunchResY);
		tfLaunchResY.setColumns(10);
		
		tglbtnEnableLaunchOptions = new JToggleButton("Enable Launch Options");
		tglbtnEnableLaunchOptions.setBounds(777, 9, 177, 25);
		pnlLaunchOptions.add(tglbtnEnableLaunchOptions);
		doc = tfLaunchResY.getDocument();
		if (doc instanceof AbstractDocument)
        {
            AbstractDocument abDoc  = (AbstractDocument) doc;
            abDoc.setDocumentFilter(new DocumentInputFilter());
        }
		
		JLabel lblGameSettings = new JLabel("Game Settings");
		lblGameSettings.setBounds(12, 247, 89, 16);
		add(lblGameSettings);
		
		JPanel pnlGameSettings = new JPanel();
		pnlGameSettings.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		pnlGameSettings.setBounds(22, 276, 966, 282);
		add(pnlGameSettings);
		pnlGameSettings.setLayout(null);
		
		gameSettingsDataModel = new DefaultTableModel(gameSettingsColumnHeaders, 0);
		
		final JComboBox<String> cbBool = new JComboBox<String>();
		cbBool.addItem("True");
		cbBool.addItem("False");
		
		final JTextField tfIntegers = new JTextField();
		doc = tfIntegers.getDocument();
		if (doc instanceof AbstractDocument)
        {
            AbstractDocument abDoc  = (AbstractDocument) doc;
            abDoc.setDocumentFilter(new DocumentInputFilter());
        }
		
		tblGameSettings = new JTable(gameSettingsDataModel){
            //  Determine editor to be used by row
            public TableCellEditor getCellEditor(int row, int column)
            {
                int modelColumn = convertColumnIndexToModel( column );

                if ( modelColumn == 1 && (row < 5 || row > 6) ) {
                    return new DefaultCellEditor(cbBool);
                } else if ( modelColumn == 1 && (row == 5 || row == 6) ) {
                	return new DefaultCellEditor(tfIntegers);
                }
                else {
                    return super.getCellEditor(row, column);
                }
            }
        };
		tblGameSettings.setBounds(0, 0, 966, 64);
		tblGameSettings.setAutoCreateRowSorter(true);
		JScrollPane spGameSettings = new JScrollPane(tblGameSettings);
		spGameSettings.setBounds(0, 0, 966, 282);
		pnlGameSettings.add(spGameSettings);
		Object[] rowDataAO = {"Ambient Occlusion", getUDKConfigSetting("UDKSystemSettings.ini", "AmbientOcclusion")};
		((DefaultTableModel)gameSettingsDataModel).addRow(rowDataAO);
		Object[] rowDataBloom = {"Bloom", getUDKConfigSetting("UDKSystemSettings.ini", "Bloom")};
		((DefaultTableModel)gameSettingsDataModel).addRow(rowDataBloom);
		Object[] rowDataDLights = {"Dynamic Lights", getUDKConfigSetting("UDKSystemSettings.ini", "DynamicLights")};
		((DefaultTableModel)gameSettingsDataModel).addRow(rowDataDLights);
		Object[] rowDataDShadows = {"Dynamic Shadows", getUDKConfigSetting("UDKSystemSettings.ini", "DynamicShadows")};
		((DefaultTableModel)gameSettingsDataModel).addRow(rowDataDShadows);
		Object[] rowDataMotionBlur = {"Motion Blur", getUDKConfigSetting("UDKSystemSettings.ini", "MotionBlur")};
		((DefaultTableModel)gameSettingsDataModel).addRow(rowDataMotionBlur);	
		Object[] rowDataResX = {"ResX", getUDKConfigSetting("UDKSystemSettings.ini", "ResX")};
		((DefaultTableModel)gameSettingsDataModel).addRow(rowDataResX);
		Object[] rowDataResY = {"ResY", getUDKConfigSetting("UDKSystemSettings.ini", "ResY")};
		((DefaultTableModel)gameSettingsDataModel).addRow(rowDataResY);
		Object[] rowDataVsync = {"Vsync", getUDKConfigSetting("UDKSystemSettings.ini", "UseVsync")};
		((DefaultTableModel)gameSettingsDataModel).addRow(rowDataVsync);
	}
	
	/**
	 * Gets the value for a particular game setting. It looks for the configuration
	 * file in the user's "Documents\My Games\Chivalry Medieval Warefare\UDKGame\Config"
	 * directory.
	 * 
	 * @param file the configuration file where the settings are located
	 * @param key the name of the setting
	 * @return the value of the setting
	 * @see JFileChooser#getFileSystemView()
	 * @see FileSystemView#getDefaultDirectory()
	 * @see BufferedReader
	 * @see FileReader
	 */
	public String getUDKConfigSetting(String file, String key) {
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();		
		String configDirectory = fw.getDefaultDirectory() + "\\My Games\\Chivalry Medieval Warfare\\UDKGame\\Config\\";
		
		String result = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(configDirectory + file));
			String line;
			while ((line = br.readLine()) != null) {
				if ( line.contains(key) ) {
					result = line.split("=")[1];
				}
				if ( !result.equals("") ) {
					break;
				}
			}
			br.close();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Writes the value for a particular game setting to the configuration file. It 
	 * looks for the configuration file in the user's
	 * "Documents\My Games\Chivalry Medieval Warefare\UDKGame\Config" directory.
	 * 
	 * @param file the configuration file where the settings are located
	 * @param key the name of the setting
	 * @param value the value of the setting
	 * @return true if it successfully wrote the setting to the configuration file; false otherwise
	 */
	public boolean writeUDKConfigSetting(String file, String key, String value) {
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();		
		String configDirectory = fw.getDefaultDirectory() + "\\My Games\\Chivalry Medieval Warfare\\UDKGame\\Config\\";
		
		String result = "";
		BufferedReader br;
		BufferedWriter bw;
		int totalLines = -1;
		int lineIndex = -1;
		String newLine = System.getProperty("line.separator");
		boolean foundKey = false;
		try {
			br = new BufferedReader(new FileReader(configDirectory + file));
			String line;
			while ((line = br.readLine()) != null) {
				totalLines++;
				if ( !foundKey && line.contains(key) ) {
					result += line.split("=")[0] + "=" + value + newLine;
					lineIndex = totalLines;
					foundKey = true;
				} else {
					result += line + newLine;
				}
			}
			br.close();
			
			if ( lineIndex > -1 ) {
				bw = new BufferedWriter(new FileWriter(configDirectory + file));
				bw.write(result);
			} else {
				return false;
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
