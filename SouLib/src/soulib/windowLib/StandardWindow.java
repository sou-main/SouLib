package soulib.windowLib;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import soulib.fileLib.FileEditor;
import soulib.logLib.GameLog;
import soulib.logLib.WindowLog;
import soulib.windowLib.CanvasComponents.CanvasComponent;

public class StandardWindow extends Frame implements CanvasWindow,KeyMouseInput{

	protected CanvasBase can;
	private String name="Window";
	private boolean Close;
	private GraphicsConfiguration gc;
	private boolean FullScreen;
	public boolean AutoReSize;
	public WindowLog windowlog;
	public boolean nnt;
	private CanvasComponentManager ccm;
	public Render render=null;
	private KI CCkeyInput;
	private MI CCmouseInput;
	private Tooltip tooltip=new Tooltip(this);
	public StandardWindow(WindowMode mode){
		super(mode!=null ? mode.gc : null);
		Close=false;
		ccm=new CanvasComponentManager();
		if(mode==null) mode=new WindowMode();
		this.gc=mode.gc;
		if(mode.WindowX<1) mode.WindowX=1;
		if(mode.WindowY<1) mode.WindowY=1;
		this.name=mode.name;
		this.render=mode.render;
		if(mode.WindowListener!=null) addWindowListener(mode.WindowListener);
		else addWindowListener(setWindowListener());
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}catch(InstantiationException e){
			e.printStackTrace();
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch(UnsupportedLookAndFeelException e){
			e.printStackTrace();
		}
		this.AutoReSize=mode.AutoReSize;
		setSize(mode.WindowX,mode.WindowY);
		setSize(getWidth()+getInsets().left+getInsets().right,getHeight()+getInsets().top+getInsets().bottom);
		if(mode.FullScreen) if(mode.Device!=null) initFullScreen(mode.Device);
		else initFullScreen(mode.monitorID);
		else can=newCan(mode);
		can.AutoUpdate=mode.AutoUpdate;
		add(can,BorderLayout.CENTER);
		setVisible(!mode.defaultHide);
		can.initThread(mode.NoNewThread);
		CCmouseInput=new ccMouse();
		CCkeyInput=new ccKeyInput();
		setMouseInput(CCmouseInput);
		setKeyInput(CCkeyInput);
		if(mode.NoCanvas){
			((Canvas) can).setSize(1,1);
			can.close=true;
			pack();
		}
		setCenter();
		if(mode.icon!=null)setIconImage(mode.icon);
		setVisible(!mode.defaultHide);
		toFront();
		nnt=mode.NoNewThread;
		if(mode.NoNewThread) can.run();
	}
	public void setCursor(Cursor c) {
		super.setCursor(c);
		can.setCursor=true;
	}
	protected CanvasBase newCan(WindowMode mode){
		return new can(mode.WindowX,mode.WindowY,this,getName(),mode.DrawMode,mode.FullScreen);
	}
	public Cursor getFullScreenCursor(){
		return Toolkit.getDefaultToolkit().createCustomCursor(
				new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB),
				new Point(),"");
	}
	public StandardWindow(int WindowX,int WindowY){
		this(WindowX,WindowY,false);
	}
	public StandardWindow(int WindowX,int WindowY,boolean DrawMode){
		this(WindowX,WindowY,DrawMode,false);
	}
	public StandardWindow(int WindowX,int WindowY,boolean DrawMode,boolean FullScreen){
		this(WindowX,WindowY,DrawMode,FullScreen,"Window");
	}
	public StandardWindow(int WindowX,int WindowY,boolean DrawMode,boolean FullScreen,String name){
		this(WindowX,WindowY,DrawMode,FullScreen,name,null);
	}
	public StandardWindow(int WindowX,int WindowY,boolean DrawMode,boolean FullScreen,String name,
			GraphicsConfiguration gc){
		this(new WindowMode(WindowX,WindowY,DrawMode,FullScreen,name,gc,false));
	}
	@Override
	public WindowMode getWindowMode(){
		WindowMode wm=new WindowMode(getWidth(),getHeight(),can.DrawMode,FullScreen,name,gc,
				can.getWidth()==1&&can.getHeight()==1);
		wm.NoNewThread=nnt;
		wm.AutoUpdate=can.AutoUpdate;
		return wm;
	}
	public void addData(Throwable t){
		String[] X=FileEditor.FE.StringToStrings(t.getMessage());
		for(int i=0;i<X.length;i++){
			if(X[i]!=null) addData(X[i]);
		}
	}
	public StandardWindow addData(String[] Data){
		for(int i=0;i<Data.length;i++){
			addData(Data[i]);
		}
		return this;
	}
	public StandardWindow addData0(String Data){
		if(Data.indexOf(System.getProperty("line.separator"))!=-1){
			addData(FileEditor.FE.StringToStrings(Data));
		}
		return this;
	}
	@Override
	public WindowBase addData(String Data){
		getWindowLog().addData(Data);
		//System.out.println("addData");
		return this;
	}
	@Override
	public void del(){
		can.del();
	}
	@Override
	public void del(Color color){
		can.del(color);
	}
	@Override
	public WindowLog getWindowLog(){
		if(windowlog==null) windowlog=new WindowLog(this);
		return windowlog;
	}
	@Override
	public String[] getDataList(){
		return getWindowLog().getDataListString();
	}
	@Override
	public void Update(Graphics g){
		Update((Graphics2D) g);
	}
	@Override
	public void Update(Graphics2D g){
		Update(new GraphicsAWT(g).setWindow(this));
	}
	@Override
	public void repaintCan(){
		can.repaintCan();
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	public void init(){
		set();
		if(render!=null) render.init(this);
	}
	@Override
	public long getSleep(){
		return can.sleep;
	}
	@Override
	public WindowListener setWindowListener(){
		return new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt){
				System.exit(0);
			}
		};
	}
	@Override
	public void setKeyInput(KI input){
		if(can.Keyinput==input) return;
		can.Keyinput=input;
		addKeyListener(input);
		can.addKeyListener(input);
	}
	@Override
	public void setMouseInput(MI input){
		if(can.Mouseinput==input) return;
		input.win=this;
		can.Mouseinput=input;
		addMouseWheelListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		can.addMouseWheelListener(input);
		can.addMouseListener(input);
		can.addMouseMotionListener(input);
	}
	@Override
	public String getTitle(){
		return getName();
	}
	/**
	 * このウィンドウを閉じる
	 */
	@Override
	public void close(){
		Close=true;
		can.End();
		tooltip.close();
		dispose();
		System.gc();
	}
	public boolean getFullScreenLog(){
		return true;
	}
	private void initFullScreen(int id){
		GraphicsEnvironment ge=GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		initFullScreen(ge.getScreenDevices()[id]);
	}
	private void initFullScreen(GraphicsDevice device){
		try{
			initFullScreen(this,device,getFullScreenLog() ? GameLog.StaticLog : null);
			FullScreen=true;
		}catch(Exception e){
			NotFullScreenSupported(device);
		}
		if(getFullScreenCursor()!=null) setCursor(getFullScreenCursor());
	}
	public static void initFullScreen(Frame win,GraphicsDevice device,GameLog log) throws Exception{
		GraphicsEnvironment ge=GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		if(device==null) device=ge.getDefaultScreenDevice();
		win.setUndecorated(true);
		if(!device.isFullScreenSupported()){
			GameLog.Log("NotFullScreenSupported");
			throw new Exception();
		}
		device.setFullScreenWindow(win);
		DisplayMode[] modes=device.getDisplayModes();
		int ModeID=0;
		double buf=10000;
		for(int i=0;i<modes.length;i++){
			if(log!=null) log.log("("+modes[i].getWidth()+","
					+modes[i].getHeight()+","+modes[i].getBitDepth()+","
					+modes[i].getRefreshRate()+") ");
			double Xdiff=Math.abs(win.getWidth()-modes[i].getWidth());
			double Ydiff=Math.abs(win.getHeight()-modes[i].getHeight());
			double buf1=Math.sqrt(Math.pow(Xdiff,2)+Math.pow(Ydiff,2));
			if(buf1<=buf){
				buf=buf1;
				ModeID=i;
			}
		}
		win.setSize(modes[ModeID].getWidth(),modes[ModeID].getHeight());
		DisplayMode dm=new DisplayMode(modes[ModeID].getWidth(),modes[ModeID].getHeight(),
				modes[ModeID].getBitDepth(),DisplayMode.REFRESH_RATE_UNKNOWN);
		device.setDisplayMode(dm);
		GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().setFullScreenWindow(win);
	}
	public void NotFullScreenSupported(GraphicsDevice device){
		System.exit(0);
	}
	@Override
	public void setCenter(){
		setCenter(this);
	}
	public static void setCenter(Window w){
		w.setBounds(getDisplayMode().getWidth()/2-w.getWidth()/2,getDisplayMode().getHeight()/2-w.getHeight()/2,
				w.getWidth(),w.getHeight());
	}
	public static DisplayMode getDisplayMode(){
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device=ge.getDefaultScreenDevice();
		return device.getDisplayMode();
	}
	/**
	 * GroupLayoutを作成する。<br>
	 *
	 * @param makePanel
	 *            GroupLayoutを適用するパネル。<br>
	 * @param components
	 *            コンポーネントの2次元配列。この配列の通りの配置になる。
	 * @return 作成したグループレイアウト
	 */
	public static GroupLayout makeGroupLayout(Container makePanel,Component[][] components){
		GroupLayout gl=new GroupLayout(makePanel);
		// 水平方向のグループ
		SequentialGroup hGroup=gl.createSequentialGroup();
		for(int i=0;i<components[0].length;i++){
			ParallelGroup pg=gl.createParallelGroup();
			for(int j=0;j<components.length;j++){
				if(components[j][i]==null) components[j][i]=new Label("");
				try{
					pg=pg.addComponent(components[j][i]);
				}catch(ArrayIndexOutOfBoundsException oob){
					//components[j][i]=new Component(){};
					//pg = pg.addComponent(new Canvas());//components[j][i]);
				}
			}
			hGroup.addGroup(pg);
		}
		gl.setHorizontalGroup(hGroup);

		// 垂直方向のグループ
		SequentialGroup vGroup=gl.createSequentialGroup();
		for(int i=0;i<components.length;i++){
			ParallelGroup pg=gl.createParallelGroup(Alignment.BASELINE);
			for(int j=0;j<components[0].length;j++){
				try{
					pg=pg.addComponent(components[i][j]);
				}catch(ArrayIndexOutOfBoundsException oob){
					//components[j][i]=new Component(){};
					//pg = pg.addComponent(new Canvas());//components[i][j]);
				}
			}
			vGroup.addGroup(pg);
		}
		gl.setVerticalGroup(vGroup);
		return gl;
	}
	/**
	 * //サンプル
	 *
	 * <pre>
	 * Button bt = new Button("初期設定"); bt.addActionListener(new
	 * ActionListener(){ public void actionPerformed(ActionEvent e){ // TODO
	 * ボタンを押した時の処理 } }); Component[][] mpcomponents = { {bt} }; return
	 * mpcomponents;
	 * </pre>
	 */
	protected Component[][] mpcomponents(){
		return null;
	}
	protected LayoutStyle setLayoutStyle(){
		return new LayoutStyle(){
			@Override
			public int getPreferredGap(JComponent component1,JComponent component2,ComponentPlacement type,int position,
					Container parent){
				return 50;
			}
			@Override
			public int getContainerGap(JComponent component,int position,Container parent){
				return 10;
			}
		};
	}
	@Override
	public void set(){
		set(BorderLayout.NORTH);
	}
	protected void set(String borderLayout){
		removeAll();
		add(can,BorderLayout.CENTER);
		Panel makePanel=new Panel();
		//makePanel.add(bt);
		Component[][] mpc=mpcomponents();
		if(mpc!=null){
			GroupLayout mpgl=makeGroupLayout(makePanel,mpc);
			mpgl.setAutoCreateGaps(true);
			mpgl.setAutoCreateContainerGaps(true);
			if(setLayoutStyle()!=null) mpgl.setLayoutStyle(setLayoutStyle());
			makePanel.setLayout(mpgl);
			add(makePanel,borderLayout);
		}
	}
	public static void setLookAndFeel(String LFName,Window c){
		WindowLib.setLookAndFeel(LFName,c);
	}
	public void setLookAndFeel(String LFName){
		setLookAndFeel(LFName,this);
	}
	public static Color getColor(String s){
		return getColor(s,16);
	}
	public static Color getColor(String s,int radix){
		return Color.decode(""+Long.parseLong(s,radix));
	}
	public CanvasBase getcan(){
		return can;
	}
	public static Font getBOLD_Font(int size){
		return new Font("sansserif",Font.BOLD,size);
	}

	@Override
	public int getWindowX(){
		return getWidth();
	}
	@Override
	public int getWindowY(){
		return getHeight();
	}
	@Override
	public boolean isClose(){
		return Close;
	}
	@Override
	public void ReSize(){
		can.imgBuf=createImage(getWidth(),getHeight());
		can.gBuf=can.imgBuf.getGraphics();
	}
	@Override
	public void setWindowlog(WindowLog windowLog){
		windowlog=windowLog;
	}
	@Override
	public boolean AutoReSize(){
		return AutoReSize;
	}
	public static File SaveFileWindow(File file){
		return WindowLib.SaveFileWindow(file);
	}
	public static boolean Yes_NoWindow(Object massage,String title){
		return WindowLib.Yes_NoWindow(massage,title);
	}
	public static String InputWindow(String title,String Default){
		return WindowLib.InputWindow(title,Default);
	}
	public static void InfoWindow(Object massage,String TitleText){
		WindowLib.InfoWindow(massage,TitleText);
	}
	public static void ErrorMassage(String string,Throwable e){
		WindowLib.ErrorMassage(string,e);
	}
	@Override
	public String getTypeName(){
		return "StandardWindow";
	}
	@Override
	public void setFps(float fps){
		can.fps.setFPS(fps);
	}
	@Override
	public FrameRate getFps(){
		return can.fps;
	}
	@Override
	public void Update(CommonGraphics g){
		if(render!=null) render.Update(g);
	}
	@Override
	public Thread getThread(){
		return can.thread;
	}
	@Override
	public KI getKeyInput(){
		return can.Keyinput;
	}
	@Override
	public MI getMouseInput(){
		return can.Mouseinput;
	}
	@Override
	public CanvasComponentManager getCanvasComponentManager(){
		return ccm;
	}
	@Override
	public void setCursor(CommonCursor c){
		setCursor(c.getCursor());
	}
	@Override
	public int getScroll(){
		return CCmouseInput.getData();
	}
	@Override
	public void setRender(Render render){
		this.render=render;
	}

	public void mouseClicked(MouseEvent e){}
	public void keyReleased(KeyEvent e){}
	public void keyPressed(KeyEvent e){}
	public void mouseWheelMoved(MouseWheelEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	private class ccKeyInput extends KI{
		@Override
		public void keyPressed(KeyEvent e){
			ccm.keyPressed(e);
			int keycode=e.getKeyCode();
			if(keycode==KeyEvent.VK_ESCAPE){
				//System.exit(0);
			}
			if(keycode==KeyEvent.VK_SHIFT){
				if(!e.isConsumed()) CCmouseInput.setShift(true);
				keyshift=true;
			}
			if(!e.isConsumed())StandardWindow.this.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e){
			ccm.keyReleased(e);
			int keycode=e.getKeyCode();
			if(keycode==KeyEvent.VK_SHIFT){
				if(!e.isConsumed()) CCmouseInput.setShift(false);
				keyshift=false;
			}
			if(!e.isConsumed())StandardWindow.this.keyReleased(e);
		}
	}

	private class ccMouse extends MI{
		public ccMouse(){
			win=StandardWindow.this;
		}
		@Override
		public synchronized void mouseClicked(MouseEvent e){
			ccm.mouseClicked(e);
			if(!e.isConsumed())StandardWindow.this.mouseClicked(e);
		}
		@Override
		public synchronized void mousePressed(MouseEvent e){
			ccm.mousePressed(e);
			if(!e.isConsumed())StandardWindow.this.mousePressed(e);
		}
		@Override
		public synchronized void mouseReleased(MouseEvent e){
			ccm.mouseReleased(e);
			if(!e.isConsumed())StandardWindow.this.mouseReleased(e);
		}
		@Override
		public void mouseWheelMoved(MouseWheelEvent e){
			ccm.mouseWheelMoved(e);
			if(!e.isConsumed()) super.mouseWheelMoved(e);
			if(!e.isConsumed())StandardWindow.this.mouseWheelMoved(e);
		}
	}
	@Override
	public void init(CommonGraphics g){

	}
	@Override
	public void moveWindow(int x,int y){
		setBounds(x,y,getWidth(),getHeight());
	}
	@Override
	public void setScroll(int sc){
		CCmouseInput.setData(sc);
	}
	@Override
	public int canWidth(){
		return can.getWidth();
	}
	@Override
	public int canHeight(){
		return can.getHeight();
	}
	@Override
	public String RenderingEngineName(){
		return "AWT";
	}
	@Override
	public Tooltip getTooltip(){
		return tooltip;
	}

	private class can extends CanvasBase{
		public static final int NUM_BUFFERS=3;
		private final boolean debugmode=false;
		public BufferStrategy abufferStrategy;
		private boolean FullScreen;
		public int seido=2;
		//public int frame;
		public can(int windowX,int windowY,StandardWindow win,String name,boolean DrawMode,boolean FullScreen){
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
					win.init(new GraphicsAWT(gBuf).setWindow(win));
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
					}
					setCursor=false;
					KeyboardFocusManager kfm=KeyboardFocusManager.getCurrentKeyboardFocusManager();
					Window w=kfm.getActiveWindow();
					if(w!=win.getTooltip().frame&&w!=win&&CCmouseInput.x>=0) {
						CCmouseInput.x=-1;
						CCmouseInput.y=-1;
						win.getTooltip().hide();
					}
					del();//TODO 表示初期化
					try{
						win.Update(gBuf);
					}catch(Throwable u){
						u.printStackTrace();
					}
					cc();
					try{
						win.getWindowLog().draw(new GraphicsAWT(gBuf).setWindow(win),win,-10*(win.getScroll()/seido));
					}catch(Throwable u){
						u.printStackTrace();
					}
					if(!setCursor)setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
			GraphicsAWT g=new GraphicsAWT(gBuf).setWindow(win);
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
		@Override
		public synchronized void removeAllCC(){
			win.getCanvasComponentManager().removeAllCanvasComponent();
		}
		@Override
		public synchronized void removeCC(CanvasComponent cc){
			win.getCanvasComponentManager().removeCanvasComponent(cc);
		}
	}
}