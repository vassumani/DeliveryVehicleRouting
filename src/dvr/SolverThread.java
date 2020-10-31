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
		solver = new SolverGA(distanceMatrix);
		bestRoute = new Route(distanceMatrix);
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
			Route newRoute = localSolver.run();
			
			// Check if new route is better than previous
			// If so then swap it out
			synchronized(this) {
				if (localSolver == solver) {
					if ((bestRoute.travelDistance() >= newRoute.travelDistance()) || (bestRoute.travelDistance() < 1)) {
						bestRoute = newRoute;
					}
				} else {
					bestRoute = new Route(distanceMatrix);
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
			switch (solver.getType()) {
			case ACO:
				solver = new SolverACO(distanceMatrix);
				break;
			case GA:
				solver = new SolverGA(distanceMatrix);
				break;
			default:
				System.out.println("Unknown solver type in SolverThread.setDistanceMatrix");
				solver = new SolverGA(distanceMatrix);
			}
			bestRoute = new Route(distanceMatrix);
		}
	}

	/**
	 * Change the type of solver used.
	 * @param t The new solver type to use.
	 */
	public void setSolverType(SolverType t) {
		synchronized(this) {
			if (t != solver.getType()) {
				switch (t) {
				case ACO:
					solver = new SolverACO(distanceMatrix);
					break;
				case GA:
					solver = new SolverGA(distanceMatrix);
					break;
				default:
					System.out.println("Unknown solver type in SolverThread.setSolverType");
					solver = new SolverGA(distanceMatrix);
				}
				bestRoute = new Route(distanceMatrix);
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
	public Route getBestRoute() {
		synchronized(this) {
			return new Route(bestRoute);
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
				return new SolverGA(distanceMatrix);
			}
		}
	}

	private boolean paused;
	private DistanceMatrix distanceMatrix;
	private Solver solver;
	private Route bestRoute;
}
