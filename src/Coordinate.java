
/**
 * Contains coordinate information only.
 * This class is to be treated as read-only once constructed.
 */
public class Coordinate {
	public final long x;
	public final long y;

	/**
	 * Default coordinate constructor.
	 */
	public Coordinate() {
		x = 0;
		y = 0;
	}
	
	/**
	 * Coordinate constructor with information specified.
	 * @param x X-component of this coordinate.
	 * @param y Y-component of this coordinate.
	 */
	public Coordinate(long X, long Y) {
		x = X;
		y = Y;
	}
	
	/**
	 * Copy constructor for coordinate data.
	 * @param src The source location which should be copied.
	 */
	public Coordinate(Coordinate l) {
		x = l.x;
		y = l.y;
	}
	
	/**
	 * A method to convert this coordinate to a string.
	 * @return The coordinate in string format.
	 */
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
