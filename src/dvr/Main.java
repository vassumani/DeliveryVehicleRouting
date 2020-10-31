package dvr;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;

/**
 * Main class, which just contains the main() method.
 */
public class Main {

	/**
	 * Main method, which is used as the entry point for the program.
	 */
	public static void main(String[] args) {

		// Create the master routing agent
        PlatformConfiguration platformConfig = PlatformConfiguration.getDefaultNoGui();
        platformConfig.addComponent(MasterRoutingAgent.class);
        IExternalAccess routingAgent = Starter.createPlatform(platformConfig).get();

        // Get the component management service
        IComponentManagementService cms = SServiceProvider.getService(routingAgent, IComponentManagementService.class).get();
        
        // Create some delivery agents
        CreationInfo ci = new CreationInfo(SUtil.createHashMap(new String[]{"capacity"}, new Object[]{10}));
        cms.createComponent("deliveryAgent", "dvr.DeliveryAgent.class", ci);
        cms.createComponent("deliveryAgent", "dvr.DeliveryAgent.class", ci);

        
		/*
		// Create and start solver thread
		SolverThread solver = new SolverThread();
		solver.start();
		solver.setDistanceMatrix(new DistanceMatrix(Location.RandomList(25, 100)));
		solver.setSolverType(SolverType.GA);
		solver.addVehicle(6);
		solver.addVehicle(8);
		solver.addVehicle(10);
		solver.unpause();
		
		// Start and start GUI
		Gui gui = new Gui(solver);
		gui.setVisible(true);
		//*/
		
		// The main() thread will end here, however the program will continue
		// The GUI thread is a non-daemon thread and will keep the Java instance alive
		// The solver thread is a daemon thread and will stop when the GUI thread does
	}

}
