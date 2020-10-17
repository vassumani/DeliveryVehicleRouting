import java.awt.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class Gui extends JFrame {
	private final JSplitPane verticalSplit;
	private final LocationRenderer topPanel;
	private final JPanel bottomPanel;
	private final JButton button1;
	private final JButton button2;
	
	/**
	 * Default constructor.
	 */
	public Gui() {
		
		// Create buttons
		button1 = new JButton("Button1");
		button2 = new JButton("Button2");
		
		// Setup top panel
		/*
		Location[] l = new Location[5];
		l[0] = new Location(0, 0, "1");
		l[1] = new Location(-5, 0, "2");
		l[2] = new Location(15, 0, "3");
		l[3] = new Location(0, -5, "4");
		l[4] = new Location(0, 15, "5");
		//*/
		Location[] l = Location.RandomList(10, 20);
		DistanceMatrix d = new DistanceMatrix(l);
		UsageMatrix u = new UsageMatrix(d);
		topPanel = new LocationRenderer(d, u);
		final int iMax = 3;
		for (int j=0; j<3; j++) {
			Route r = new Route(d);
			for (int i=0; i<iMax; i++) r.add(i + (j * iMax));
			topPanel.getRoutes().add(r);
		}
		
		// Setup bottom panel
		bottomPanel = new JPanel();
		bottomPanel.setPreferredSize(new Dimension(0, 50));
		bottomPanel.setLayout(new GridLayout(1, 2));
		bottomPanel.add(button1);
		bottomPanel.add(button2);

		// Setup the layout panels
		verticalSplit = new JSplitPane();
		verticalSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		verticalSplit.setTopComponent(topPanel);
		verticalSplit.setBottomComponent(bottomPanel);
		verticalSplit.setDividerLocation(520);

		// Setup this JFrame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(800,600));
		getContentPane().setLayout(new GridLayout());
		getContentPane().add(verticalSplit);
		
		// Final command before returning
		pack();
	}
	
	/**
	 * Start the main GUI.
	 * Do this via the event queue to ensure that the action is taken within the correct thread.
	 */
	static public void create() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Gui().setVisible(true);
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
