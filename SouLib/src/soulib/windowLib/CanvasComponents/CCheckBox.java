package soulib.windowLib.CanvasComponents;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;

import soulib.windowLib.CommonGraphics;
import soulib.windowLib.WindowLib;

public class CCheckBox extends CanvasComponent implements Toggle{

	public int check=4;
	public int space=4;
	private boolean toggle;
	public String text;
	private ToggleGrup tg;
	public void setToggleGroup(ToggleGrup t) {
		tg=t;
	}
	/**
	 * 指定した文字を表示するチェックボックスを作成します。
	 *
	 * @param string 表示する文字
	 */
	public CCheckBox(String string){
		this.text=string;
		this.setBackground(Color.black);
		this.setColor(Color.black);
		this.setFont(getFont());
	}
	/** 何も文字を表示しないチェックボックスを作成します。 */
	public CCheckBox(){
		this("");
	}
	@Override
	public void draw(CommonGraphics g,Point p){
		g.setFont(getFont());
		g.setColor(getColor());
		g.drawString(text,getX()+box()+space*2,getY()+getH()-2);
		if(toggle){
			g.setColor(getColor());
			g.fillRect(getX()+space,getY()+space,box(),box());
		}
		g.setColor(getBackground());
		g.drawRect(getX(),getY(),getW(),getH());
		g.drawRect(getX()+space,getY()+space,box(),box());
	}
	@Override
	public void mouseClicked(MouseEvent e){
		if(isInside(e.getPoint())) {
			if(tg==null)editToggle();
			else tg.click(this);
		}
	}
	@Override
	public void editToggle(){
		this.setToggle(!this.toggle);
	}
	public int box(){
		return getH()-space*2;
	}
	@Override
	public boolean getToggle(){
		return this.toggle;
	}
	@Override
	public void setToggle(boolean toggle){
		this.toggle=toggle;
	}
	@Override
	public int getH(){
		if(h>0&&!autoSize) return h;
		return h=getFont().getSize()+4;
	}
	@Override
	public int getW(){
		if(w>0&&!autoSize) return w;
		return w=WindowLib.getPaintWidth(getFont(),text)+box()+space*2+2;
	}
	/** ここで指定した色は正方形の色になります。 */
	@Override
	public void setBackground(Color c){
		super.setBackground(c);
	}
}
