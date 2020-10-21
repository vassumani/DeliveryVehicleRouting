import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

@SuppressWarnings("serial")
public class Gui extends JFrame {
	private final JPanel verticalSplit;
	private final JPanel buttonPanel;
	private final LocationRenderer locationPanel;
	private final JButton button1;
	private final JButton button2;
	private Timer refreshTimer;
	
	/**
	 * Default constructor.
	 * @param d Distance matrix to be rendered.
	 */
	public Gui(DistanceMatrix d) {
		
		// Set a timer to refresh the screen from time to time
		refreshTimer = new Timer(true);
		refreshTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				repaint();
			}
		}, 50, 50);
		
		// Create buttons
		button1 = new JButton("Button1");
		button2 = new JButton("Button2");

		// Setup location panel
		locationPanel = new LocationRenderer(d);

		// Setup button panel
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		buttonPanel.add(button1);
		buttonPanel.add(button2);

		// Setup vertical panel
		verticalSplit = new JPanel(new BorderLayout());
		verticalSplit.add(buttonPanel, BorderLayout.PAGE_START);
		verticalSplit.add(locationPanel, BorderLayout.CENTER);

		// Setup this JFrame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(600,500));
		getContentPane().setLayout(new GridLayout());
		getContentPane().add(verticalSplit);
		setMinimumSize(new Dimension(100, 100));
		
		// Final command before returning
		pack();
	}
	
	/**
	 * Start the main GUI.
	 * Do this via the event queue to ensure that the action is taken within the correct thread.
	 * @param d Distance matrix to be rendered.
	 */
	static public void create(DistanceMatrix d) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Gui(d).setVisible(true);
			}
		});
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
}
