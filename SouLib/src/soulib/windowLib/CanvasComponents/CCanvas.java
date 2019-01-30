package soulib.windowLib.CanvasComponents;

import java.awt.Point;

import soulib.windowLib.CommonGraphics;
import soulib.windowLib.Render;

/**
 * CPanel等で移動させたい場合は<br>
 * getX()やgetY()等を使って座標を指定してください
 */
public class CCanvas extends CanvasComponent{

	public Render paint;

	public CCanvas(){

	}
	public CCanvas(Render render){
		paint=render;
	}
	@Override
	public void draw(CommonGraphics g,Point p){
		Update(g);
	}
	public void Update(CommonGraphics g){
		if(paint!=null) paint.Update(g);
	}
	public void setRender(Render render){
		paint=render;
	}
}
