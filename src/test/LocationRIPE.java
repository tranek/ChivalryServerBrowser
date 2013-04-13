package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LocationRIPE {

	public LocationRIPE() {}
	
	public HashMap<String, String> getLocation(String ip) {
		HashMap<String, String> location = new HashMap<String, String>();
		String city = "";
		String state = "";
		String country = "";
		String latitude = "";
		String longitude = "";
		try {
			/*JSONObject json = readJsonFromUrl("http://apps.db.ripe.net/whois/search.json?query-string=" +
					ip + "&source=ripe");
			JSONObject resources = json.getJSONObject("whois-resources");
			JSONObject objects = resources.getJSONObject("objects");
			JSONArray object = objects.getJSONArray("object");
			JSONObject o0 = object.getJSONObject(0);
			JSONObject attributes0 = o0.getJSONObject("attributes");
			JSONArray attribute0 = attributes0.getJSONArray("attribute");
			JSONObject cntry = attribute0.getJSONObject(3);
			country = cntry.getString("value");
			
			JSONObject o1 = object.getJSONObject(1);
			JSONObject attributes1 = o1.getJSONObject("attributes");
			JSONArray attribute1 = attributes1.getJSONArray("attribute");
			JSONObject cty = attribute1.getJSONObject(4);
			city = cty.getString("value");
			
			JSONObject ste = attribute1.getJSONObject(6);
			state = ste.getString("value");
			if ( state.contains(",") ) {
				state = state.split(",")[0];
			}*/
			JSONObject json = readJsonFromUrl("http://stat.ripe.net/data/geoloc/data.json?resource=" + ip);
			JSONObject data = json.getJSONObject("data");
			JSONArray locations = data.getJSONArray("locations");
			for ( int i=0; i<locations.length(); i++ ) {
				JSONObject l = locations.getJSONObject(i);
				try {
					if ( city.equals("") ) {
						city = l.getString("city");
					}
				} catch (JSONException e) {}
				try {
					if ( state.equals("") ) {
						state = l.getString("state");
					}
				} catch (JSONException e) {}
				try {
					if ( country.equals("") ) {
						country = l.getString("country");
					}
				} catch (JSONException e) {}
				try {
					if ( latitude.equals("") ) {
						latitude = "" + l.getDouble("latitude");
					}
				} catch (JSONException e) {e.printStackTrace();}
				try {
					if ( longitude.equals("") ) {
						longitude = "" + l.getDouble("longitude");
					}
				} catch (JSONException e) {e.printStackTrace();}
			}
			
			if ( country.contains("(") ) {
				state = country.split("\\(")[0];
				country = country.split("\\(")[1];
				country = country.substring(0, country.length()-1);
			}
			
			location.put("city", city);
			location.put("state", state);
			location.put("country", country);
			location.put("latitude", latitude);
			location.put("longitude", longitude);
			return location;
		} catch(IOException | JSONException e) {
			e.printStackTrace();
		}
		return null;
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
	
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
}
