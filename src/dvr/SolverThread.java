package dvr;

/**
 * Contains the current location list, solver, and best route.
 */
public class SolverThread extends Thread {
	
	/**
	 * Default constructor.
	 */
	public SolverThread() {
		paused = true;
		distanceMatrix = new DistanceMatrix(Location.RandomList(3,  10));
		vehicleCapacity = null;
		solver = new SolverGA(distanceMatrix, vehicleCapacity);
		resetRoute();
		setDaemon(true); // This thread should not stop the program from terminating
	}
	
	/**
	 * When the thread is started, this is the method which is run.
	 */
	public void run() {
		Solver localSolver;
		
		// Run forever
		// This is a daemon thread and should not keep the program from terminating
		while (true) {
			
			// Check if paused
			// Wait here if required
			synchronized(this) {
				if (paused) {
					try {
						wait();
					} catch (Exception e) {
					}
					continue; // Go back to top of while loop
				} else {
					
					// Make a copy of the solver to use
					// Cannot access solver value outside synchronised section
					localSolver = solver;
				}
			}
			
			// Run solver
			// Must not use any values which require synchronisation
			//System.out.println("Start "+localSolver.getType());
			Route[] newRoute = localSolver.run();
			//System.out.println("Finish");
			
			// Get the total cost of all the routes combined
			int newTotalCost = 0;
			for (Route r : newRoute) newTotalCost += r.getCost();
			
			// Check if new route is better than previous
			// If so then swap it out
			synchronized(this) {
				if (localSolver == solver) {
					if ((totalCost >= newTotalCost) || (totalCost < 1)) {
						route = newRoute;
						totalCost = newTotalCost;
					}
				} else {
					resetRoute();
				}
			}
		}
	}
	
	/**
	 * Check if the solver is currently running.
	 * @return True if the solver is running, or false if paused.
	 */
	public boolean isPaused() {
		synchronized(this) {
			return paused;
		}
	}
	
	/**
	 * Tell the solver to stop running and wait.
	 */
	public void pause() {
		synchronized(this) {
			paused = true;
		}
	}
	
	/**
	 * Tell the solver to resume running.
	 */
	public void unpause() {
		synchronized(this) {
			paused = false;
			notify();
		}
	}
	
	/**
	 * Change the distance matrix to use.
	 * @param dm The new distance matrix to use.
	 */
	public void setDistanceMatrix(DistanceMatrix dm) {
		synchronized(this) {
			distanceMatrix = dm;
			recreateSolver(solver.getType());
		}
	}

	/**
	 * Change the type of solver used.
	 * @param t The new solver type to use.
	 */
	public void setSolverType(SolverType t) {
		synchronized(this) {
			if (t != solver.getType()) {
				recreateSolver(t);
			}
		}
	}
	
	/**
	 * Get the current type of solver being used.
	 * @return Current solver type.
	 */
	public SolverType getSolverType() {
		synchronized(this) {
			return solver.getType();
		}
	}
	
	/**
	 * Get the current best route available.
	 * @return The current best route.
	 */
	public Route[] getRoute() {
		synchronized(this) {
			return Route.makeCopy(route);
		}
	}

	/**
	 * Get the current distance matrix being used by the solver-thread.
	 * @return Distance matrix currently in use.
	 */
	public DistanceMatrix getDistanceMatrix() {
		synchronized(this) {
			return distanceMatrix;
		}
	}

	/**
	 * Get a copy of the current solver being used by the solver-thread.
	 * @return Copy of the solver being used.
	 */
	public Solver getSolver() {
		synchronized(this) {
			switch (solver.getType()) {
			case ACO:
				return new SolverACO((SolverACO)solver);
			case GA:
				return new SolverGA((SolverGA)solver);
			default:
				return new SolverGA(distanceMatrix, vehicleCapacity);
			}
		}
	}

	/**
	 * Add a new vehicle to the list of vehicles which can make deliveries.
	 * @param capacity The capacity of the vehicle being added (number of locations it can visit).
	 * @return The vehicle index.
	 */
	public int addVehicle(int capacity) {
		capacity = Math.max(capacity, 1);
		synchronized(this) {
	    	if (vehicleCapacity == null) {
	    		vehicleCapacity = new int[1];
	    		vehicleCapacity[0] = capacity;
	    	} else {
	    		int[] temp = new int[vehicleCapacity.length + 1];
	    		for (int i=0; i<vehicleCapacity.length; i++) temp[i] = vehicleCapacity[i];
	    		temp[vehicleCapacity.length] = capacity;
	    		vehicleCapacity = temp;
	    	}
	    	recreateSolver(solver.getType());
	    	return vehicleCapacity.length - 1;
		}
	}
	
	/**
	 * Used internally to recreate the solver when needed.
	 * This method must be protected by synchronisation, as it has none of its own.
	 * @param t The type of solver to create.
	 */
	private void recreateSolver(SolverType t) {
		switch (t) {
		case ACO:
			solver = new SolverACO(distanceMatrix, vehicleCapacity);
			break;
		case GA:
			solver = new SolverGA(distanceMatrix, vehicleCapacity);
			break;
		default:
			System.out.println("Found unknown solver type while recreating solver");
			solver = new SolverGA(distanceMatrix, vehicleCapacity);
		}
		resetRoute();
	}
	
	/**
	 * Reset the current best route to nothing.
	 */
	private void resetRoute() {
		route = new Route[] {new Route(distanceMatrix)};
		totalCost = 0;
	}
	
	private boolean paused;
	private DistanceMatrix distanceMatrix;
	private Solver solver;
	private Route[] route;
	private int totalCost;
	private int[] vehicleCapacity;
}
