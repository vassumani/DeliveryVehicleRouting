package dvr;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.service.IService;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.*;

/**
 * A delivery agent connects to the master routing agent and receives routes.
 */
@Agent
@RequiredServices(
	@RequiredService(name="routingservices", type= IRoutingService.class, multiple=true, binding=@Binding(scope=Binding.SCOPE_GLOBAL)))
@Arguments(@Argument(name="capacity", description = "Capacity of this delivery agent", clazz=Integer.class, defaultvalue = "5"))
public class DeliveryAgent {

	/**
	 * The capacity of this delivery agent, or the number of locations
	 * that it can travel too before needing to head back to the depot.
	 */
	@AgentArgument
	protected int capacity;
	
    /**
     * The routing services are searched and added at agent startup.
     */
	@AgentService
    public void addRoutingService(IRoutingService routingservice) {
		
		// Make sure the capacity is at least one
		if (capacity < 1) {
			System.out.println("Warning: DeliveryAgent capacity was set as " + capacity + ". Now set to 1.");
			capacity = 1;
		}
		
		// Obtain a subscription to the master routing service
        ISubscriptionIntermediateFuture<String> subscription = routingservice.registerVehicle(capacity);
        
        // Wait for results from the subscription
        while(subscription.hasNextIntermediateResult()) {
        	
        	// Get the latest route provided
            String route = subscription.getNextIntermediateResult();
            String platform = ((IService)routingservice).getServiceIdentifier().getProviderId().getPlatformName();
            System.out.println("DeliveryAgent received new route from "+platform+": "+route);
        }
    }
	
	@AgentCreated
	public void created() {
		System.out.println("New delivery agent with a capacity of " + capacity);
	}
	
    @AgentBody
    public void body() {
        System.out.println("Hello World, from DeliveryAgent.");
        System.out.println("New delivery agent with a capacity of " + capacity);
    }

    /**
     * A main() method which will start the program as a delivery agent.
     * In short, this will start a Jadex platform with just this agent.
     */
    public static void  main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefaultNoGui();
        config.setNetworkName("MattAndAkshata");
        config.setNetworkPass("NetworkPass");
        config.addComponent(DeliveryAgent.class);
        config.setAwareness(true);
        Starter.createPlatform(config).get();
    }
}
