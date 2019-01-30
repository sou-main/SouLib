package soulib.windowLib.CanvasComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import soulib.windowLib.CommonGraphics;
import soulib.windowLib.WindowBase;
import soulib.windowLib.WindowLib;

public class CButton extends CanvasComponent{
	public String name="";
	public int fs=13;
	public Color FrameColor=Color.gray;
	public boolean draw3d=true;
	public boolean click=false;
	public boolean newthread=true;
	public boolean ue;
	public static Color[] defaultColor= new Color[]{Color.red,Color.cyan};
	private static ExecutorService pool=Executors.newCachedThreadPool();
	public boolean setCursor=true;

	public CButton(String s){
		name=s;
		setFont(WindowLib.getPLAIN_Font(15));
		setColor(defaultColor[0]);
		setBackground(defaultColor[1]);
	}
	public CButton(){
		this("Button");
	}
	public void setFrameColor(Color c){
		FrameColor=c;
	}
	public Color getFrameColor(){
		return FrameColor;
	}
	@Override
	public int getH(){
		if(h>0&&!autoSize) return h;
		return fs+5;
	}
	@Override
	public int getW(){
		if(w>0&&!autoSize) return w;
		return WindowLib.getPaintWidth(getFont(),name)+15;
	}
	@Override
	public void setFont(Font font){
		super.setFont(font);
		fs=WindowLib.getPaintHeight(font);
	}
	public void update(){

	}
	/**
	 * @param e 現在のカーソルの位置
	 */
	@Override
	public void draw(CommonGraphics g,Point e){
		if(!active)return;
		update();
		if(getX()+getW()<0||getX()>g.getWidth()) return;
		if(getY()+getH()<0||getY()>g.getHeight()) return;
		g.setColor(getBackground());
		if(draw3d) g.fill3DRect(getX(),getY(),getW(),getH(),true);
		else g.fillRect(getX(),getY(),getW(),getH());
		if(e==null) e=new Point();
		if(isInside(e)) {
			if(setCursor) {
				WindowBase win0=g.getWindow();
				if(win0 instanceof Frame) {
					((Frame)win0).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			}
			if(click||ue){
				drawPressed(g);
			}
		}
		g.setColor(getFrameColor());
		g.drawRect(getX(),getY(),getW(),getH());
		g.setColor(getColor());
		drawText(g);
	}
	protected void drawPressed(CommonGraphics g){
		if(draw3d){
			g.setColor(getBackground());
			g.fill3DRect(getX(),getY(),getW(),getH(),false);
		}else{
			g.setColor(getBackground().darker());
			g.fillRect(getX(),getY(),getW(),getH());
		}
	}
	protected void drawText(CommonGraphics g){
		g.setFont(getFont());
		setW(g.drawString(name,getX()+5,getY()+fs)+10);
	}
	@Override
	public void mousePressed(MouseEvent e){
		if(this.isInside(e.getPoint())){
			click=true;
		}
	}
	@Override
	public void mouseReleased(MouseEvent e){
		click=false;
	}
	@Override
	public void mouseClicked(final MouseEvent e){
		if(!active)return;
		if(isInside(e.getPoint())){
			final ActionEvent e0=new ActionEvent(e,MouseEvent.MOUSE_CLICKED,"Click",e.getWhen(),e.getModifiers());
			if(newthread){
				pool.execute(new Runnable() {
					@Override
					public void run(){
						click(e);
						actionPerformed(e0);
					}
				});
			}else{
				try{
					click(e);
					actionPerformed(e0);
				}catch(Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	/**MouseEvent e を引数に取る方が色々できる*/
	protected void click(){

	}
	protected void click(MouseEvent e){
		click();
	}
	@Override
	public String toString() {
		return name;
	}
}