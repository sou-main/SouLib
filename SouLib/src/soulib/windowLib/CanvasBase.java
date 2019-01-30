package soulib.windowLib;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import soulib.windowLib.CanvasComponents.CanvasComponent;

public abstract class CanvasBase extends Canvas implements Runnable{

	public boolean AutoUpdate=true;
	public MI Mouseinput;
	public KI Keyinput;
	public boolean DrawMode;
	public long sleep;
	public FrameRate fps=new FrameRate();
	public Image imgBuf;
	public Graphics gBuf;
	public Thread thread;
	protected CanvasWindow win;
	protected boolean End=false;
	protected boolean close;
	protected String name="Canvas";
	public boolean setCursor;

	public CanvasBase(int windowX, int windowY,boolean DrawMode,CanvasWindow win){
		setSize(windowX, windowY);
		this.DrawMode=DrawMode;
		this.win=win;
		End=false;
	}
	public void initThread(boolean notNewThread){
		if(notNewThread){
			thread=Thread.currentThread();
		}else{
			thread=new Thread(this,name);
			thread.start();
		}
	}
	public void del() {
		del(getBackground());
	}
	public abstract void del(Color color);
	public void repaintCan(){

	}
	public void End(){
		End=true;
		while(!close) {
			try{
				Thread.sleep(100);
			}catch(InterruptedException e){}
		}
	}
	public void ReSize(){

	}
	public void removeAllCC(){
		win.getCanvasComponentManager().removeAllCanvasComponent();
	}
	public void removeCC(CanvasComponent cc){
		win.getCanvasComponentManager().removeCanvasComponent(cc);
	}
	public void addCanvasComponent(CanvasComponent cc){
		win.getCanvasComponentManager().addCanvasComponent(cc);
	}
	public CanvasComponent[] getCanvasComponent(){
		return win.getCanvasComponentManager().getCanvasComponent();
	}
}
