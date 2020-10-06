
/**
 * Contains the information about a single location.
 */
public class Location {

	public long x;
	public long y;
	public int index;

	static public final int nullIndex = -1;

	public Location() {
		x = 0;
		y = 0;
		index = -1;
	}
	
	public Location(long X, long Y) {
		x = X;
		y = Y;
		index = nullIndex;
	}
}
