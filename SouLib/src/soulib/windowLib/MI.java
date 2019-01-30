package soulib.windowLib;

import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MI extends MouseAdapter{
	private int buf=0;
	protected boolean Keyshift=false;
	public int x;
	public int y;
	protected boolean pressed;
	public WindowBase win;
	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		if(Keyshift){
			buf+=e.getWheelRotation()*10;
		}else{
			buf+=e.getWheelRotation();
		}
	}
	public int getData(){
		return buf;
	}
	public void setShift(boolean getkeyshift) {
		Keyshift=getkeyshift;
	}
	@Override
	public void mouseMoved(MouseEvent e){
		x=e.getX();
		y=e.getY();
	}
	@Override
	public void mouseDragged(MouseEvent e){
		x=e.getX();
		y=e.getY();
	}
	public void mouseExited(MouseEvent e) {
		if(win==null) {
			x=-1;
			y=-1;
		}
		KeyboardFocusManager kfm=KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Window w=kfm.getActiveWindow();
		if(w!=win.getTooltip().frame) {
			x=-1;
			y=-1;
			win.getTooltip().hide();
		}
	}
	@Override
	public void mouseClicked(MouseEvent e){

	}
	public void setData(int i){
		buf=i;
	}
	@Override
	public void mousePressed(MouseEvent e) {
		pressed=true;
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		pressed=false;
	}
	public boolean getPressed() {
		return pressed;
	}
}
