import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

public class MouseEvents extends MouseInputAdapter {
		// Complement "has pink disk" property
		public void mouseClicked(MouseEvent e) {
			Object ob= e.getSource();
			if (ob instanceof Square) {
				((Square)ob).complementDisk();
			}
		}
	}
