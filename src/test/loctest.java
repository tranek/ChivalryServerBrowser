package test;
import java.util.HashMap;


public class loctest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		LocationRIPE l = new LocationRIPE();
		
		HashMap<String, String> loc = new HashMap<String, String>();
		String ip = "64.34.176.217";
		loc = l.getLocation(ip);
		
		String city = loc.get("city");
		String state = loc.get("state");
		String country = loc.get("country");
		if (!country.equals("USA") && !state.equals("")) {
			city += ",";
		}
		if (state.equals("")) {
			System.out.println("Location = " + city + ", " + country);
		} else {
			System.out.println("Location = " + city + " " + state + ", " + country);
		}
	}

}
