package com.tranek.chivalryserverbrowser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationARIN {

	public LocationARIN() {}
	
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
