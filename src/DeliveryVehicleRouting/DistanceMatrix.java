package DeliveryVehicleRouting;

/**
 * Used to hold the distances between locations within a location list.
 * This class is to be treated as read-only once constructed.
 */
public class DistanceMatrix {

	/**
	 * Distance matrix constructor.
	 * @param l List of locations used to initialise the distance matrix
	 */
	public DistanceMatrix(Location[] l) {
		assert l != null;
		assert l.length > 0;
		
		// Record the location list and the number of locations there in
		size = l.length;
		location = l;
		
		// Create a new array for the distance matrix
		distance = new long[size][size];
		
		// Loop through every possible combination of two locations
		// Skip cells where the location leads to itself
		for (int x=0; x<size; x++) {
			for (int y=x+1; y<size; y++) {
				long d = calculate(x, y);
				distance[x][y] = d;
				distance[y][x] = d;
				if (maxDistance < d) maxDistance = d;
			}
		}
		
		// Set the distance values for cells where the location leads to itself
		for (int i=0; i<size; i++) {
			distance[i][i] = 0;
		}

		// Setup the location AABB
		locationAABB = new AABB();
		for (int i=0; i<size; i++) {
			locationAABB.add(location[i].coord);
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
	 * @return Cost of travelling from location-A to location-B.
	 */
	public long getDistance(int locationA, int locationB) {
		return distance[locationA][locationB];
	}
	
	/**
	 * Get a reference to the given location data.
	 * The location data should be read only.
	 * @param index The index of the location within the location list.
	 */
	public Location getLocation(int index) {
		return location[index];
	}
	
	/**
	 * Get the maximum distance between any two locations within the distance matrix.
	 * @return Max distance between locations.
	 */
	public long getMaxDistance() {
		return maxDistance;
	}
	
	/**
	 * Get a copy of the axis aligned bounding box which encompasses all locations.
	 * @return AABB encompassing all locations
	 */
	public AABB getLocationAABB() {
		return new AABB(locationAABB);
	}

	/**
	 * Calculates and returns the cost of travelling from location-A to location-B.
	 */
	private long calculate(int locationA, int locationB) {
		Location a = location[locationA];
		Location b = location[locationB];
		double dx = a.coord.x - b.coord.x;
		double dy = a.coord.y - b.coord.y;
		return (long)Math.sqrt((dx * dx) + (dy * dy));
	}
	
	private long[][] distance;
	private Location[] location;
	private int size;
	private long maxDistance;
	private AABB locationAABB;
}
