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
public class ButtonSaveRoute extends JButton implements ActionListener {
	private final SolverThread solver;

	/**
	 * Button constructor.
	 * @param s The solver which contains the data to be saved.
	 */
	public ButtonSaveRoute(SolverThread s) {
		super("Save Route");
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
				FileNameExtensionFilter ffCSV = new FileNameExtensionFilter("CSV Files", "csv");
				FileNameExtensionFilter ffTXT = new FileNameExtensionFilter("Text Files", "txt");
				
				// Create and open file chooser dialog window
				JFileChooser fc = new JFileChooser();
				fc.addChoosableFileFilter(ffCSV);
				fc.addChoosableFileFilter(ffTXT);
				fc.setFileFilter(ffTXT);
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
						if (fc.getFileFilter() == ffCSV) {
							file = new File(file.toString() + ".csv");
						} else if (fc.getFileFilter() == ffTXT) {
							file = new File(file.toString() + ".txt");
						}
					}

					// Check the type of file being generated
					boolean makeCSVFile = (fc.getFileFilter() == ffCSV) || file.getName().toLowerCase().endsWith(".csv");

					// Make sure the file can be written too
					if (!file.exists() || (file.isFile() && file.canWrite())) {
						System.out.println("Save route file: "+file.getName());
						
						// Open file writer
						fileWriter = new FileWriter(file);
						bufferedWriter = new BufferedWriter(fileWriter);
						
						// Write data to file
						Route route = solver.getBestRoute();
						int length = route.size();
						if (makeCSVFile) {
							bufferedWriter.write("\"Distance\",\"Path\"");
							bufferedWriter.newLine();
							if (length > 0) {
								bufferedWriter.write(Long.toString(route.travelDistance()) + "," + Integer.toString(route.getLocationIndex(0)));
								for (int i=1; i<length; i++) {
									bufferedWriter.write("," + Integer.toString(route.getLocationIndex(i)));
								}
							}
						} else {
							bufferedWriter.write("Distance " + Long.toString(route.travelDistance()) + ", Path: ");
							if (length > 0) {
								bufferedWriter.write(Integer.toString(route.getLocationIndex(0)));
								for (int i=1; i<length; i++) {
									bufferedWriter.write(" -> " + Integer.toString(route.getLocationIndex(i)));
								}
							}
						}
					}
				}
			} catch (Exception err) {
				JOptionPane.showMessageDialog(this, "Failed to save route data to file", "Error", JOptionPane.ERROR_MESSAGE);
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
