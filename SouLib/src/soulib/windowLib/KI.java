package soulib.windowLib;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KI extends KeyAdapter{
	protected boolean keyshift;
	@Override
	public void keyPressed(KeyEvent e){
		int keycode=e.getKeyCode();
		if (keycode==KeyEvent.VK_ESCAPE){
			//System.exit(0);
		}
		if (keycode==KeyEvent.VK_SHIFT){
			keyshift=true;
		}
	}
	@Override
	public void keyReleased(KeyEvent e){
		int keycode=e.getKeyCode();
		if (keycode==KeyEvent.VK_SHIFT){
			keyshift=false;
		}
	}
	public boolean getkeyshift(){
		return keyshift;
	}
}
