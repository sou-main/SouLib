package soulib.windowLib.CanvasComponents;

import java.awt.event.ActionListener;

public class CClickMenu extends CPanel{

	public void addMenu(String name,ActionListener e) {
		CButton cc=new CButton(name);
		cc.addActionListener(e);
		addCanvasComponent(cc,0,cc.getH());
	}
}
