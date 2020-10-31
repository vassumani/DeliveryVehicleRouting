package DeliveryVehicleRouting;

import java.util.Random;

/**
 * A solver which uses Ant Colony Optimisation (ACO) to find routes.
 */
public class SolverACO implements Solver {

	/**
	 * Get the type of solver.
	 * @return The solver type.
	 */
	public SolverType getType() {
		return SolverType.ACO;
	}
	
	/**
	 * Calculate and return a route.
	 * The returned route may not initially be optimal but should get better each run.
	 * @return A calculated route.
	 */
	public Route run() {
		return run(10);
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
		
			/*
			String debug;
			System.out.println();
			//*/
			
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
					float pUsage = Math.max(usage[lastVisited][toVisit.get(i)], 0.001f);
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
			
			// Update average route distance
			if (routeAverageDistance == 0) {
				routeAverageDistance = route.travelDistance();
			} else {
				routeAverageDistance = ((routeAverageDistance * 99) + route.travelDistance()) / 100;
			}
			
			
			// Calculate score for this route
			float score = (float)routeAverageDistance / (float)route.travelDistance();
			score = (float)Math.pow(score, 7) * 0.1f;

			//System.out.println("Score="+score+", Distance="+route.travelDistance()+", Average="+(long)routeAverageDistance);

			// Update usage matrix
			reduce(0.999f);
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
	 * Get the average distance travelled by the calculated routes.
	 * @return Average route distance.
	 */
	public long getAverageDistance() {
		return routeAverageDistance;
	}
	
	/**
	 * Use a route to increase usage values on a certain path by a certain amount.
	 * @param route The route which defines the path.
	 * @param amount The amount to increase each value along the length of the path.
	 */
	private void increase(Route route, float amount) {
		for (int i=1; i<route.size(); i++) {
			int a = route.getLocationIndex(i - 1);
			int b = route.getLocationIndex(i);
			usage[a][b] += amount;
			if (usageMax < usage[a][b]) usageMax = usage[a][b];
		}
	}
	
	/**
	 * Reduce values within the path-usage matrix.
	 * @param multiplier Each value is multiplied by this amount.
	 */
	private void reduce(float multiplier) {
		usageMax = usageMaxSmallest;
		for (int a=0; a<size; a++) {
			for (int b=0; b<size; b++) {
				float u = usage[a][b] * multiplier;
				usage[a][b] = u;
				if (usageMax < u) usageMax = usage[a][b];
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
		return usage[locationA][locationB];
	}
	
	/**
	 * Get the highest usage value contained within the matrix.
	 * @return The highest single usage value.
	 */
	public float getMaxUsage() {
		return usageMax;
	}

	/**
	 * Solver constructor.
	 * @param d Distance matrix used to initialise the solver.
	 */
	public SolverACO(DistanceMatrix d) {
		assert d != null;
		assert d.size() > 0;
		
		// Record the size of the matrix
		distanceMatrix = d;
		
		// Setup general values
		size = d.size();
		usageMax = usageMaxSmallest;
		routeAverageDistance = 0;

		// Create a new random number generator
		rnd = new Random();
		rnd.nextFloat(); // Run once
		
		// Create a new array for the path-usage matrix
		// Only the top half of the matrix is used
		usage = new float[size][size];
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				usage[x][y] = 0;
			}
		}
	}

	/**
	 * Copy constructor.
	 */
	public SolverACO(SolverACO src) {
		assert src != null;
		
		// Record the size of the matrix
		distanceMatrix = src.distanceMatrix;
		
		// Setup general values
		size = src.size;
		usageMax = src.usageMax;
		routeAverageDistance = src.routeAverageDistance;

		// Create a new random number generator
		rnd = new Random();
		rnd.nextFloat(); // Run once
		
		// Create a new array for the path-usage matrix
		// Only the top half of the matrix is used
		usage = new float[size][size];
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				usage[x][y] = src.usage[x][y];
			}
		}
	}
	
	static final private float usageMaxSmallest = 0.001f;
	
	final private DistanceMatrix distanceMatrix;
	final private int size;
	private float[][] usage;
	private float usageMax;
	private Random rnd;
	private long routeAverageDistance;
}
