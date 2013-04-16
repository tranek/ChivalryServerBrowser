package com.tranek.chivalryserverbrowser;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Gets the location information of a server via its IP address from the
 * ARIN WHOIS services.
 *
 */
public class LocationARIN extends Location {
	
	/**
	 * Creates a new LocationARIN.
	 */
	public LocationARIN() {}
	
	/**
	 * Gets the location (city, state, and country) of a server via its IP
	 * address from the ARIN WHOIS services. This only works with servers
	 * serviced by ARIN (North America). If it detects a server is 
	 * serviced by RIPE, it tries to create a {@link LocationRIPE} to query
	 * its information. This needs to be removed as all of the logic
	 * for location in the application tries RIPE first.
	 * 
	 * @param ip the IP address of the server
	 * @return a HashMap of the city, state, and country for the server;
	 * or null if unsuccessful in getting its location
	 * @see JSONObject
	 * @see JSONException
	 * @see LocationRIPE
	 */
	public HashMap<String, String> getLocation(String ip) {
		HashMap<String, String> location = new HashMap<String, String>();
		try {
			JSONObject json = readJsonFromUrl("http://whois.arin.net/rest/ip/" + ip + "/pft.json");
			JSONObject details = null;
			try {
				details = json.getJSONObject("ns4:pft").getJSONObject("org");
			} catch (JSONException e) {
				try {
					details = json.getJSONObject("ns4:pft").getJSONObject("customer");
				} catch (JSONException e2) {}
			}
			
			String city = "";
			String state = "";
			String country = "";
			String handle = "";
			if (details != null) {
				try {
					handle = details.getJSONObject("handle").getString("$");
					if ( handle.equals("RIPE") ) {
						LocationRIPE lripe = new LocationRIPE();
						return lripe.getLocation(ip);
					}
				} catch (JSONException e) {}
				
				try {
					city = details.getJSONObject("city").getString("$");
					city = city.replaceAll("\\+", " ");
				} catch (JSONException e) {}
					
				try {
					state = details.getJSONObject("iso3166-2").getString("$");
				} catch (JSONException e) {}
				
				try {
					JSONObject countryInfo = details.getJSONObject("iso3166-1");
					country = countryInfo.getJSONObject("code2").getString("$");
				} catch (JSONException e) {}
			}
			
			location.put("city", city);
			location.put("state", state);
			location.put("country", country);
			return location;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
