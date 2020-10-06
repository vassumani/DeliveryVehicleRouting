
public class Main {

	/**
	 * Main method, which is used as the entry point for the program.
	 */
	public static void main(String[] args) {
				
		// Start GUI
		// This will start use a secondary event thread
		Gui.create();
		
		// Thread used for main() ends here
		// Program will continue because there is another thread still running
	}

}
