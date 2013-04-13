package com.tranek.chivalryserverbrowser;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;


@SuppressWarnings("serial")
public class GamepadKeybindDialog extends JDialog {
	
	private final String mButton;
	private JTable tblCommands;
	private TableModel dataModel;
	private final String[] actionTableHeaders = {"Actions"};
	private JComboBox<String> cbCommands;
	private final MainWindow mw;

	public GamepadKeybindDialog(MainWindow MW, Frame frame, String title, final String button) {
		super(frame, title, true);
		setMinimumSize(new Dimension(450, 315));
		setResizable(false);
		setPreferredSize(new Dimension(450, 315));
		mButton = button;
		mw = MW;
		getContentPane().setLayout(null);
		
		JLabel lblCommandsBoundTo = new JLabel("Commands (order matters) bound to button: " + title);
		lblCommandsBoundTo.setBounds(12, 13, 338, 16);
		getContentPane().add(lblCommandsBoundTo);
		
		dataModel = new DefaultTableModel(actionTableHeaders, 0);

		cbCommands = new JComboBox<String>() {
			{
				for ( String command : Keybinds.commands ) {
					addItem(Keybinds.commandDescriptions.get(command));
				}
			}
		};
		
		tblCommands = new JTable(dataModel) {
			 public TableCellEditor getCellEditor(int row, int column)
	            {
                    return new DefaultCellEditor(cbCommands);
	            }
		};
		
		JScrollPane spActionsTable = new JScrollPane(tblCommands);
		spActionsTable.setBounds(22, 42, 396, 146);
		spActionsTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		spActionsTable.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(spActionsTable);
		
		JButton btnAddCommand = new JButton("Add Command");
		btnAddCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCommand();
			}
		});
		btnAddCommand.setBounds(22, 201, 161, 25);
		getContentPane().add(btnAddCommand);
		
		JButton btnRemoveCommand = new JButton("Remove Command");
		btnRemoveCommand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeCommand();
			}
		});
		btnRemoveCommand.setBounds(22, 239, 161, 25);
		getContentPane().add(btnRemoveCommand);
		
		JButton btnMoveUp = new JButton("Move Up");
		btnMoveUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveCommandUp();
			}
		});
		btnMoveUp.setBounds(257, 201, 161, 25);
		getContentPane().add(btnMoveUp);
		
		JButton btnMoveDown = new JButton("Move Down");
		btnMoveDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveCommandDown();
			}
		});
		btnMoveDown.setBounds(257, 239, 161, 25);
		getContentPane().add(btnMoveDown);
		setLocationRelativeTo(frame);
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	String commands = "";
		    	for ( int i=0; i<tblCommands.getRowCount(); i++ ) {
		    		final int row = tblCommands.convertRowIndexToModel(i);
					final int col = tblCommands.convertColumnIndexToModel(0);
					String value = (String) ((DefaultTableModel)dataModel).getValueAt(row, col);
		    		@SuppressWarnings("unchecked")
					JComboBox<String> jcb = (JComboBox<String>) tblCommands.getCellEditor(row, 0).getTableCellEditorComponent(tblCommands, value, true, row, col);
		    		int index = jcb.getSelectedIndex();
		    		if ( !commands.equals("") ) {
		    			commands += " | ";
		    		}
		    		commands += Keybinds.commands.get(index);
		    	}
		    	
		    	if ( writeBinding("UDKGame.ini", button, commands) ) {
		    		mw.printlnMC("Saved keybind changes.");
		    	}
		    		
		    	setVisible(false);
		    }
		});
		
		//fix for B button default
		//it defaults to reload then feint which causes it to feint out of a reload
		//this correctly switches that order to be feint and then reload
		if ( mButton.equals(Keybinds.XBOXTYPES_B) ) {
			String commands = getCommands("UDKGame.ini", mButton);
			if ( commands.equals("GBA_Reload | GBA_Feint") ) {
				mw.printlnMC("Detected incorrect default B button assignment (reload then feint).");
				commands = "GBA_Feint | GBA_Reload";
				writeBinding("UDKGame.ini", mButton, commands);
				mw.printlnMC("Corrected default B button assignment (feint then reload).");
			}
		}
		
		getBinding("UDKGame.ini", mButton);
	}
	
	public void addCommand() {
		Object[] rowData = new Object[1];
		rowData[0] = Keybinds.commands.get(0);
		((DefaultTableModel)dataModel).addRow(rowData);
		final int row = tblCommands.convertRowIndexToModel(tblCommands.getRowCount()-1);
		final int col = tblCommands.convertColumnIndexToModel(0);
		String value = (String) ((DefaultTableModel)dataModel).getValueAt(tblCommands.getRowCount()-1, col);
		@SuppressWarnings("unchecked")
		JComboBox<String> jcb = (JComboBox<String>) tblCommands.getCellEditor(row, col).getTableCellEditorComponent(tblCommands, value, true, row, col);
		tblCommands.setRowSelectionInterval(row, row);
		tblCommands.editCellAt(row, col);
		jcb.setVisible(true);
		jcb.setPopupVisible(true);
	}
	
	public void removeCommand() {
		if ( tblCommands.getSelectedRow() > -1 ) {
			final int tableRow = tblCommands.getSelectedRow();
			final int row = tblCommands.convertRowIndexToModel(tableRow);
			int col = 0;
			String value = (String) ((DefaultTableModel)dataModel).getValueAt(row, col);
			@SuppressWarnings("unchecked")
			JComboBox<String> jcb = (JComboBox<String>) tblCommands.getCellEditor(row, col).getTableCellEditorComponent(tblCommands, value, true, row, col);
			jcb.setSelectedIndex(jcb.getSelectedIndex());
			tblCommands.setRowSelectionInterval(row, row);
			((DefaultTableModel)dataModel).removeRow(row);
		}
	}
	
	public void moveCommandUp() {
		final int tableRow = tblCommands.getSelectedRow();
		final int row = tblCommands.convertRowIndexToModel(tableRow);
		if ( row > 0 ) {
			int col = 0;
			String value = (String) ((DefaultTableModel)dataModel).getValueAt(row, col);
			@SuppressWarnings("unchecked")
			JComboBox<String> jcb = (JComboBox<String>) tblCommands.getCellEditor(row, col).getTableCellEditorComponent(tblCommands, value, true, row, col);
			jcb.setSelectedIndex(jcb.getSelectedIndex());
			tblCommands.setRowSelectionInterval(row, row);
			Object[] rowData = new Object[1];
			rowData[0] = ((DefaultTableModel)dataModel).getValueAt(row, 0);
			((DefaultTableModel)dataModel).removeRow(row);
			((DefaultTableModel)dataModel).insertRow(row-1, rowData);
			tblCommands.setRowSelectionInterval(row-1, row-1);
		}
	}
	
	public void moveCommandDown() {
		final int tableRow = tblCommands.getSelectedRow();
		final int row = tblCommands.convertRowIndexToModel(tableRow);
		if ( row < tblCommands.getRowCount()-1 && row > -1 ) {
			int col = 0;
			String value = (String) ((DefaultTableModel)dataModel).getValueAt(row, col);
			@SuppressWarnings("unchecked")
			JComboBox<String> jcb = (JComboBox<String>) tblCommands.getCellEditor(row, col).getTableCellEditorComponent(tblCommands, value, true, row, col);
			jcb.setSelectedIndex(jcb.getSelectedIndex());
			tblCommands.setRowSelectionInterval(row, row);
			Object[] rowData = new Object[1];
			rowData[0] = ((DefaultTableModel)dataModel).getValueAt(row, 0);
			((DefaultTableModel)dataModel).removeRow(row);
			((DefaultTableModel)dataModel).insertRow(row+1, rowData);
			tblCommands.setRowSelectionInterval(row+1, row+1);
		}
	}
	
	public void showDialog() {
		setVisible(true);
	}
	
	public void getBinding(String file, String button) {
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();		
		String configDirectory = fw.getDefaultDirectory() + "\\My Games\\Chivalry Medieval Warfare\\UDKGame\\Config\\";
		
		String binding = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(configDirectory + file));
			String line;
			boolean foundKeybind = false;
			//find [UTGame.UTPlayerInput]
			while ((line = br.readLine()) != null) {
				if ( line.contains("[UTGame.UTPlayerInput]") ) {
					foundKeybind = true;
					break;
				}
			}
			//find our button
			if ( foundKeybind ) {
				boolean foundButton = false;
				boolean endOfBinding = false;
				while ((line = br.readLine()) != null) {
					if ( !foundButton && !endOfBinding ) {
						if ( line.contains("Bindings=(Name=\"" + button) && !line.contains("Bindings=(Name=\"" + button + "Axis") ) {
							foundButton = true;
							binding += line;
						}
					} else if ( foundButton && !endOfBinding ){
						if ( line.contains("Bindings=(Name=\"") ) {
							endOfBinding = true;
						} else {
							binding += line;
						}
					}
				}
			}
			br.close();
			
			//we found button, parse out commands
			if ( !binding.equals("") ) {
				int startIndex = binding.indexOf("Command=");
				String commands = binding.substring(startIndex + 9).trim();
				int endIndex = commands.indexOf("Control=") - 2;
				commands = commands.substring(0, endIndex);
				String[] command = commands.split("\\|");
				for ( String c : command ) {
					Object[] rowData = new Object[1];
					rowData[0] = Keybinds.commandDescriptions.get(c.trim());
					((DefaultTableModel)dataModel).addRow(rowData);
				}
			}
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getCommands(String file, String button) {
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();		
		String configDirectory = fw.getDefaultDirectory() + "\\My Games\\Chivalry Medieval Warfare\\UDKGame\\Config\\";
		
		String binding = "";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(configDirectory + file));
			String line;
			boolean foundKeybind = false;
			//find [UTGame.UTPlayerInput]
			while ((line = br.readLine()) != null) {
				if ( line.contains("[UTGame.UTPlayerInput]") ) {
					foundKeybind = true;
					break;
				}
			}
			//find our button
			if ( foundKeybind ) {
				boolean foundButton = false;
				boolean endOfBinding = false;
				while ((line = br.readLine()) != null) {
					if ( !foundButton && !endOfBinding ) {
						if ( line.contains("Bindings=(Name=\"" + button) && !line.contains("Bindings=(Name=\"" + button + "Axis") ) {
							foundButton = true;
							binding += line;
						}
					} else if ( foundButton && !endOfBinding ){
						if ( line.contains("Bindings=(Name=\"") ) {
							endOfBinding = true;
						} else {
							binding += line;
						}
					}
				}
			}
			br.close();
			
			//we found button, parse out commands
			if ( !binding.equals("") ) {
				int startIndex = binding.indexOf("Command=");
				String commands = binding.substring(startIndex + 9).trim();
				int endIndex = commands.indexOf("Control=") - 2;
				commands = commands.substring(0, endIndex).trim();
				return commands;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean writeBinding(String file, String button, String commands) {
		
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();		
		String configDirectory = fw.getDefaultDirectory() + "\\My Games\\Chivalry Medieval Warfare\\UDKGame\\Config\\";
		
		String result = "";
		String result2 = "";
		String binding = "";
		BufferedReader br;
		String newLine = System.getProperty("line.separator");
		try {
			br = new BufferedReader(new FileReader(configDirectory + file));
			String line;
			boolean foundKeybind = false;
			//find [UTGame.UTPlayerInput]
			while ((line = br.readLine()) != null) {
				result += line + newLine;
				if ( line.contains("[UTGame.UTPlayerInput]") ) {
					foundKeybind = true;
					break;
				}
			}
			//find our button
			if ( foundKeybind ) {
				boolean foundButton = false;
				boolean endOfBinding = false;
				while ((line = br.readLine()) != null) {
					if ( !foundButton && !endOfBinding ) {
						if ( line.contains("Bindings=(Name=\"" + button) && !line.contains("Bindings=(Name=\"" + button + "Axis") ) {
							foundButton = true;
							binding += line;
						} else {
							result += line + newLine;
						}
					} else if ( foundButton && !endOfBinding ){
						if ( line.contains("Bindings=(Name=\"") ) {
							endOfBinding = true;
							result2 += line + newLine;
						} else {
							binding += line;
						}
					} else {
						result2 += line + newLine;
					}
				}
			}
			br.close();
			
			//found button, remove and insert new commands
			if ( !binding.equals("") && !commands.equals("") ) {
				int startIndex = binding.indexOf("Command=");
				int endIndex = binding.indexOf("Control=") - 2;
				
				String newBinding = binding.substring(0, startIndex + 9);
				newBinding += commands;
				newBinding += binding.substring(endIndex, binding.length());
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(configDirectory + file));
				String out = result + newBinding + newLine + result2;
				bw.write(out);
				bw.close();
				return true;
			}
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return false;
	}
	
}
