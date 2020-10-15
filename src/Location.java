import java.util.Random;

/**
 * Contains the information about a single location.
 * This class is to be treated as read-only once constructed.
 */
public class Location {
	public final long x;
	public final long y;

	public Location() {
		x = 0;
		y = 0;
	}
	
	public Location(long X, long Y) {
		x = X;
		y = Y;
	}
	
	public Location(Location l) {
		x = l.x;
		y = l.y;
	}
	
	/**
	 * Returns a list of random locations.
	 * @param count The number of locations to generate within the list.
	 * @param range The maximum absolute coordinate of any location (maybe negative).
	 */
	public static Location[] RandomList(int count, int range) {
		assert count > 0;
		Random rand = new Random();
		Location[] list = new Location[count];
		int limit = (range * 2) + 1;
		for (int i=0; i<count; i++) {
			long x = rand.nextInt(limit) - range;
			long y = rand.nextInt(limit) - range;
			list[i] = new Location(x, y);
		}
		return list;
	}
}
