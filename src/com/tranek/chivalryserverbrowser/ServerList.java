package com.tranek.chivalryserverbrowser;
import java.util.Vector;


public interface ServerList {
	public void RefreshServers();
	public MainWindow getMW();
	public Vector<ChivServer> getServerList();
	public boolean isRefreshing();
}
