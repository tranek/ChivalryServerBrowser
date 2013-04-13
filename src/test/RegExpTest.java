package test;

public class RegExpTest {

	public static void main(String[] args) {
		String str = "In-Game<br />Chivalry: Medieval Warfare <span class=\"friend_join_game_dash\">-</span> <a class=\"friend_join_game_link\" href=\"steam://connect/69.39.239.19:9377\">Join</a>";
		//String regexp = "<.*>";
		
		//String[] tokens = str.split(regexp);
		
		if ( str.substring(0, 13).equals("In-Game<br />") ) {
			System.out.println("Match!");
			System.out.println(str.substring(13));
			System.out.println(str.substring(13, str.indexOf("<span class")).trim());
			String ipstr = str.substring(str.indexOf("href="));
			ipstr = ipstr.substring(22, ipstr.indexOf(">")-1);
			System.out.println(ipstr);
			//System.out.println(ipstr.substring(str.inde, endIndex));
		}
		
		System.out.println("Done!");
	}

}
