import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * A JButton used to show or hide the solver workings.
 */
@SuppressWarnings("serial")
public class ButtonToggleWorkings extends JButton implements ActionListener {
	private final LocationRenderer renderer;

	static private final String labelShow = "Show Workings";
	static private final String labelHide = "Hide Workings";
	
	/**
	 * Button constructor.
	 * @param r The renderer which  which needs to be updated.
	 */
	public ButtonToggleWorkings(LocationRenderer r) {
		super(labelShow);
		renderer = r;
		addActionListener(this);
	}

	/**
	 * The action performed when the button is clicked.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (this == e.getSource()) {
			if (renderer.showWorking) {
				renderer.showWorking = false;
				setText(labelShow);
			} else {
				renderer.showWorking = true;
				setText(labelHide);
			}
		}
	}
}
