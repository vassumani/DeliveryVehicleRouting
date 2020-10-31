package DeliveryVehicleRouting;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A JButton used to load a list of locations.
 */
@SuppressWarnings("serial")
public class ButtonLoadLocations extends JButton implements ActionListener {
	private final JFrame parentFrame;
	private final SolverThread solver;

	/**
	 * Button constructor.
	 * @param parent The parent JFrame which should be repainted after locations are loaded.
	 * @param s The solver which contains the data to be updated.
	 */
	public ButtonLoadLocations(JFrame parent, SolverThread s) {
		super("Load Locations");
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
			
			// Declare resource values
			FileReader fileReader = null;
			BufferedReader bufferedReader = null;
			
			// Enter try-catch block
			try {
			
				// Get the previous working directory
				Config config = new Config();
				File directory = config.getWorkingDirectory();
				
				// Create a file filter
				FileNameExtensionFilter ffCSV = new FileNameExtensionFilter("CSV Files", "csv");
				
				// Create and open file chooser dialog window
				JFileChooser fc = new JFileChooser();
				fc.addChoosableFileFilter(ffCSV);
				fc.setFileFilter(ffCSV);
				fc.setCurrentDirectory(directory);
				int result = fc.showOpenDialog(this.getTopLevelAncestor());
				
				// Record current working directory
				config.setWorkingDirectory(fc.getCurrentDirectory());
				config.save();
				
				// Check if a file was selected
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					
					// Make sure the file exists
					if (file.isFile() && file.canRead()) {
						System.out.println("Load location file: "+file.getName());
						
						// Create an array to hold generated locations
						ArrayList<Location> location = new ArrayList<Location>();
						
						// Open file reader
						fileReader = new FileReader(file);
						bufferedReader = new BufferedReader(fileReader);
						
						// Read each line from the file
						while (bufferedReader.ready()) {
							String line = bufferedReader.readLine();
							if (!line.isBlank()) {
								
								// Split the line into sections
								// Split where a comma is followed by an even number of quotes
								// This ensures that both (1, 2) and (1, 2, "some,name,with,commas") work as expected
								String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
								if (values.length < 2) throw new Exception();
								
								// Get location data from line
								int x = Integer.parseInt(values[0].trim());
								int y = Integer.parseInt(values[1].trim());
								String label =
									(values.length > 2) ?
									values[2].trim().replaceAll("^\"|\"$", "") : // Use given label, without end quotes
									Integer.toString(location.size()); // Use location index number for label

								// Create the new location and add it to the list
								Location l = new Location(x, y, label);
								System.out.println("  Location: "+l);
								location.add(l);
							}
						}
						
						// Update the solver
						DistanceMatrix dm = new DistanceMatrix(location.toArray(new Location[location.size()]));
						solver.setDistanceMatrix(dm);
						parentFrame.repaint();
					}
				}
			} catch (FileNotFoundException err) {
				JOptionPane.showMessageDialog(this, "File not found", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (Exception err) {
				JOptionPane.showMessageDialog(this, "File format incorrect", "Error", JOptionPane.ERROR_MESSAGE);
			} finally {

				// Close file reader
				if (bufferedReader != null) {
					try {bufferedReader.close();} catch (IOException err) {}
				}
				if (fileReader != null) {
					try {fileReader.close();} catch (IOException err) {}
				}
			}
		}
	}
}
