package control;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public abstract class LayoutAccesser {
	public abstract JScrollPane getRightPanel();
	public abstract void setRightPanel(JScrollPane rightPanel);
	public abstract JPanel getBottomPanel();
	public abstract void setBottomPanel(JPanel bottomPanel);
}
