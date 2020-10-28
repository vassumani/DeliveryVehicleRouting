
public class Main {

	/**
	 * Main method, which is used as the entry point for the program.
	 */
	public static void main(String[] args) {
		
		// Create working data
		/*
		Location[] l = new Location[7];
		l[0] = new Location(0, 0, "0");
		l[1] = new Location(5, -2, "1");
		l[2] = new Location(8, 4, "2");
		l[3] = new Location(4, 8, "3");
		l[4] = new Location(-2, 5, "4");
		l[5] = new Location(2, 3, "5");
		l[6] = new Location(4, 5, "6");
		//*/
		/*
		Location[] l = new Location[12];
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
		l[10] = new Location(2, -5, "10");
		l[11] = new Location(12, -5, "11");
		//*/
		/*
		Location[] l = new Location[16];
		l[0] = new Location(0, 0, "0");
		l[1] = new Location(-10, 5, "1");
		l[2] = new Location(15, 8, "2");
		l[3] = new Location(5, -8, "3");
		l[4] = new Location(8, 15, "4");
		l[5] = new Location(15, 20, "5");
		l[6] = new Location(20, 10, "6");
		l[7] = new Location(5, 5, "7");
		l[8] = new Location(-5, 15, "8");
		l[9] = new Location(10, -10, "9");
		l[10] = new Location(-5, -10, "10");
		l[11] = new Location(12, -5, "11");
		l[12] = new Location(20, 0, "12");
		l[13] = new Location(10, 0, "13");
		l[14] = new Location(-2, 8, "14");
		l[15] = new Location(10, 10, "15");
		//*/
		//Location[] l = Location.RandomList(40, 20);
		//DistanceMatrix d = new DistanceMatrix(l);

		/*
		Solver solver = new SolverGA(d);
		for (int i=0; i<5; i++) {
			System.out.println(" --- Attempt " + i + " ---");
			Route r = solver.run(1);
			System.out.println(i + ": " + r);
			System.out.println();
		}
		//*/
		
		// Create and start solver thread
		SolverThread solver = new SolverThread();
		solver.start();
		
		// Start and start GUI
		Gui gui = new Gui(solver);
		gui.setVisible(true);
		
		// The main() thread will end here, however the program will continue
		// The GUI thread is a non-daemon thread and will keep the Java instance alive
		// The solver thread is a daemon thread and will stop when the GUI thread does
	}

}
