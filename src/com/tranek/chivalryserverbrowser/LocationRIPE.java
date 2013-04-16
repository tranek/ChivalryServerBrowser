package com.tranek.chivalryserverbrowser;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Gets the location information of a server via its IP address from the
 * RIPE WHOIS services.
 *
 */
public class LocationRIPE extends Location {

	/**
	 * Creates a new LocationRIPE.
	 */
	public LocationRIPE() {}
	
	/**
	 * Gets the location (city, state, country, latitude, and longitude)
	 * of a server via its IP address from the RIPE WHOIS services.
	 * This works with almost every server.
	 * 
	 * @param ip the IP address of the server
	 * @return a HashMap of the city, state, country, latitude, and longitude
	 */
	public HashMap<String, String> getLocation(String ip) {
		HashMap<String, String> location = new HashMap<String, String>();
		String city = "";
		String state = "";
		String country = "";
		String latitude = "";
		String longitude = "";
		try {
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
				} catch (JSONException e) {}
				try {
					if ( longitude.equals("") ) {
						longitude = "" + l.getDouble("longitude");
					}
				} catch (JSONException e) {}
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
	
}
