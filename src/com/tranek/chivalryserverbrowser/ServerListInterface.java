package com.tranek.chivalryserverbrowser;
import java.util.Vector;


public interface ServerListInterface {
	public void RefreshServers();
	public MainWindow getMW();
	public Vector<ChivServer> getServerList();
	public boolean isRefreshing();
}
