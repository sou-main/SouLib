package soulib.windowLib.CanvasComponents;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JFrame;

import soulib.windowLib.CommonGraphics;
import soulib.windowLib.WindowMode;

@Deprecated
public class CanvasJFrame extends CanvasComponent{
	private JFrame frame;
	private long time;
	private int fps=20;
	private boolean end;
	@Deprecated
	public CanvasJFrame() {
		this(new WindowMode(400,400));
	}
	@Deprecated
	public CanvasJFrame(WindowMode wm) {
		frame=new JFrame();
		frame.setUndecorated(true);
		if(autoSize)frame.pack();
		else frame.setSize(wm.WindowX,wm.WindowY);
		new Thread() {
			@Override
			public void run(){
				while(!end) {
					if(fps<=0)fps=30;
					if(System.currentTimeMillis()-time>10000-fps*100)setVisible(false);//System.out.println("Close");;
					else setVisible(true);
					try{
						Thread.sleep(200);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
				}
				frame.dispose();
			}
		}.start();
	}
	private void setVisible(boolean b) {
		if(!frame.isVisible()&&b)frame.setVisible(true);
		if(frame.isVisible()&&!b)frame.setVisible(false);
	}
	@Override
	protected void finalize() {
		end=true;
	}
	public void add(Component comp) {
		frame.add(comp);
		if(autoSize)frame.pack();
		else frame.setSize(w,h);
	}
	@Override
	public void draw(CommonGraphics g,Point p){
		//fps=(int)g.getWindow().getFps().getFrameRate();
		time=System.currentTimeMillis();
	}
	@Override
	public void setX(int x){
		super.setX(x);
		if(autoSize)frame.pack();
		else frame.setLocation(x,getY());
	}
	@Override
	public void setY(int y) {
		super.setY(y);
		if(autoSize)frame.pack();
		else frame.setLocation(getX(),y);
	}
	@Override
	public void setW(int w) {
		super.setW(w);
		if(autoSize)frame.pack();
		else frame.setSize(w,getH());
	}
	@Override
	public void setH(int h) {
		super.setH(h);
		if(autoSize)frame.pack();
		else frame.setSize(getW(),getH());
	}
	@Override
	public int getW() {
		return frame.getWidth();
	}
	@Override
	public int getH() {
		return frame.getHeight();
	}
}
