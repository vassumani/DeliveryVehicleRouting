package DeliveryVehicleRouting;

public interface Solver {

	/**
	 * Get the type of solver.
	 * @return The solver type.
	 */
	public SolverType getType();
	
	/**
	 * Calculate and return a route.
	 * The returned route may not initially be optimal but should get better each run.
	 * @return A calculated route.
	 */
	public Route run();

	/**
	 * Calculate and return a route.
	 * The returned route may not initially be optimal but should get better each run.
	 * @param iterations Number of attempts to find a better route.
	 * @return A calculated route.
	 */
	public Route run(int iterations);
}
