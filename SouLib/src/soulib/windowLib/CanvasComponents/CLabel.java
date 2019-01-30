package soulib.windowLib.CanvasComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;

import soulib.windowLib.CommonGraphics;
import soulib.windowLib.Tooltip;
import soulib.windowLib.WindowLib;

public class CLabel extends CanvasComponent{

	private String text="";
	private String tooltip=null;
	private int count;

	/**何も表示しない*/
	public CLabel() {
		this("");
	}
	public CLabel(String text) {
		this.text =text;
		this.setBackground(new Color(0,0,0,0));
		this.setColor(Color.black);
	}
	public void setTooltip(String s) {
		tooltip=s;
	}
	@Override
	public int getH(){
		if(h>0&&!autoSize)return h;
		if(getText().isEmpty())return 0;
		return h=WindowLib.getPaintHeight(getFont());
	}
	@Override
	public int getW() {
		if(w>0&&!autoSize)return w;
		return w=WindowLib.getPaintWidth(getFont(),getText());
	}
	public static int getH(int fs){
		return fs+5;
	}
	@Override
	public void draw(CommonGraphics g,Point e){
		if(getBackground().getAlpha()>0) {
			g.setColor(getBackground());
			g.fillRect(getX(),getY(),getW(),getH());
		}
		g.setColor(getColor());
		g.setFont(getFont());
		g.drawString(getText(),getX(),getY()+getH()-2);
		if(tooltip!=null) {
			win=g.getWindow();
			if(win instanceof Window) {
				Window win0=(Window) win;
				Tooltip tt=win.getTooltip();
				if(isInside(e)) {
					if(count>1*win.getFps().FPS) {
						tt.show(e.x+win0.getX(),e.y+win0.getY()+45,tooltip);
					}else count++;
				}else{
					count=0;
					tt.hide();
				}
			}
		}
	}
	public String getText(){
		return text;
	}
	@Override
	public void setFont(Font font) {
		super.setFont(font);
	}
	public void setText(String string){
		text=string;
	}
}
