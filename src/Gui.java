import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

@SuppressWarnings("serial")
public class Gui extends JFrame {

	/**
	 * Default constructor.
	 * @param s The solver thread manager.
	 */
	public Gui(SolverThread s) {
		
		// Create buttons
		JButton buttonLoadLocations = new ButtonLoadLocations(this, s);
		JButton buttonSaveLocations = new ButtonSaveLocations(s);

		// Setup location renderer panel
		JPanel locationPanel = new LocationRenderer(s);

		// Setup control panel
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(2, 1));
		controlPanel.add(buttonLoadLocations);
		controlPanel.add(buttonSaveLocations);

		// Setup top split panel
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(controlPanel, BorderLayout.WEST);
		topPanel.add(locationPanel, BorderLayout.CENTER);

		// Setup this JFrame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(600,400));
		getContentPane().setLayout(new GridLayout());
		getContentPane().add(topPanel);
		setMinimumSize(new Dimension(100, 100));
		
		// Final command before returning
		pack();

		// Set a timer to refresh the screen from time to time
		refreshTimer = new Timer(true);
		refreshTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				repaint();
			}
		}, 500, 500);
	}
	

	/* Old code kept as example code for button callback
	static public JFrame createMainMenu() {
		JFrame frame = new JFrame("My First GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300,300);
		frame.getContentPane().add(new LocationRenderer());
		JButton button = new JButton("Press to Exit");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();
				Container c = b.getTopLevelAncestor();
				if (c instanceof Window) {
					Window w = (Window)c;
					w.dispose();
				}
			}
		});
		frame.getContentPane().add(button);
		return frame;
	}*/
	
	private Timer refreshTimer;
}
