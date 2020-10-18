
public class Main {

	/**
	 * Main method, which is used as the entry point for the program.
	 */
	public static void main(String[] args) {
		
		// Create working data
		/*
		Location[] l = new Location[5];
		l[0] = new Location(0, 0, "1");
		l[1] = new Location(-5, 0, "2");
		l[2] = new Location(15, 0, "3");
		l[3] = new Location(0, -5, "4");
		l[4] = new Location(0, 15, "5");
		//*/
		/*
		Location[] l = new Location[10];
		l[0] = new Location(0, 0, "0");
		l[1] = new Location(-10, 5, "1");
		l[2] = new Location(15, 0, "2");
		l[3] = new Location(5, -10, "3");
		l[4] = new Location(10, 15, "4");
		l[5] = new Location(15, 20, "5");
		l[6] = new Location(20, 10, "6");
		l[7] = new Location(5, 15, "7");
		l[8] = new Location(-5, 15, "8");
		l[9] = new Location(10, -10, "9");
		//*/
		Location[] l = Location.RandomList(50, 20);
		DistanceMatrix d = new DistanceMatrix(l);
		UsageMatrix u = new UsageMatrix(d);
		
		/*
		for (int i=0; i<1; i++) {
			Solver solver = new Solver(d, u);
			Route r = solver.run();
			System.out.println(i + ": " + r);
		}
		//*/
		
		// Start GUI
		// This will start use a secondary event thread
		Gui.create(d, u);
		
		// Thread used for main() ends here
		// Program will continue because there is another thread still running
	}

}
