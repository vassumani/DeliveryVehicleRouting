package DeliveryVehicleRouting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.swing.JFileChooser;

/**
 * Contains methods to get and set configuration values.
 */
public class Config {
	static public final String keyWorkingDirectory = "working-directory";
	
	/**
	 * Get the current working directory.
	 * This will be used with file selectors.
	 * @return Current working directory.
	 */
	public File getWorkingDirectory() {
		String dir = prop.getProperty(keyWorkingDirectory);
		File out = null;
		if (dir != null) {
			out = new File(dir);
			if (!out.isDirectory()) out = null;
		}
		return (out != null) ? out : (new JFileChooser()).getCurrentDirectory();
	}
	
	/**
	 * Set the current working directory.
	 * @param newValue Current working directory.
	 */
	public void setWorkingDirectory(File newValue) {
		String path = newValue.getAbsolutePath();
		Object old = prop.setProperty(keyWorkingDirectory, path);
		updated |= (old == null) || (old.toString() != path);
	}
	
	/**
	 * Default constructor.
	 */
	public Config() {
		prop = new Properties();
		updated = false;
		try {
			FileInputStream in = new FileInputStream(getConfigFilePath());
			prop.load(in);
		} catch (Exception e) {
		}
	}

	/**
	 * Save the current configuration to file.
	 */
	public void save() {
		if (updated) {
			try {
				FileOutputStream out = new FileOutputStream(getConfigFilePath());
				prop.store(out, "insert-comments-here");
				updated = false;
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Called by garbage collector when it garbage collects this object.
	 */
	public void finalize() {
		save();
	}
	
	/**
	 * Get the location of the configuration file.
	 * @return Path to configuration file.
	 */
	static public String getConfigFilePath() {
		
		// Get location of ClassLoader
		// This should be the location of the jar file
		File info = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
		String directory = info.getAbsolutePath();
		
		// Make sure there is a file separator (slash) on the end of the directory string
		if (!directory.endsWith(File.separator)) {
			directory += File.separator;
		}
		
		// Return location of the configuration file
		return directory + "config.properties";
	}
	
	final private Properties prop;
	private boolean updated;
}
