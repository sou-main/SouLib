package soulib.windowLib.CanvasComponents;

import java.util.ArrayList;

public class ToggleGrup{
	public ArrayList<Toggle> list=new ArrayList<Toggle>();
	public void click(Toggle tg) {
		for(int i=0;i<list.size();i++) {
			list.get(i).setToggle(false);
		}
		tg.setToggle(true);
	}
}
