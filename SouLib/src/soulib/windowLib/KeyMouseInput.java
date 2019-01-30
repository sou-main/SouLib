package soulib.windowLib;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface KeyMouseInput{
	public void mouseClicked(MouseEvent e);
	public void keyReleased(KeyEvent e);
	public void keyPressed(KeyEvent e);
	public void mouseWheelMoved(MouseWheelEvent e);
	public void mouseReleased(MouseEvent e);
	public void mousePressed(MouseEvent e);
}
