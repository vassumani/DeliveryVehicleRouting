import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * A JButton used to load a list of locations.
 */
@SuppressWarnings("serial")
public class ButtonLoadLocations extends JButton implements ActionListener {
	private final Gui gui;
	private final SolverThread solver;
	
	/**
	 * Button constructor.
	 * @param label Button label.
	 * @param gui Parent manager.
	 */
	public ButtonLoadLocations(Gui g, SolverThread s) {
		super("Load Locations");
		gui = g;
		solver = s;
		addActionListener(this);
	}
	
	/**
	 * The action performed when the button is clicked.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (this == e.getSource()) {
			
				// Create and open file selector dialog window
				JFileChooser fc = new JFileChooser();
				int result = fc.showOpenDialog(this.getTopLevelAncestor());
				
				// Check if opening file
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.isFile() && file.canRead()) {
						
						// Read file
						Scanner reader = new Scanner(file);
						ArrayList<Location> location = new ArrayList<Location>();
						int count = 0;
						while (reader.hasNext()) {
							location.add(new Location(reader.nextInt(), reader.nextInt(), Integer.toString(count++)));
						}
						reader.close();
						
						// Update the solver
						DistanceMatrix dm = new DistanceMatrix(location.toArray(new Location[location.size()]));
						solver.setDistanceMatrix(dm);
						gui.repaint();
					}
				}
			}
		} catch (FileNotFoundException err) {
			JOptionPane.showMessageDialog(this, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception err) {
			JOptionPane.showMessageDialog(this, "File format incorrect", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
