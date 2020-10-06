import java.util.ArrayList;

/**
 * A route contains a list of locations which are to be visited, and the total cost of the route.
 */
public class Route {

	/**
	 * Add a new location to the list.
	 */
	public void add(int locationIndex) {
		add(refList.get(locationIndex));
	}
	
	/**
	 * Add a new location to the list.
	 */
	public void add(Location location) {
		assert location.index != Location.nullIndex;
		locations.add(location);
		if (last != null) {
			cost += refList.cost(location.index, last.index);
		}
		last = location;
	}

	/**
	 * Get the location at a given index.
	 */
	public Location get(int index) {
		return locations.get(index);
	}
	
	/**
	 * Returns the number of locations within the list.
	 */
	public int size() {
		return locations.size();
	}
	
	/**
	 * Returns true if the list is empty.
	 */
	public boolean isEmpty() {
		return locations.isEmpty();
	}
	
	/**
	 * Returns the current cost of the route.
	 */
	public long getCost() {
		return cost;
	}
	
	/**
	 * Reset this route to contains no locations.
	 */
	public void clear() {
		locations.clear();
		last = null;
		cost = 0;
	}
	
	/**
	 * Default constructor.
	 */
	public Route(LocationList l) {
		locations = new ArrayList<Location>();
		last = null;
		cost = 0;
		refList = l;
	}
	
	private ArrayList<Location> locations;
	private Location last;
	private long cost;
	private LocationList refList;
}
