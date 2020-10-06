import java.lang.Math;
import java.util.ArrayList;

/**
 * Contains a list of Location data.
 * Can be used to calculate the distance between locations.
 */
public class LocationList {
	
	/**
	 * Add a new location to the list of locations.
	 * @return Index of the location.
	 */
	public int add(long x, long y) {
		return add(new Location(x, y));
	}
	
	/**
	 * Add a new location to the list.
	 * @return Index of the location.
	 */
	public int add(Location l) {
		assert l != null;
		assert l.index == Location.nullIndex;
		int index = data.size();
		data.add(l);
		l.index = index;
		return index;
	}
	
	/**
	 * Get the location at a given index.
	 */
	public Location get(int index) {
		return data.get(index);
	}
	
	/**
	 * Returns the number of locations within the list.
	 */
	public int size() {
		return data.size();
	}
	
	/**
	 * Returns true if the list is empty.
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	/**
	 * Returns the cost of traveling from location-A to location-B.
	 */
	public long cost(int locationA, int locationB) {
		Location a = data.get(locationA);
		Location b = data.get(locationB);
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return (long)Math.sqrt((dx * dx) + (dy * dy));
	}
	
	/**
	 * Default constructor.
	 */
	public LocationList() {
		data = new ArrayList<Location>();
	}
	
	private ArrayList<Location> data;
}
