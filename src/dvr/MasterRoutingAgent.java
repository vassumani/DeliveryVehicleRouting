package dvr;

import java.util.ArrayList;
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
	 * Subscribe to the master routing agent to receive routing information.
	 * This is used by delivery agents to inform the master that they exist.
	 * @param capacity Number of items the delivery agent can deliver as once.
	 */
    public ISubscriptionIntermediateFuture<String> registerVehicle(int capacity) {

        // Add the capacity of the new vehicle
    	// Get the assigned vehicle index
    	int index = solver.addVehicle(capacity);
    	
    	// Record the new vehicle data
    	System.out.println("MasterRoutingAgent received new delivery agent [" + index + "], capacity " + capacity);
    	Vehicle vehicle = new Vehicle(capacity, index);
    	vehicles.add(vehicle);
    	
    	// Get result which will be returned from method
    	SubscriptionIntermediateFuture<String> result = vehicle.subscriber;
    	
        // Set a termination command to the result
        // This command will run if the subscription ends for some reason,
        // either due to an error or from the agent being shutdown. 
        result.setTerminationCommand(new TerminationCommand() {
            public void terminated(Exception reason) {
                System.out.println("removed subscriber due to: "+reason);
                for (int i=0; i<vehicles.size();) {
                	if (vehicles.get(i).subscriber == result) {
                		vehicles.remove(i);
                	} else {
                		i++;
                	}
                }
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
		System.out.println("MasterRoutingAgent starting.");

		// Start solver thread
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
			
			// Get the route list
			Route[] route = solver.getRoute();
			
			// Notify all subscribers
			for(Vehicle v : vehicles) {
				
				// Add the current route to the intermediate result
				// The if-undone is to ignore errors relating to subscribers leaving
				if ((route != null) && (v.index < route.length) && (route[v.index].getCost() > 0)) {
					v.subscriber.addIntermediateResultIfUndone(route[v.index].toString());
				} else {
					v.subscriber.addIntermediateResultIfUndone("No route");
				}
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
		vehicles = new ArrayList<Vehicle>();
		solver = new SolverThread();
	}

	/**
	 * An internal class used to store information about delivery agents.
	 */
	public class Vehicle {
		SubscriptionIntermediateFuture<String> subscriber;
		int capacity;
		int index;
		public Vehicle(int capacity, int index) {
			this.subscriber = new SubscriptionIntermediateFuture<String>();
			this.capacity = capacity;
			this.index = index;
		}
	}
	
    protected ArrayList<Vehicle> vehicles;
    SolverThread solver;
}
