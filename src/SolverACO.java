import java.util.Random;

/**
 * A solver which uses Ant Colony Optimisation (ACO) to find routes.
 */
public class SolverACO implements Solver {

	/**
	 * Calculate and return a route.
	 * The returned route may not initially be optimal but should get better each run.
	 * @return A calculated route.
	 */
	public Route run() {
		return run(1);
	}
	
	/**
	 * Calculate and return a route.
	 * The returned route may not initially be optimal but should get better each run.
	 * @param iterations Number of attempts to find a better route.
	 * @return A calculated route.
	 */
	public Route run(int iterations) {
		final int n = distanceMatrix.size();

		// Create a route to return
		Route route = new Route(distanceMatrix);
		Route bestRoute = new Route(distanceMatrix);
		
		// Create list to contain the probability of visiting a specific location
		float[] toVisitProbability = new float[n];

		// Create a list of locations yet to be visited
		// The list is of the location indices
		IntegerList toVisit = new IntegerList();
		toVisit.reserve(n);
		
		// Loop for the requested number of iterations
		// A new route will be calculated on each iteration
		for (int it=0; it<iterations; it++) {
		
			//String debug;
			
			// Fill the location list with all the location indices
			toVisit.clear();
			toVisit.reserve(n);
			for (int i=1; i<n; i++) {
				toVisit.push(i);
			}
			
			// Reset the route data
			// Add the starting location (location index 0)
			route.clear();
			route.add(0);
			int lastVisited = 0;
			
			// Declare some variables
			float maxDistance = 0; // Maximum distance between lastVisited and any toVisit[]
			
			// Loop until location list is empty
			while (!toVisit.isEmpty()) {
				
				// Find the longest available distance, if not already known
				if (maxDistance < 1) {
					for (int i=0; i<toVisit.size(); i++) {
						maxDistance = Math.max(maxDistance, distanceMatrix.getDistance(lastVisited, toVisit.get(i)));
					}
				}

				/*
				debug = "toVisit: " + toVisit.get(0);
				for (int i=1; i<toVisit.size(); i++) {
					debug += ", " + toVisit.get(i);
				}
				System.out.println(debug);
				//*/
				
				// Calculate the probability of visiting each remaining location
				// Fill the probability list accordingly
				float pTotal = 0;
				for (int i=0; i<toVisit.size(); i++) {
					float pDistance = 1.0f - (distanceMatrix.getDistance(lastVisited, toVisit.get(i)) / maxDistance);
					float pUsage = getUsage(lastVisited, toVisit.get(i)) / averageUsage;
					toVisitProbability[i] = pDistance + pUsage;
					pTotal += toVisitProbability[i];
					/*System.out.println(
						"Trip=" + lastVisited + "->" + toVisit.get(i) +
						", pDistance=" + pDistance +
						", pUsage=" + pUsage); //*/
				}
				
				// Pick a location
				pTotal *= rnd.nextFloat();
				for (int i=toVisit.size()-1; i>=0; i--) {
					if ((pTotal > toVisitProbability[i]) && (i > 0)) {
						pTotal -= toVisitProbability[i];
					} else {
						route.add(toVisit.get(i));
						lastVisited = toVisit.get(i);
						toVisit.removeUnordered(i);
						break;
					}
				}
			}
			
			// Add the finish location to the end of the list
			// This is for the return trip
			route.add(0);
			
			// Update the rolling average of route scores
			// Calculate score for this route
			float score;
			if (routeScoreAverage == 0) {
				routeScoreAverage = route.travelDistance();
				score = 1;
			} else {
				routeScoreAverage = (route.travelDistance() + (routeScoreAverage * 99f)) / 100f;
				score = routeScoreAverage / route.travelDistance();
				score *= score * score;
				score -= 1;
			}
			
			// Update usage matrix
			//usageMatrix.reduceFixedAmount(0.0001f * usageMatrix.getAverageUsage());
			reduceByMultiplier(0.9999f);
			if (score > 0) increase(route, score);

			// Check if route is acceptable
			if ((route.travelDistance() < bestRoute.travelDistance()) || (bestRoute.travelDistance() == 0)) {
				Route t = bestRoute;
				bestRoute = route;
				route = t;
			}
		}
		
		//System.out.println("routeScoreAverage = " + routeScoreAverage);
		
		// Record route
		return bestRoute;
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
				pathUsage[x][y] += amount;
				if (pathUsageHighest < pathUsage[x][y]) pathUsageHighest = pathUsage[x][y];
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
		pathUsageHighest = 1;
		for (int x=0; x<size; x++) {
			for (int y=x+1; y<size; y++) {
				float u = Math.max(pathUsage[x][y] - amount, 0);
				pathUsage[x][y] = u;
				if (pathUsageHighest < u) pathUsageHighest = pathUsage[x][y];
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
		pathUsageHighest = 1;
		for (int x=0; x<size; x++) {
			for (int y=x+1; y<size; y++) {
				float u = pathUsage[x][y] * multiplier;
				pathUsage[x][y] = u;
				if (pathUsageHighest < u) pathUsageHighest = pathUsage[x][y];
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
			return pathUsage[locationA][locationB];
		} else {
			return pathUsage[locationB][locationA];
		}
	}
	
	/**
	 * Get the highest usage value contained within the matrix.
	 * @return The highest single usage value.
	 */
	public float getMaxUsage() {
		return pathUsageHighest;
	}

	/**
	 * Get the average usage value contained within the matrix.
	 * @return The average of all usage values.
	 */
	public float getAverageUsage() {
		return averageUsage;
	}

	/**
	 * Solver constructor.
	 * @param d Distance matrix used to initialise the usage matrix.
	 */
	public SolverACO(DistanceMatrix d) {
		assert d != null;
		assert d.size() > 0;
		
		// Record the size of the matrix
		distanceMatrix = d;
		
		// Setup general values
		size = d.size();
		pathUsageHighest = 1;
		averageUsage = 1;
		routeScoreAverage = 0;

		// Create a new random number generator
		rnd = new Random();
		rnd.nextFloat(); // Run once
		
		// Create a new array for the path-usage matrix
		// Only the top half of the matrix is used
		pathUsage = new float[size][size];
		for (int x=0; x<size; x++) {
			for (int y=x+1; y<size; y++) {
				pathUsage[x][y] = 0;
			}
		}
	}
	
	final private DistanceMatrix distanceMatrix;
	final private int size;
	private float[][] pathUsage;
	private float pathUsageHighest;
	private float averageUsage;
	private Random rnd;
	private float routeScoreAverage;
}
