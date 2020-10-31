package DeliveryVehicleRouting;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * A JButton used to generate a random list of locations.
 */
@SuppressWarnings("serial")
public class ButtonRandomLocations extends JButton implements ActionListener {
	private final JFrame parentFrame;
	private final SolverThread solver;

	/**
	 * Button constructor.
	 * @param parent The parent JFrame which should be repainted after locations are generated.
	 * @param s The solver which contains the data to be updated.
	 */
	public ButtonRandomLocations(JFrame parent, SolverThread s) {
		super("Random Locations");
		parentFrame = parent;
		solver = s;
		addActionListener(this);
	}

	/**
	 * The action performed when the button is clicked.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (this == e.getSource()) {
			try {
				
				// Show input dialog
				String input = JOptionPane.showInputDialog(this, "Enter number of locations to generate");
				if ((input != null) && !input.isBlank()) {
	
					// Get value entered
					final int countMin = 3;
					int count = Integer.parseInt(input);
					if (count < countMin) {
						JOptionPane.showMessageDialog(this, "Value must be at least " + countMin, "Error", JOptionPane.ERROR_MESSAGE);
					} else {
	
						// Generate a list of random locations
						// Give the list to the solver
						solver.setDistanceMatrix(new DistanceMatrix(Location.RandomList(count, 100)));
						parentFrame.repaint();
					}
				}
			} catch (NumberFormatException err) {
				JOptionPane.showMessageDialog(this, "Invalid decimal number", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (Exception err) {
				JOptionPane.showMessageDialog(this, err.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
