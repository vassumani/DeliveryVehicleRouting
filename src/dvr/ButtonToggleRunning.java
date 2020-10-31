package dvr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * A JButton used to pause and resume the solver.
 */
@SuppressWarnings("serial")
public class ButtonToggleRunning extends JButton implements ActionListener {
	private final SolverThread solver;

	static private final String labelPause = "Pause Solver";
	static private final String labelUnpause = "Unpause Solver";
	
	/**
	 * Button constructor.
	 * @param s The solver which contains the data to be saved.
	 */
	public ButtonToggleRunning(SolverThread s) {
		super(labelUnpause);
		solver = s;
		addActionListener(this);
	}

	/**
	 * The action performed when the button is clicked.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (this == e.getSource()) {
			if (solver.isPaused()) {
				solver.unpause();
				setText(labelPause);
			} else {
				solver.pause();
				setText(labelUnpause);
			}
		}
	}
}
