
/**
 * Used to hold the distances between locations within a location list.
 * This class is to be treated as read-only once constructed.
 */
public class DistanceMatrix {

	/**
	 * Distance matrix constructor.
	 */
	public DistanceMatrix(Location[] l) {
		assert l != null;
		assert l.length > 0;
		size = l.length;
		location = l;
		distance = new long [size][size];
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				distance[x][y] = calculate(x, y);
			}
		}
	}
	
	/**
	 * Get the size of the distance matrix in one dimension.
	 * This is the same as the number of locations within the distance matrix.
	 * @return Size of the matrix in one dimension.
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Get the distance between two locations.
	 * @param locationA The index of location-A within the location list.
	 * @param locationB The index of location-B within the location list.
	 * @return Cost of traveling from location-A to location-B.
	 */
	public long getDistance(int locationA, int locationB) {
		return distance[locationA][locationB];
	}
	
	/**
	 * Get a copy of the data for a given location.
	 * @param index The index of the location within the location list.
	 */
	public Location getLocation(int index) {
		return new Location(location[index]);
	}
	
	/**
	 * Calculates and returns the cost of traveling from location-A to location-B.
	 */
	private long calculate(int locationA, int locationB) {
		Location a = location[locationA];
		Location b = location[locationB];
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return (long)Math.sqrt((dx * dx) + (dy * dy));
	}
	
	private long[][] distance;
	private Location[] location;
	private int size;
}
