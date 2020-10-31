package dvr;

import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 * Interface for routing services.
 */
public interface IRoutingService {

	/**
	 * Subscribe to the master routing agent to receive routing information.
	 * This is used by delivery agents to inform the master that they exist.
	 * @param capacity Number of items the delivery agent can deliver as once.
	 */
	public ISubscriptionIntermediateFuture<String> registerVehicle(int capacity);
	
}
