package dvr;

/**
 * Axis Aligned Bounding Box. Provides the minimum and maximum coordinate used to bound the area.
 */
public class AABB {
	long xMin;
	long xMax;
	long yMin;
	long yMax;
	
	/**
	 * Default constructor.
	 */
	public AABB() {
		xMin = Long.MAX_VALUE;
		yMin = Long.MAX_VALUE;
		xMax = Long.MIN_VALUE;
		yMax = Long.MIN_VALUE;
	}

	/**
	 * Copy constructor.
	 * @param src The AABB to copy
	 */
	public AABB(AABB src) {
		xMin = src.xMin;
		xMax = src.xMax;
		yMin = src.yMin;
		yMax = src.yMax;
	}
	
	/**
	 * Check if the axis aligned bounding box is valid.
	 * @return True if the AABB is valid.
	 */
	public boolean isValid() {
		return (xMin <= xMax) && (yMin <= yMax);
	}
	
	/**
	 * Adjust the AABB to encompass the location, if it does not already.
	 * @param l The location to be included.
	 */
	public void add(Coordinate l) {
		if (xMin > l.x) xMin = l.x;
		if (xMax < l.x) xMax = l.x;
		if (yMin > l.y) yMin = l.y;
		if (yMax < l.y) yMax = l.y;
	}
}
