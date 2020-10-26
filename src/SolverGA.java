import java.util.Random;

/**
 * A solver which uses Genetic Algorithm (GA) to find routes.
 */
public class SolverGA implements Solver {

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

		// Loop for the requested number of iterations
		// A new route will be calculated on each iteration
		for (int it=0; it<iterations; it++) {
			
			Route child = generateChild(parent[0], parent[1]);
			if (parent[0].travelDistance() > parent[1].travelDistance()) {
				if (parent[0].travelDistance() > child.travelDistance()) {
					parent[0] = child;
				}
			} else {
				if (parent[1].travelDistance() > child.travelDistance()) {
					parent[1] = child;
				}
			}
			
		}
		
		// Record route
		Route result = parent[0];
		for (int i=1; i<parentMax; i++) {
			if (result.travelDistance() > parent[i].travelDistance()) {
				result = parent[i];
			}
		}
		return new Route(result);
	}
	
	private Route generateChild(Route parentA, Route parentB) {
		assert parentA != null;
		assert parentB != null;
		assert parentA.size() == parentB.size();

		// Get number of locations
		final int n = distanceMatrix.size();
		
		// Create child route
		Route child = new Route(distanceMatrix);

		// Create a list containing all locations, except location 0
		IntegerList allLocations = new IntegerList();
		allLocations.reserve(n);
		for (int i=1; i<n; i++) allLocations.add(i);
		
		// Calculate locations of crossover point
		int c = 1 + rnd.nextInt(parentA.size() - 2);

		// Copy values from parent-A, up to crossover point
		for (int i=0; i<c; i++) {
			int locationIndex = parentA.getLocationIndex(i);
			
			// Add location to child
			child.add(locationIndex);
			
			// Remove location from list of all locations
			allLocations.removeUnordered(allLocations.find(locationIndex));
		}
		
		// Copy values from parent-B, from crossover point onwards
		for (int i=c; i<parentB.size(); i++) {
			int locationIndex = parentB.getLocationIndex(i);

			// Check for location 0
			// Skip if this is the end of the list
			if (locationIndex == 0) {

				// Add location 0 to child, if not the last one
				if (i < (parentB.size() - 1)) child.add(0);
				
			} else {
			
				// Search for location within list of all locations
				// If not found then it has already been added as part of parent-A
				int a = allLocations.find(locationIndex);
				if (a >= 0) {
	
					// Add location to child
					child.add(locationIndex);
					
					// Remove location from list of all locations
					allLocations.removeUnordered(allLocations.find(locationIndex));
				}
			}
		}
		
		// Add any locations which are missing
		while (!allLocations.isEmpty()) {
			child.add(allLocations.pop());
		}
		
		// Add location 0 to the end of the list
		child.add(0);
		
		// Mutate the list, if required
		if (rnd.nextFloat() < 0.01) {
			
			// Swap two random locations within the list
			int indexA = 1 + rnd.nextInt(child.size() - 2);
			int indexB = 1 + rnd.nextInt(child.size() - 2);
			int locationA = child.getLocationIndex(indexA);
			int locationB = child.getLocationIndex(indexB);
			child.setLocationIndex(indexA, locationB);
			child.setLocationIndex(indexB, locationA);
		}
		
		// Return result
		return child;
	}
	
	/**
	 * Generate a new random route.
	 * @return A new random route.
	 */
	private Route generateRandom() {
		
		// Get number of locations
		final int n = distanceMatrix.size();
		
		
		// Fill the location list with all the location indices
		IntegerList unused = new IntegerList();
		unused.reserve(n);
		for (int i=1; i<n; i++) {
			unused.push(i);
		}
		
		// Create route and add locations
		Route route = new Route(distanceMatrix);
		route.add(0); // Start location
		while (!unused.isEmpty()) {
			int i = rnd.nextInt(unused.size());
			route.add(unused.get(i));
			unused.removeUnordered(i);
		}
		route.add(0); // End location
		return route;
	}
	
	/**
	 * Solver constructor.
	 * @param d Distance matrix used to initialise the solver.
	 */
	public SolverGA(DistanceMatrix d) {
		assert d != null;
		assert d.size() > 0;
		
		// Record the size of the matrix
		distanceMatrix = d;
		
		// Create a new random number generator
		rnd = new Random();
		rnd.nextFloat(); // Run once
		
		// Create candidate list
		parent = new Route[parentMax];
		for (int i=0; i<parentMax; i++) parent[i] = generateRandom();
	}
	
	final private DistanceMatrix distanceMatrix;
	final private int parentMax = 2;
	private Route[] parent;
	private Random rnd;
}
