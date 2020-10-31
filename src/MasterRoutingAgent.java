import java.util.LinkedHashSet;
import java.util.Set;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.*;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.micro.annotation.*;

/**
 * The master routing agent.
 * This agent receives connections from delivery agents and assigns routes to them.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type= IRoutingService.class))
public class MasterRoutingAgent implements IRoutingService {
	
	/**
	 * Subscribe to the routing service.
	 * All subscribers will periodically receive updated routing information.
	 */
    public ISubscriptionIntermediateFuture<String> subscribe() {
    	SubscriptionIntermediateFuture<String> result = new SubscriptionIntermediateFuture<String>();
    	
        // Add the new subscription to the list of subscriptions.
        subscriptions.add(result);
        
        // Set a termination command to the result
        // This command will run if the subscription ends for some reason,
        // either due to an error or from the agent being shutdown. 
        result.setTerminationCommand(new TerminationCommand() {
            public void terminated(Exception reason) {
                System.out.println("removed subscriber due to: "+reason);
                subscriptions.remove(result);
            }
        });
        return result;
    }

    /**
     * The body of the agent is run after the agent has been setup.
     * For the master routing agent this will start the solver and GUI,
     * and will then start providing routing information to any subscribers.
     */
	@AgentBody
	public void agentBody(IInternalAccess ia) {
		System.out.println("Hello World, from MasterRoutingAgent.");

		// Create and start solver thread
        SolverThread solver = new SolverThread();
		solver.start();
		
		// Start and start GUI
		Gui gui = new Gui(solver);
		gui.setVisible(true);
		
		// Get the execution feature for this agent
		// This will allow the scheduling of some callback code
		IExecutionFeature exeFeat = ia.getComponentFeature(IExecutionFeature.class);
		
		// Schedule a recurring agent step to provide subscribers with routing information
		// The step will occur every 5000ms, starting in 5000ms
		exeFeat.repeatStep(5000, 5000, ia1 -> {
			
			// Notify all subscribers
			for(SubscriptionIntermediateFuture<String> subscriber: subscriptions) {
				
				// Add the current route to the intermediate result
				// The if-undone is to ignore errors relating to subscribers leaving
				subscriber.addIntermediateResultIfUndone(solver.getBestRoute().toString());
			}
			return IFuture.DONE;
		});
	}

    /**
     * A main() method which will start the program as the master routing agent.
     * In short, this will start a Jadex platform with just this agent.
     */
    public static void  main(String[] args) {
        PlatformConfiguration config = PlatformConfiguration.getDefaultNoGui();
        config.setNetworkName("MattAndAkshata");
        config.setNetworkPass("NetworkPass");
        config.addComponent(MasterRoutingAgent.class);
        config.setAwareness(true);
        Starter.createPlatform(config).get();
    }
	
	/**
	 * Default constructor.
	 */
	public MasterRoutingAgent() {
		subscriptions = new LinkedHashSet<SubscriptionIntermediateFuture<String>>();
	}
	
    protected Set<SubscriptionIntermediateFuture<String>> subscriptions;
}
