
/**
 * Contains the pheromone markers laid out by previous successful journeys.
 */
public class UsageMatrix {

	/**
	 * Usage matrix constructor.
	 * @param d Distance matrix used to initialise the usage matrix.
	 */
	public UsageMatrix(DistanceMatrix d) {
		assert d != null;
		assert d.size() > 0;
		
		// Record the size of the matrix
		size = d.size();
		maxUsage = 1;
		averageUsage = 1;
		
		// Create a new array for the usage matrix
		usage = new float[size][size];
		for (int x=0; x<size; x++) {
			for (int y=x+1; y<size; y++) {
				usage[x][y] = 0;
			}
		}
	}
	
	/**
	 * Use a route to increase usage values on a certain path by a certain amount.
	 * @param route The route which defines the path.
	 * @param amount The amount to increase each value along the length of the path.
	 */
	public void increase(Route route, float amount) {
		for (int i=1; i<route.size(); i++) {
			int x = route.getLocationIndex(i - 1);
			int y = route.getLocationIndex(i);
			// usage[lower_value][higher_value] += amount;
			if (x != y) {
				if (x > y) {int t = x; x = y; y = t;} // Swap x and y to ensure x is lower
				usage[x][y] += amount;
				if (maxUsage < usage[x][y]) maxUsage = usage[x][y];
			}
		}
	}
	
	/**
	 * Reduce values within matrix by a small amount.
	 */
	public void reduce() {
		reduceByMultiplier(0.98f);
	}

	/**
	 * Reduce values within matrix by a specific amount.
	 * @param amount The amount to subtract from each usage value.
	 */
	public void reduceFixedAmount(float amount) {
		assert amount > 0;
		float count = 0;
		maxUsage = 1;
		for (int x=0; x<size; x++) {
			for (int y=x+1; y<size; y++) {
				float u = Math.max(usage[x][y] - amount, 0);
				usage[x][y] = u;
				if (maxUsage < u) maxUsage = usage[x][y];
				averageUsage += u * u;
				count++;
			}
		}
		averageUsage = (float)Math.sqrt(averageUsage / count);
	}

	/**
	 * Reduce values within matrix by a multiplier amount.
	 * @param amount The amount to subtract from each usage value.
	 */
	public void reduceByMultiplier(float multiplier) {
		assert (0 <= multiplier) && (multiplier <= 1);
		float count = 0;
		maxUsage = 1;
		for (int x=0; x<size; x++) {
			for (int y=x+1; y<size; y++) {
				float u = usage[x][y] * multiplier;
				usage[x][y] = u;
				if (maxUsage < u) maxUsage = usage[x][y];
				averageUsage += u * u;
				count++;
			}
		}
		averageUsage = (float)Math.sqrt(averageUsage / count);
	}

	/**
	 * Get the size of the usage matrix in one dimension.
	 * @return Size of the matrix in one dimension.
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Get the usage value (pheromone level) between two locations.
	 * @param locationA The index of location-A within the location list.
	 * @param locationB The index of location-B within the location list.
	 * @return A measure of the amount of traffic moving from location-A to location-B.
	 */
	public float getUsage(int locationA, int locationB) {
		// return usage[lower_value][higher_value];
		if (locationA == locationB) {
			return 0;
		} else if (locationA < locationB) {
			return usage[locationA][locationB];
		} else {
			return usage[locationB][locationA];
		}
	}
	
	/**
	 * Get the highest usage value contained within the matrix.
	 * @return The highest single usage value.
	 */
	public float getMaxUsage() {
		return maxUsage;
	}

	/**
	 * Get the average usage value contained within the matrix.
	 * @return The average of all usage values.
	 */
	public float getAverageUsage() {
		return averageUsage;
	}
	
	private float[][] usage;
	private int size;
	private float maxUsage;
	private float averageUsage;
}
