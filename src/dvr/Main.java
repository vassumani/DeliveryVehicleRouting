package dvr;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

/**
 * Main class, which just contains the main() method.
 */
public class Main {

	/**
	 * Main method, which is used as the entry point for the program.
	 */
	public static void main(String[] args) {

		// Create the master routing agent and a delivery agent
        PlatformConfiguration platformConfig = PlatformConfiguration.getDefaultNoGui();
        platformConfig.addComponent(MasterRoutingAgent.class);
        platformConfig.addComponent(DeliveryAgent.class);
        platformConfig.addComponent(DeliveryAgent.class);
        Starter.createPlatform(platformConfig).get();
        
		/*
		// Create and start solver thread
		SolverThread solver = new SolverThread();
		solver.start();
		
		// Start and start GUI
		Gui gui = new Gui(solver);
		gui.setVisible(true);
		//*/
		
		// The main() thread will end here, however the program will continue
		// The GUI thread is a non-daemon thread and will keep the Java instance alive
		// The solver thread is a daemon thread and will stop when the GUI thread does
	}

}
