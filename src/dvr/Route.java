package dvr;

/**
 * Contains a list of locations to be visited.
 */
public class Route {

	/**
	 * Route constructor.
	 * @param d The distance matrix to use as a reference.
	 */
	public Route(DistanceMatrix d) {
		distanceMatrix = d;
		location = new IntegerList();
		cost = 0;
		location.reserve(d.size() + 1);
	}

	/**
	 * Route copy constructor.
	 * @param src The route to copy.
	 */
	public Route(Route src) {
		distanceMatrix = src.distanceMatrix;
		location = new IntegerList(src.location);
		cost = src.cost;
	}

	/**
	 * Make a copy of the route.
	 * @param src The route to be copied (will not be altered)
	 * @return A copy of the route.
	 */
	static public Route makeCopy(Route src) {
		return new Route(src);
	}

	/**
	 * Make a copy of list of routes.
	 * @param src The routes to be copied (will not be altered)
	 * @return A copy of the routes.
	 */
	static public Route[] makeCopy(Route[] src) {
		Route[] routes = new Route[src.length];
		for (int i=0; i<src.length; i++) {
			routes[i] = new Route(src[i]);
		}
		return routes;
	}
	
	/**
	 * Get the reference distance matrix
	 * @return Reference to the distance matrix.
	 */
	public DistanceMatrix distanceMatrix() {
		return distanceMatrix;
	}
	
	/**
	 * Get the cost of the route.
	 * @return Total route cost/distance.
	 */
	public long getCost() {
		return cost;
	}

	/**
	 * Get the cost of the route list.
	 * @return Total route cost/distance.
	 */
	static public long getCost(Route[] route) {
		long totalCost = 0;
		for (Route r : route) totalCost += r.cost;
		return totalCost;
	}
	
	/**
	 * Get the current number of locations within the route.
	 * @return Number of locations within the route.
	 */
	public int size() {
		return location.size();
	}
	
	/**
	 * Add a new location to the route.
	 * @param locationIndex Index of the location within the reference distance matrix.
	 */
	public void add(int locationIndex) {
		assert (0 <= locationIndex) && (locationIndex < distanceMatrix.size());
		if (!location.isEmpty()) {
			int last = location.get(location.size() - 1);
			cost += distanceMatrix.getDistance(last, locationIndex);
		}
		location.add(locationIndex);
	}

	/**
	 * Set the distance matrix index of a location at a given index within the route.
	 * @param index Index within the route.
	 * @param locationIndex Location index within the distance matrix.
	 */
	public void setLocationIndex(int index, int locationIndex) {
		
		// Get old index
		int oldLocation = location.get(index);
		if (oldLocation != locationIndex) {
			
			// Get previous and next location, if any
			final int invalid = -1;
			int prevLocation = (index > 0) ? location.get(index - 1) : invalid;
			int nextLocation = (index < (location.size() - 1)) ? location.get(index + 1) : invalid;

			// Update location list
			location.set(index, locationIndex);
			
			// Update cost from previous location
			if (prevLocation != invalid) {
				cost +=
					distanceMatrix.getDistance(prevLocation, locationIndex) -
					distanceMatrix.getDistance(prevLocation, oldLocation);
			}
			if (nextLocation != invalid) {
				cost +=
						distanceMatrix.getDistance(locationIndex, nextLocation) -
						distanceMatrix.getDistance(oldLocation, nextLocation);
			}
		}
	}
	
	/**
	 * Get the distance matrix index of a location for a given index within the route.
	 * @param index Index of the location within the route.
	 * @return Index of the location within the distance matrix.
	 */
	public int getLocationIndex(int index) {
		return location.get(index);
	}
	
	/**
	 * Get the location at a given index within the route.
	 * @param index Index of the location within the route.
	 * @return Location data at the given index.
	 */
	public Location getLocation(int index) {
		return distanceMatrix.getLocation(location.get(index));
	}
	
	/**
	 * Check if the route is empty.
	 * @return True if the route has no locations.
	 */
	public boolean isEmpty() {
		return location.isEmpty();
	}
	
	/**
	 * Reset this route to contains no locations.
	 */
	public void clear() {
		location.clear();
		cost = 0;
	}

	/**
	 * A method to convert this coordinate to a string.
	 * @return The coordinate in string format.
	 */
	@Override
	public String toString() {
		String result = Integer.toString(location.get(0));
		for (int i=1; i<location.size(); i++) {
			result += " -> " + location.get(i);
		}
		return result + " : Distance " + cost;
	}
	
	private DistanceMatrix distanceMatrix;
	private IntegerList location;
	private long cost;
}
