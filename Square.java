import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;


public class Square extends JPanel{

	public static final int HEIGHT= 100;
	public static final int WIDTH= 100;
	int x, y; // Panel is at (x, y)
	private boolean hasDisk= false;
	Color green = new Color(0,150,0);
	Color red = new Color(210,21,21);
	Color white = new Color(255,255,255);
	Color black = new Color(0,0,0);
	int circleColor = 0; //1 for white, -1 for black, 0 for nothing, 2 for small gray circle
	
	
	/** Const: square at (x, y). Red/green? Parity of x+y. */
	public Square(int x, int y) {
		this.x= x; this.y= y;
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
	} 
	
	public void complementDisk() {
		if (circleColor==0) { 
			hasDisk= false;
		} else {
			hasDisk = true;
		}
		repaint(); // Ask the system to repaint the square
	}
	
	/* paint this square using g. System calls
	paint whenever square has to be redrawn.*/
	public void paint(Graphics g) {
		Color c = black;
		g.drawRect(0,0,WIDTH,HEIGHT);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		g.setColor(green);
		g.fillRect(0, 0, WIDTH-1, HEIGHT-1);
		if (circleColor==1) {
			c = white;
			g.setColor(c);
			g.fillOval(13, 13, WIDTH-26, HEIGHT-26);
		} else if (circleColor==-1){
			c = black;
			g.setColor(c);
			g.fillOval(13, 13, WIDTH-26, HEIGHT-26);
		} else if (circleColor==2) {
			//smallCircle(g);
			c = new Color(100,100,100);
			g.setColor(c);
			g.fillOval(35, 35, WIDTH-70, HEIGHT-70);
		}
		if (hasDisk) {
			
		}
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, WIDTH-1,HEIGHT-1);
		
	}
	
	public void clearDisk() {
		hasDisk= false;
		// Ask system to
		// repaint square
		repaint();
	} 
	
	public void swapColor() {
		circleColor *= -1;
		//repaint();
	}
	
	public void smallCircle(Graphics g) {
		
	}
	
	public class MouseEvents extends MouseInputAdapter {
		// Complement "has pink disk" property
		public void mouseClicked(MouseEvent e) {
			Object ob= e.getSource();
			if (ob instanceof Square) {
				((Square)ob).complementDisk();
			}
		}
	}
	
}
