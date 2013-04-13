package test;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;


public class QueryServerApplication {

	public SourceServer server;
	public String port = "";
	public String ping = "";
	public String mIPaddress;
	public int mPort;
	//public HashMap<String, SteamPlayer> mPlayers;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		QueryServerApplication qsa = new QueryServerApplication();
		qsa.getInfo();

	}
	
	public QueryServerApplication() {
		//mIPaddress="74.91.125.196";
		//mPort = 27015;
		mIPaddress="66.55.129.178";
		mPort=7887;
	}
	
	public void getInfo() {
			
		try {
			server = new SourceServer(InetAddress.getAllByName(mIPaddress)[0], mPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		}
		try {
			server.initialize();
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		//System.out.println(server.getPlayers());
		System.out.println(server.getIpAddresses());
		//System.out.println(server.getMaster().getIpAddresses());
		try {
			System.out.println(server.getServerInfo());
			
			port = "" + server.getServerInfo().get("serverPort");
			//System.out.println(port);
			String name = server.getServerInfo().get("serverName").toString();
			String IP = server.getIpAddresses().get(0).toString();
			String sport = server.getServerInfo().get("serverPort").toString();
			
			System.out.println(server.getRules());
			
			
			//phpbb forums
			System.out.println(name + " (" + IP + ":" + sport + ") " + "[u][server]" + IP + ":" + sport + "[/server][/u]");
					
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		//System.out.println(server.getRules());
		try {
			//System.out.println(server.getPing());
			
			ping = "" + server.getPing();
			//System.out.println(ping);
			
			//mPlayers = server.getPlayers();
			
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		
		//String[] ret = {port, ping};
	}

}
