package dvr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * A JButton used to pause and resume the solver.
 */
@SuppressWarnings("serial")
public class ButtonToggleSolver extends JButton implements ActionListener {
	private final JFrame parentFrame;
	private final SolverThread solver;

	/**
	 * Button constructor.
	 * @param parent The parent JFrame which should be repainted after the solver is changed.
	 * @param s The solver which contains is to be updated.
	 */
	public ButtonToggleSolver(JFrame parent, SolverThread s) {
		parentFrame = parent;
		solver = s;
		addActionListener(this);
		setText(getSolverType());
	}

	/**
	 * The action performed when the button is clicked.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (this == e.getSource()) {
			switch (solver.getSolverType()) {
			case ACO:
				solver.setSolverType(SolverType.GA);
				break;
			case GA:
				solver.setSolverType(SolverType.ACO);
				break;
			}
			setText(getSolverType());
			parentFrame.repaint();
		}
	}
	
	private String getSolverType() {
		return "Solver: " + solver.getSolverType().toString();
	}
}
