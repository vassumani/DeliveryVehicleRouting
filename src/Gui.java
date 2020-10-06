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
		LocationList l = new LocationList();
		Route r = new Route(l);
		topPanel = new LocationRenderer(l, r);
		
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
		verticalSplit.setDividerLocation(300);

		// Setup this JFrame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(400,400));
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
