package dvr;

public interface Solver {

	/**
	 * Get the type of solver.
	 * @return The solver type.
	 */
	public SolverType getType();
	
	/**
	 * Calculate and return a route.
	 * The returned route may not initially be optimal but should get better each run.
	 * @return One or more routes.
	 */
	public Route[] run();

	/**
	 * Calculate and return a route.
	 * The returned route may not initially be optimal but should get better each run.
	 * @param iterations Number of attempts to find a better route.
	 * @return One or more routes.
	 */
	public Route[] run(int iterations);
}
