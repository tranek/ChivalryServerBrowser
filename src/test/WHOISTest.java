package test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Code from http://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
 */

public class WHOISTest {

	public static void main(String[] args) throws JSONException, IOException {
		/*WhoisClient whois;
		whois = new WhoisClient();
        // We want to timeout if a response takes longer than 60 seconds
        whois.setDefaultTimeout(60000);
        try {
			//InetAddress address = InetAddress.getByName("www.google.com");
        	String handle = "www.google.com";
			whois.connect(WhoisClient.DEFAULT_HOST);
            System.out.print(whois.query(handle));
            whois.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		//String IPaddress = "50.97.201.58";
		String IPaddress = "109.70.148.28";
		JSONObject json = readJsonFromUrl("http://whois.arin.net/rest/ip/" + IPaddress + "/pft.json");
		JSONObject org = json.getJSONObject("ns4:pft").getJSONObject("org");
		JSONObject country = org.getJSONObject("iso3166-1");
	    //System.out.println(json.toString());
	    //System.out.println(json.get("ns4:pft"));
		System.out.println(org.toString());
		System.out.println(org.get("streetAddress"));
		//System.out.println(org.get("city"));
		String city = org.getJSONObject("city").getString("$");
		city = city.replaceAll(" ", "+");
		System.out.println(city);
		String state = org.getJSONObject("iso3166-2").getString("$");
		System.out.println(state);
		System.out.println(org.get("postalCode"));
		//System.out.println(country.get("name"));
		String countryName = country.getJSONObject("name").getString("$");
		countryName = countryName.replaceAll(" ", "+");
		System.out.println(countryName);
		System.out.println(country.getJSONObject("code2").getString("$"));
		System.out.println(country.get("code3"));
		
		String address = "http://maps.googleapis.com/maps/api/geocode/json?address=" +
				city + "," + countryName + "&sensor=false";
		JSONObject location = readJsonFromUrl(address);
		System.out.println(location.toString());
		JSONObject loc = location.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
		System.out.println(loc.toString());
		//String lat = loc.getJSONObject("lat").getString("$");
		String lat = loc.get("lat").toString();
		String lng = loc.get("lng").toString();
		System.out.println("lat= " + lat);
		System.out.println("lng= " + lng);
	}
		
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}


}
