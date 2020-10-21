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
		return run(100);
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
			
			// Loop until location list is empty
			while (!toVisit.isEmpty()) {
				
				// Find the longest available distance
				float maxDistance = 0;
				for (int i=0; i<toVisit.size(); i++) {
					maxDistance = Math.max(maxDistance, distanceMatrix.getDistance(lastVisited, toVisit.get(i)));
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
					float pDistance = 1.001f - (distanceMatrix.getDistance(lastVisited, toVisit.get(i)) / maxDistance);
					float pUsage = Math.max(getUsage(lastVisited, toVisit.get(i)), 0.001f);
					toVisitProbability[i] = pDistance + pUsage;
					pTotal += toVisitProbability[i];
					/*System.out.println(
						"Trip=" + lastVisited + "->" + toVisit.get(i) +
						", pDistance=" + pDistance +
						", pUsage=" + pUsage +
						", pTotal=" + toVisitProbability[i]); //*/
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
			
			// Calculate score for this route
			float score = (float)routeShortestFound / (float)route.travelDistance();
			score = (score * score * score) * 0.1f;

			// Update the record of the shortest route found thus far
			if (routeShortestFound > route.travelDistance()) {
				routeShortestFound = route.travelDistance();
			} else if (routeShortestFound == 0) {
				routeShortestFound = route.travelDistance();
				score = 0.1f;
			}
			
			
			//System.out.println("Score=" + score + ", Distance=" + route.travelDistance() + ", Shortest=" + routeShortestFound);
			
			// Update usage matrix
			reduce(0.005f, 0.995f);
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
	private void increase(Route route, float amount) {
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
	 * Reduce values within the path-usage matrix.
	 * @param subtract Each value has this amount subtracted.
	 * @param multiplier Each value is multiplied by this amount.
	 */
	private void reduce(float subtract, float multiplier) {
		pathUsageHighest = 1;
		for (int x=0; x<size; x++) {
			for (int y=x+1; y<size; y++) {
				float u = Math.max((pathUsage[x][y] * multiplier) - subtract, 0);
				pathUsage[x][y] = u;
				if (pathUsageHighest < u) pathUsageHighest = pathUsage[x][y];
			}
		}
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
		routeShortestFound = 0;

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
	private Random rnd;
	private long routeShortestFound;
}
