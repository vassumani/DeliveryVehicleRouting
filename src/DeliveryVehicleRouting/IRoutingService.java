package DeliveryVehicleRouting;

import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 * Interface for routing services.
 */
public interface IRoutingService {

	/**
	 * Subscribe to the master routing agent to receive routing information.
	 */
	public ISubscriptionIntermediateFuture<String> subscribe();
	
}
