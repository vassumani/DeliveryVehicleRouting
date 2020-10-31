package dvr;

import java.util.Random;

/**
 * Contains the information about a single location.
 * This class is to be treated as read-only once constructed.
 */
public class Location {
	public final Coordinate coord;
	public final String name;

	/**
	 * Default location constructor.
	 */
	public Location() {
		coord = new Coordinate();
		name = "";
	}

	/**
	 * Location constructor with information specified.
	 * @param x X-coordinate of this location.
	 * @param y Y-coordinate of this location.
	 * @param label An optional name used to identify this location.
	 */
	public Location(long x, long y, String label) {
		assert label != null;
		coord = new Coordinate(x, y);
		name = label;
	}

	/**
	 * Copy constructor for location data.
	 * @param src The source location which should be copied.
	 */
	public Location(Location src) {
		coord = src.coord;
		name = src.name;
	}

	/**
	 * A method to convert this coordinate to a string.
	 * @return The coordinate in string format.
	 */
	@Override
	public String toString() {
		return name + ":" + coord;
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
			list[i] = new Location(x, y, Integer.toString(i));
		}
		return list;
	}
}
