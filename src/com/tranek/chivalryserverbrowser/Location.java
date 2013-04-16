package com.tranek.chivalryserverbrowser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * Super class of the location classes. Contains shared utility methods.
 *
 */
public class Location {
	
	/**
	 * Reads JSON data from a URL address.
	 * 
	 * 
	 * @param url the URL address to read the JSON data from
	 * @return a JSONObject of the text data read in from the URL
	 * @throws IOException
	 * @throws JSONException
	 */
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
	
	/**
	 * Reads all text data from a Reader and converts it to one complete string.
	 * 
	 * @param rd the Reader containing the text separated by new lines
	 * @return a concatenated string of the text
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
}
