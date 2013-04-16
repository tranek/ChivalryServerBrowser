package com.tranek.chivalryserverbrowser;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;

import net.barkerjr.gameserver.GameServer.Request;
import net.barkerjr.gameserver.valve.SourceServer;
import net.barkerjr.gameserver.valve.SourceServerList;
import net.barkerjr.gameserver.valve.ValveServerList;

public class MasterServerQueryBeta extends MasterServerQuery {
	
	public MasterServerQueryBeta(MainWindow mw, ServerFilters sf) {
		super(mw, sf);
	}
	
	@Override
	public Vector<ChivServer> getServers() throws IOException {
		SourceServerList list = new SourceServerList();
		list.gameDir = "chivalrymedievalwarfarebeta";
		HashSet<SourceServer> serverList = new HashSet<SourceServer>();
		Vector<ChivServer> cServers = new Vector<ChivServer>();
		ValveServerList<SourceServer>.ServerIterator servers = list.iterator(10000);
		try {
			while (servers.hasNext()) {
				SourceServer server = servers.next();
				serverList.add(server);
				server.load(Request.INFORMATION);
			}
		} finally {
			servers.close();
		}
		for ( SourceServer server : serverList ) {
			String name = server.getName();
			String ip = server.getIP();
			String port = "" + server.getPort();
			String map = "" + server.getMap();
			cServers.add(new ChivServer(name, ip, port, map));
		}
		return cServers;
	}
	
	@Override
	public Vector<ChivServer> getServerList() {
		return mw.serversBeta;
	}

}
