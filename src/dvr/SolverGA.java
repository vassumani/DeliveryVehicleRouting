package dvr;

import java.util.Random;

/**
 * A solver which uses Genetic Algorithm (GA) to find routes.
 */
public class SolverGA implements Solver {

	/**
	 * The number of parent routes which are mixed together to create new candidate routes.
	 */
	static final public int parentMax = 6;
	
	/**
	 * Number of candidate routes to generate when making new routes.
	 */
	static final public int newCandidateCount = 20;

	/**
	 * Number of completely random parent routes to add when checking candidates.
	 * This value must be less than parentMax.
	 */
	static final public int newRandomParents = 1;
	
	/**
	 * Get the type of solver.
	 * @return The solver type.
	 */
	public SolverType getType() {
		return SolverType.GA;
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

		// Create list to hold candidate routes
		Route[] candidate = new Route[newCandidateCount + parentMax];

		// Loop for the requested number of iterations
		// A new route will be calculated on each iteration
		for (int it=0; it<iterations; it++) {

			// Generate and store new candidate routes
			for (int c=0; c<newCandidateCount; c++) {
				candidate[c] = generateChild(parent[c % parentMax], parent[(c + 1) % parentMax]);
			}
		
			// Add parent routes to candidate list
			for (int p=0; p<parentMax; p++) {
				candidate[newCandidateCount + p] = parent[p];
			}

			//for (int c=0; c<candidate.length; c++) System.out.println("  Candidate["+c+"]: "+candidate[c]);
			
			// Loop through candidate list and find best options
			// Record best options within parent list
			for (int p=0; p<(parentMax - newRandomParents); p++) {
				
				// Find best candidate
				int best = p;
				for (int c=p+1; c<candidate.length; c++) {
					if (candidate[c].travelDistance() < candidate[best].travelDistance()) {
						best = c;
					}
				}
				
				// Record best candidate in parent list
				// Overwrite removed entry with something else
				parent[p] = candidate[best];
				candidate[best] = candidate[p];
				//System.out.println("  Parent["+p+"]: "+parent[p]);
			}
			
			// Add random parents to the parent list
			for (int p=parentMax-newRandomParents; p<parentMax; p++) {
				parent[p] = generateRandom();
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
	
	/**
	 * Generate a child route using two parent routes.
	 * @param parentA A parent route.
	 * @param parentB A parent route.
	 * @return A newly generated route.
	 */
	private Route generateChild(Route parentA, Route parentB) {
		assert parentA != null;
		assert parentB != null;
		assert parentA.size() == parentB.size();

		// Get number of locations
		final int n = distanceMatrix.size();
		
		// Create child route
		Route child = new Route(distanceMatrix);

		// Calculate crossover point for child
		// The child will consist of data from parent-A before the crossover, and parent-B after the crossover
		int c = 1 + rnd.nextInt(parentA.size() - 2);

		// Create a list containing all locations
		IntegerList allLocations = new IntegerList();
		allLocations.reserve(n);
		for (int i=0; i<n; i++) allLocations.add(i);
		
		// Copy values from parent-A, up to crossover point
		// Also remove the visited locations from the location list
		for (int i=0; i<c; i++) {
			int locationIndex = parentA.getLocationIndex(i);
			
			// Add location to child
			child.add(locationIndex);
			
			// Remove location from list of all locations
			int toRemove = allLocations.find(locationIndex);
			if (toRemove != -1) allLocations.removeUnordered(toRemove);
		}
		
		// Make a copy of the location list
		// Remove the locations from parent-B which are after the crossover point
		// Whatever locations are left have been missed (not in first part of parent-A, or second part of parent-B)
		IntegerList missedLocations = new IntegerList(allLocations);
		for (int i=c; i<parentB.size(); i++) {
			int toRemove = missedLocations.find(parentB.getLocationIndex(i));
			if (toRemove != -1) missedLocations.removeUnordered(toRemove);
		}
		
		// Copy values from parent-B, from crossover point onwards
		// Check for duplicate values and replace them with missed locations
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
					
				} else {
					
					// Add location from missed location list
					child.add(missedLocations.pop());
				}
			}
		}
		
		// Add any locations which are missing
		while (!missedLocations.isEmpty()) {
			child.add(missedLocations.pop());
		}
		
		// Add location 0 to the end of the list
		child.add(0);
		
		// Mutate the list, if required
		float mutateThreshold = ((parentA.travelDistance() == parentB.travelDistance()) ? 0.9f : 0.01f);
		while (rnd.nextFloat() < mutateThreshold) {
			mutateThreshold *= 0.8f;
			
			// Swap two random locations within the list
			int indexA = 1 + rnd.nextInt(child.size() - 2);
			int indexB = 1 + rnd.nextInt(child.size() - 2);
			int locationA = child.getLocationIndex(indexA);
			int locationB = child.getLocationIndex(indexB);
			child.setLocationIndex(indexA, locationB);
			child.setLocationIndex(indexB, locationA);
		}
		
		/*
		System.out.println("  Crossover="+c);
		System.out.println("  ParentA: "+parentA);
		System.out.println("  Child:   "+child);
		System.out.println("  ParentB: "+parentB);
		//*/
		
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
	 * Get a string version of one of the parent routes.
	 * @param index Index of the requested parent route.
	 * @return The route in string form.
	 */
	public String getParentString(int index) {
		return parent[index].toString();
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
	
	/**
	 * Copy constructor.
	 */
	public SolverGA(SolverGA src) {
		assert src != null;

		// Record the size of the matrix
		distanceMatrix = src.distanceMatrix;
		
		// Create a new random number generator
		rnd = new Random();
		rnd.nextFloat(); // Run once
		
		// Create candidate list
		parent = new Route[parentMax];
		for (int i=0; i<parentMax; i++) parent[i] = new Route(src.parent[i]);
	}
	
	final private DistanceMatrix distanceMatrix;
	private Route[] parent;
	private Random rnd;
}
