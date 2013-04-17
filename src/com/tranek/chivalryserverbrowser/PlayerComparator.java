package com.tranek.chivalryserverbrowser;
import java.util.Comparator;

/**
 * 
 * Comparator used in the server list tables for sorting the Players
 * column. It sorts the current players as integers instead of the
 * default Strings.
 *
 */
public class PlayerComparator implements Comparator<String> {
	
	/**
	 * Parses out the first integer (current players) and compares
	 * them as integers would be compared normally.
	 */
	@Override
	public int compare(String o1, String o2) {
		if ( o1.equals("") && !o2.equals("") ) {
			return -1;
		} else if ( !o1.equals("") && o2.equals("") ) {
			return 1;
		} else if ( o1.equals("") && o2.equals("") ) {
			return 0;
		}
		o1 = o1.split(" ")[0];
		o2 = o2.split(" ")[0];
		int p1 = Integer.parseInt(o1);
		int p2 = Integer.parseInt(o2);
		if ( p1 < p2 ) {
			return -1;
		} else if ( p1 > p2 ) {
			return 1;
		}
		return 0;
	}	
}