package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

public class ChivInstallLocation {

	public static void main(String[] args) {

		/*64 bit processor, 32bit app
		HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall\Steam App 219640
		64 bit processor, 64 bit app
		HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall
		32 bit processor
		HKLM\Software\Microsoft\Windows\CurrentVersion\Uninstall\[app name]
		
		key "InstallLocation"*/
		
		/*String value = WinRegistry.readString (
    WinRegistry.HKEY_LOCAL_MACHINE,                             //HKEY
   "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion",           //Key
   "ProductName");                                              //ValueName
    System.out.println("Windows Distribution = " + value);  */
		
		int hkey = WinRegistry.HKEY_LOCAL_MACHINE;
		String key = "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 219640";
		String valueName = "InstallLocation";
		String value = "";
		try {
			value = WinRegistry.readString(hkey, key, valueName);
		} catch (IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		System.out.println("Chiv install location: " + value);
		
		/*http://stackoverflow.com/questions/1503555/how-to-find-my-documents-folder*/
		JFileChooser fr = new JFileChooser();
		FileSystemView fw = fr.getFileSystemView();
		System.out.println(fw.getDefaultDirectory());
		
		String configDirectory = fw.getDefaultDirectory() + "\\My Games\\Chivalry Medieval Warfare\\UDKGame\\Config\\";
		System.out.println(configDirectory);
		
		String xdim = "";
		String ydim = "";
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(configDirectory + "UDKSystemSettings.ini"));
			String line;
			while ((line = br.readLine()) != null) {
			   // process the line.
				if ( line.contains("ResX") ) {
					xdim = line.split("=")[1];
				} else if ( line.contains("ResY") ) {
					ydim = line.split("=")[1];
				}
				if ( !xdim.equals("") && !ydim.equals("") ) {
					break;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Resolution: " + xdim + " x " + ydim);
	}

}
