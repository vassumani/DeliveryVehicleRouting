import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A JButton used to save the list of locations.
 */
@SuppressWarnings("serial")
public class ButtonSaveLocations extends JButton implements ActionListener {
	private final SolverThread solver;

	/**
	 * Button constructor.
	 * @param s The solver which contains the data to be saved.
	 */
	public ButtonSaveLocations(SolverThread s) {
		super("Save Locations");
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
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter = null;
			
			// Enter try-catch block
			try {
			
				// Get the previous working directory
				Config config = new Config();
				File directory = config.getWorkingDirectory();
				
				// Create a file filter
				FileNameExtensionFilter ff = new FileNameExtensionFilter("CSV Files", "csv");
				
				// Create and open file chooser dialog window
				JFileChooser fc = new JFileChooser();
				fc.addChoosableFileFilter(ff);
				fc.setFileFilter(ff);
				fc.setCurrentDirectory(directory);
				int result = fc.showSaveDialog(this.getTopLevelAncestor());
				
				// Record current working directory
				config.setWorkingDirectory(fc.getCurrentDirectory());
				config.save();
				
				// Check if a file was selected
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();

					// Make sure the file has an extension type
					if (!file.getName().contains(".")) {
						file = new File(file.toString() + ".csv");
					}

					// Make sure the file can be written too
					if (!file.exists() || (file.isFile() && file.canWrite())) {
						System.out.println("Save location file: "+file.getName());
						
						// Open file writer
						fileWriter = new FileWriter(file);
						bufferedWriter = new BufferedWriter(fileWriter);
						
						// Write data to file
						DistanceMatrix dist = solver.getDistanceMatrix();
						int iMax = dist.size();
						for (int i=0; i<iMax; i++) {
							Location l = dist.getLocation(i);
							bufferedWriter.write(l.coord.x + ", " + l.coord.y + ", \"" + l.name + "\"");
						}
					}
				}
			} catch (Exception err) {
				JOptionPane.showMessageDialog(this, "Failed to save location data to file", "Error", JOptionPane.ERROR_MESSAGE);
			} finally {

				// Close file reader
				if (bufferedWriter != null) {
					try {bufferedWriter.close();} catch (IOException err) {}
				}
				if (fileWriter != null) {
					try {fileWriter.close();} catch (IOException err) {}
				}
			}
		}
	}
}
