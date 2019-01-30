package soulib.windowLib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

import soulib.windowLib.CanvasComponents.CanvasComponent;

public class OLDcan extends CanvasBase{
	public static final int NUM_BUFFERS=3;
	public static final Font font40=new Font("sansserif",Font.BOLD,40);
	private final boolean debugmode=false;
	public BufferStrategy abufferStrategy;
	private boolean FullScreen;
	public int seido=2;
	public int frame;
	public OLDcan(int windowX,int windowY,StandardWindow win,String name,boolean DrawMode,boolean FullScreen){
		super(windowX,windowY,DrawMode,win);
		this.name=name;
		this.FullScreen=FullScreen;
		if(FullScreen) this.DrawMode=true;
		setFocusable(true);
	}
	@Override
	public void run(){
		int ec=0;
		int ec2=0;
		try{
			try{
				win.init(new GraphicsAWT(gBuf));
			}catch(Throwable u){
				u.printStackTrace();
			}
			try{
				win.init();
			}catch(Throwable u){
				u.printStackTrace();
			}
			if(win.isClose()) return;
			imgBuf=createImage(getWidth(),getHeight());
			gBuf=imgBuf.getGraphics();
			if(DrawMode) gBuf=abufferStrategy.getDrawGraphics();
		}catch(java.lang.IllegalArgumentException i){
			if(getWidth()<1) setSize(1,getHeight());
			if(getHeight()<1) setSize(getWidth(),1);
			imgBuf=createImage(getWidth(),getHeight());
			gBuf=imgBuf.getGraphics();
			if(DrawMode) gBuf=abufferStrategy.getDrawGraphics();
			//i.printStackTrace();
		}catch(Throwable u){
			u.printStackTrace();
		}
		while(!End&&AutoUpdate){
			if(ec2>6){
				if(ec!=0) ec=0;
			}else ec2++;
			if(win.AutoReSize()) if(win.getWidth()!=getWidth()||win.getHeight()!=getHeight()){
				win.ReSize();
			}
			try{
				if(DrawMode){
					gBuf=abufferStrategy.getDrawGraphics();
					del();
				}del();//TODO 表示初期化
				try{
					win.Update(gBuf);
				}catch(Throwable u){
					u.printStackTrace();
				}
				cc();
				try{
					win.getWindowLog().draw(new GraphicsAWT(gBuf),win,-10*(win.getScroll()/seido));
				}catch(Throwable u){
					u.printStackTrace();
				}
				repaint();
				fps.count();
				sleep=fps.sleep();
				if(End) break;
			}catch(Throwable u){
				u.printStackTrace();
				if(ec>200) break;
			}
		}
		if(End) close=true;
	}
	private void cc(){
		Point p=new Point(Mouseinput.x,Mouseinput.y);
		GraphicsAWT g=new GraphicsAWT(gBuf);
		win.getCanvasComponentManager().draw(g,p);
	}
	@Override
	public void addCanvasComponent(CanvasComponent cc){
		win.getCanvasComponentManager().addCanvasComponent(cc);
	}
	@Override
	public void initThread(boolean notNewThread){
		if(FullScreen){
			DrawMode=true;
			win.createBufferStrategy(NUM_BUFFERS);
			abufferStrategy=win.getBufferStrategy();
		}else if(DrawMode){
			createBufferStrategy(NUM_BUFFERS);
			abufferStrategy=getBufferStrategy();
		}
		if(debugmode) System.out.println("DrawMode="+DrawMode);
		if(debugmode) System.out.println("abufferStrategy=null?"+(abufferStrategy!=null));
		if(notNewThread){
			thread=Thread.currentThread();
		}else{
			thread=new Thread(this,name);
			thread.start();
		}
	}
	@Override
	public void paint(Graphics g){
		try{
			if(DrawMode&&!abufferStrategy.contentsLost()){
				abufferStrategy.show();
				Toolkit.getDefaultToolkit().sync();
			}else{
				g.drawImage(imgBuf,0,0,this);
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
	@Override
	public void update(Graphics g){
		paint(g);
	}
	@Override
	public void repaint(){
		win.repaintCan();
	}
	@Override
	public void ReSize(){
		win.ReSize();
	}
	@Override
	public void del(Color color){
		if(gBuf!=null){
			gBuf.setColor(color);
			gBuf.fillRect(0,0,getWidth(),getHeight());
		}
	}
	@Override
	public void repaintCan(){
		super.repaint();
	}
	public synchronized void mouseClicked(MouseEvent e){
		win.getCanvasComponentManager().mouseClicked(e);
	}
	public synchronized void mousePressed(MouseEvent e){
		win.getCanvasComponentManager().mousePressed(e);
	}
	public synchronized void mouseReleased(MouseEvent e){
		win.getCanvasComponentManager().mouseReleased(e);
	}
	@Override
	public synchronized void removeAllCC(){
		win.getCanvasComponentManager().removeAllCanvasComponent();
	}
	@Override
	public synchronized void removeCC(CanvasComponent cc){
		win.getCanvasComponentManager().removeCanvasComponent(cc);
	}
}