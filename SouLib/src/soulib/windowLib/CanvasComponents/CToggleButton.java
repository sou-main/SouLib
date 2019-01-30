package soulib.windowLib.CanvasComponents;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseEvent;

import soulib.windowLib.CommonGraphics;
import soulib.windowLib.WindowBase;

public class CToggleButton extends CButton implements Toggle{
	public CToggleButton(String name){
		super(name);
	}
	public CToggleButton(){
		super();
	}
	public boolean toggle=false;
	@Override
	public void click(MouseEvent e){
		toggle=!toggle;
		super.click(e);
	}
	@Override
	public void draw(CommonGraphics g,Point e){
		update();
		setW(getW());
		setH(getH());
		if(getX()+getW()<0||getX()>g.getWidth()) return;
		if(getY()+getH()<0||getY()>g.getHeight()) return;
		g.setColor(getBackground());
		if(draw3d)g.fill3DRect(getX(),getY(),w,h,true);
		else g.fillRect(getX(),getY(),w,h);
		if(e==null)e=new Point();
		if(isInside(e)){
			WindowBase win0=g.getWindow();
			if(win0 instanceof Frame)((Frame)win0).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			if(draw3d) {
				g.setColor(getBackground());
				g.fill3DRect(getX(),getY(),w,h,!click||toggle);
			}else if(click){
				g.setColor(getBackground().darker());
				g.fillRect(getX(),getY(),w,h);
			}
		}if(toggle) {
			g.setColor(getBackground());
			if(draw3d)g.fill3DRect(getX(),getY(),w,h,false);
			else g.fillRect(getX(),getY(),w,h);
		}
		g.setColor(FrameColor);
		g.drawRect(getX(),getY(),w,h);
		g.setColor(getColor());
		g.setFont(getFont());
		setW(g.drawString(name, getX()+5, getY()+fs)+10);
	}
	@Override
	public boolean getToggle(){
		return toggle;
	}
	@Override
	public void setToggle(boolean b){
		toggle=b;
	}
	@Override
	public void editToggle(){
		toggle=!toggle;
	}
}
