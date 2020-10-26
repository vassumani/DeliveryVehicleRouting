
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
		travelDistance = 0;
		location.reserve(d.size() + 1);
	}

	/**
	 * Route copy constructor.
	 * @param src The route to copy.
	 */
	public Route(Route src) {
		distanceMatrix = src.distanceMatrix;
		location = new IntegerList(src.location);
		travelDistance = src.travelDistance;
	}

	/**
	 * Get the reference distance matrix
	 * @return Reference to the distance matrix.
	 */
	public DistanceMatrix distanceMatrix() {
		return distanceMatrix;
	}
	
	/**
	 * Get the total distance of the route thus far.
	 * @return Total route cost/distance.
	 */
	public long travelDistance() {
		return travelDistance;
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
			travelDistance += distanceMatrix.getDistance(last, locationIndex);
		}
		location.add(locationIndex);
	}

	/**
	 * Set the distance matrix index of a location at a given index within the route.
	 * @param index Index within the route.
	 * @param locationIndex Location index within the distance matrix.
	 */
	public void setLocationIndex(int index, int locationIndex) {
		location.set(index, locationIndex);
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
		travelDistance = 0;
	}

	/**
	 * A method to convert this coordinate to a string.
	 * @return The coordinate in string format.
	 */
	@Override
	public String toString() {
		String result = "0";
		for (int i=1; i<location.size(); i++) {
			result += " -> " + location.get(i);
		}
		return result + " : Distance " + travelDistance;
	}
	
	private DistanceMatrix distanceMatrix;
	private IntegerList location;
	private long travelDistance;
}
