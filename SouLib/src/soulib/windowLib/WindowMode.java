package soulib.windowLib;

import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.event.WindowListener;

import soulib.lib.SystemState;

public class WindowMode{
	public static final WindowMode MaxSize = new WindowMode(){
		@Override
		public void init(){
		DisplayMode dm = SystemState.SS.DefaultGraphicsDevice.getDisplayMode();
		WindowX=dm.getWidth();
		WindowY=dm.getHeight();
		}
	};
	public int WindowX=100;
	public int WindowY=100;
	public boolean DrawMode=false;
	public boolean FullScreen=false;
	public String name="Window";
	public GraphicsConfiguration gc=null;
	public boolean NoCanvas=false;
	public GraphicsDevice Device=null;
	public boolean NoNewThread=false;
	public boolean AutoUpdate=true;
	public float depth=100;
	public boolean AutoReSize=false;
	public Render render=null;
	public boolean defaultHide=false;
	public Image icon=null;
	/**<pre>
//サンプル
new WindowAdapter(){
public void windowClosing(WindowEvent evt){
System.exit(0);
}
};
	</pre>*/
	public WindowListener WindowListener;
	public int monitorID;
	public WindowMode(int WindowX, int WindowY){
		this(WindowX, WindowY,false);
	}
	public WindowMode(int WindowX, int WindowY,boolean DrawMode){
		this(WindowX, WindowY,DrawMode,false);
	}
	public WindowMode(int WindowX, int WindowY,boolean DrawMode,boolean FullScreen){
		this(WindowX, WindowY, DrawMode, FullScreen,"Window");
	}
	public WindowMode(int WindowX, int WindowY,boolean DrawMode,boolean FullScreen, String name){
		this(WindowX, WindowY, DrawMode,FullScreen, name,null);
	}
	public WindowMode(int WindowX, int WindowY,boolean DrawMode,boolean FullScreen, String name,GraphicsConfiguration gc){
		this(WindowX, WindowY,DrawMode, FullScreen, name, gc,false);
	}
	public WindowMode(int WindowX, int WindowY,boolean DrawMode,boolean FullScreen, String name,GraphicsConfiguration gc,boolean NoCanvas){
		this(WindowX, WindowY,DrawMode, FullScreen, name, gc,NoCanvas,null);
	}
	public WindowMode(int WindowX, int WindowY,boolean DrawMode,boolean FullScreen, String name,GraphicsConfiguration gc,boolean NoCanvas, GraphicsDevice Device){
		this.WindowX=WindowX;
		this.WindowY=WindowY;
		this.DrawMode=DrawMode;
		this.FullScreen=FullScreen;
		this.name=name;
		this.gc=gc;
		this.NoCanvas=NoCanvas;
		this.Device=Device;
	}
	public WindowMode(){
		init();
	}
	public WindowMode(int width,int height,float depth){
		this(width, height);
		this.depth=depth;
	}
	/**コピー*/
	public WindowMode(WindowMode mode){
		WindowX=mode.WindowX;
		WindowY=mode.WindowY;
		DrawMode=mode.DrawMode;
		FullScreen=mode.FullScreen;
		name=mode.name;
		gc=mode.gc;
		NoCanvas=mode.NoCanvas;
		Device=mode.Device;
		NoNewThread=mode.NoNewThread;
		AutoUpdate=mode.AutoUpdate;
		depth=mode.depth;
	}
	public void init(){

	}
}