package soulib.windowLib.CanvasComponents;

import java.awt.Color;
import java.awt.Point;

import soulib.windowLib.CommonGraphics;

public class CProgresBar extends CanvasComponent{
	/**ピクセル単位*/
	private int bar;
	/**パーセント単位*/
	private float bp;
	public CProgresBar() {
		setW(50);
		setH(10);
		setColor(Color.cyan);
	}
	@Override
	public void draw(CommonGraphics g,Point p){
		g.setColor(getColor());
		g.fillRect(getX(),getY(),bar,getH());
	}
	public void setW(int w) {
		super.setW(w);
		bar=(int) (bp/100*getW());
	}
	public void setBar(float b) {
		bar=(int) (b/100*getW());
		bp=b;
	}
	public float getBar() {
		return bp;
	}
}
