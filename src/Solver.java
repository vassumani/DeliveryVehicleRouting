import java.util.Random;

public class Solver {
	private final DistanceMatrix distanceMatrix;
	private final UsageMatrix usageMatrix;
	private Route bestRoute;
	private Route route;
	private Random rnd;
	private float[] prop;

	/**
	 * Solver constructor.
	 * @param d The reference distance matrix for this location renderer.
	 * @param u The usage matrix for this location renderer.
	 */
	public Solver(DistanceMatrix d, UsageMatrix u) {
		distanceMatrix = d;
		usageMatrix = u;
		bestRoute = new Route(distanceMatrix);
		route = new Route(distanceMatrix);
		rnd = new Random();
		rnd.nextFloat();
	}
	
	/**
	 * Try to find a route.
	 * @return A route found (may not be the best available).
	 */
	public Route run() {
		return run(100);
	}
	
	/**
	 * Try to find a route.
	 * @param iterations Number of attempts at finding a better route.
	 * @return A route found (may not be the best available).
	 */
	public Route run(int iterations) {
		final int n = distanceMatrix.size();
		final float maxDist = distanceMatrix.getMaxDistance();

		// Setup list to contain probabilities
		float[] prob = new float[n];
		float p;

		// Create list to hold location indices
		IntegerList location = new IntegerList();
		location.reserve(n);
		
		// Loop until a good route is found
		for (int it=0; it<iterations; it++) {
		
			// Setup list of location indices
			location.clear();
			location.reserve(n);
			for (int i=1; i<n; i++) {
				location.push(i);
			}
			
			// Reset route
			route.clear();
			route.add(0); // Start location
			
			// Loop until location list is empty
			int lastLocation = 0;
			while (!location.isEmpty()) {
				
				// Fill probability list
				p = 0;
				for (int i=0; i<location.size(); i++) {
					prob[i] =
						(1.0f - (distanceMatrix.getDistance(lastLocation, location.get(i)) / maxDist)) +
						usageMatrix.getUsage(lastLocation, location.get(i)) / usageMatrix.getAverageUsage();
						//usageMatrix.getUsage(lastLocation, location.get(i));
					p += prob[i];
				}
				
				// Pick a location
				p *= rnd.nextFloat();
				for (int i=0; i<location.size(); i++) {
					if (p > prob[i]) {
						p -= prob[i];
					} else {
						route.add(location.get(i));
						location.removeUnordered(i);
						break;
					}
				}
			}
			
			// Add return trip
			route.add(0); // Finish location
			
			// Update usage matrix
			usageMatrix.reduceFixedAmount(0.0001f * usageMatrix.getAverageUsage());
			//usageMatrix.reduceByMultiplier(0.99999f);
			float score = (bestRoute.travelDistance() / route.travelDistance());
			score *= score * score;
			if (score > 0) usageMatrix.increase(bestRoute, score);

			// Check if route is acceptable
			if ((route.travelDistance() < bestRoute.travelDistance()) || (bestRoute.travelDistance() == 0)) {
				Route t = bestRoute;
				bestRoute = route;
				route = t;
			}
		}
		
		// Record route
		return bestRoute;
	}
}
