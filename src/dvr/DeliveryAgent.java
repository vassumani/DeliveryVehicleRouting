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
public class DeliveryAgent {

    /**
     * The routing services are searched and added at agent startup.
     */
	@AgentService
    public void addRoutingService(IRoutingService routingservice) {
		
		// Obtain a subscription to the master routing service
        ISubscriptionIntermediateFuture<String> subscription = routingservice.subscribe();
        
        // Wait for results from the subscription
        while(subscription.hasNextIntermediateResult()) {
        	
        	// Get the latest route provided
            String route = subscription.getNextIntermediateResult();
            String platform = ((IService)routingservice).getServiceIdentifier().getProviderId().getPlatformName();
            System.out.println("New route received from "+platform+": "+route);
        }
    }
	
    @AgentBody
    public void body() {
        System.out.println("Hello World, from DeliveryAgent.");
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
