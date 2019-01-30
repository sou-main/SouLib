package soulib.windowLib.CanvasComponents;

import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tootip{
	public JFrame frame;
	private JLabel label;
	public Tootip() {
		frame=new JFrame("");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setVisible(true);
		frame.add(label=new JLabel());
	}
	public void show(Point p,String text) {
		frame.setLocation(p.x,p.y);
		label.setText(text);
		setVisible(true);
		frame.pack();
	}
	public void hide() {
		setVisible(false);
	}
	public void setVisible(boolean b) {
		frame.isVisible();
	}
	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}
}
