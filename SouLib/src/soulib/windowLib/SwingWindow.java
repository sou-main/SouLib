package soulib.windowLib;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;

import soulib.lib.DataEditor;
import soulib.logLib.GameLog;
import soulib.logLib.WindowLog;

public class SwingWindow extends JFrame implements CanvasWindow,KeyMouseInput{
	/**TypeName*/
	public static final String WindowCode="SwingWindow";
	private SwingWindowCanvas sc;
	private WindowLog wl;
	private Render render;
	private boolean close;
	public WindowMode wm;
	protected CanvasComponentManager ccm;
	JccMouse CCmouseInput;
	private JccKeyInput CCkeyInput;
	private boolean setCursor;
	private Tooltip tooltip=new Tooltip(this);
	public SwingWindow(int width,int height){
		this(new WindowMode(width,height));
	}
	public SwingWindow(WindowMode wm){
		super(wm.gc);
		this.wm=wm;
		ccm=new CanvasComponentManager();
		wl=new WindowLog();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		CCmouseInput=new JccMouse();
		CCkeyInput=new JccKeyInput();
		sc=new SwingWindowCanvas(wm,this);
		setCanvasMouseInput(CCmouseInput);
		setKeyInput(CCkeyInput);
		init();
		setSize(wm.WindowX,wm.WindowY);
		this.render=wm.render;
		if(wm.WindowListener!=null) addWindowListener(wm.WindowListener);
		else addWindowListener(setWindowListener());
		if(wm.FullScreen) if(wm.Device!=null) initFullScreen(wm.Device);
		else initFullScreen(wm.monitorID);
		add(sc,BorderLayout.CENTER);
		if(wm.NoCanvas){
			sc.setSize(1,1);
			pack();
		}
		sc.initThread(wm);
		if(wm.NoNewThread) sc.run();
		if(wm.icon!=null) setIconImage(wm.icon);
		setCenter();
		setVisible(!wm.defaultHide);
		setTitle(wm.name);
		toFront();
		if(render!=null) render.init(this);
	}
	public void setCursor(Cursor c) {
		super.setCursor(c);
		setCursor=true;
	}
	public void repaint(){
		sc.repaint();
	}
	@Override
	public String getName(){
		return wm.name;
	}
	@Override
	public String getTitle(){
		return super.getTitle();
	}
	@Override
	public void moveWindow(int x,int y){
		setBounds(x,y,getWidth(),getHeight());
	}
	@Override
	public void del(){
		sc.del();
	}
	@Override
	public void del(Color color){
		sc.del(color);
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
	public void Update(CommonGraphics g){
		if(render!=null) render.Update(g);
	}
	@Override
	public void close(){
		sc.End();
		close=true;
		tooltip.close();
		dispose();
	}
	@Override
	public void setCenter(){
		StandardWindow.setCenter(this);
	}
	@Override
	public void init(){
		//set();
		if(render!=null) render.init(this);
	}
	@Override
	public void setWindowlog(WindowLog windowLog){
		wl=windowLog;
	}
	@Override
	public WindowBase addData(String data){
		wl.addData(data);
		return this;
	}
	@Override
	public WindowLog getWindowLog(){
		return wl;
	}
	@Override
	public String[] getDataList(){
		return wl.getDataListString();
	}
	@Override
	public void repaintCan(){
		sc.repaint();
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
	public boolean isClose(){
		return close;
	}
	@Override
	public boolean AutoReSize(){
		return wm.AutoReSize;
	}
	@Override
	public void ReSize(){
		sc.ReSize();
	}
	/**
	 * //サンプル
	 *
	 * <pre>
	 * Button bt = new Button("初期設定"); bt.addActionListener(new ActionListener(){
	 * public void actionPerformed(ActionEvent e){ // TODO ボタンを押した時の処理 } });
	 * Component[][] mpcomponents = { {bt} }; return mpcomponents;
	 * </pre>
	 *
	 * @return 現在は使用されない
	 */
	protected JComponent[][] mpcomponents(){
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
		getContentPane().add(new JScrollPane(sc),BorderLayout.CENTER);
		JComponent[][] mpc=mpcomponents();
		if(mpc!=null){
			JPanel makePanel=new JPanel();
			//makePanel.add(bt);
			GroupLayout mpgl=makeGroupLayout(makePanel,mpc);
			mpgl.setAutoCreateGaps(true);
			mpgl.setAutoCreateContainerGaps(true);
			if(setLayoutStyle()!=null) mpgl.setLayoutStyle(setLayoutStyle());
			makePanel.setLayout(mpgl);
			getContentPane().add(makePanel);
		}
	}
	public static GroupLayout makeGroupLayout(JPanel makePanel,JComponent[][] components){
		GroupLayout gl=new GroupLayout(makePanel);
		// 水平方向のグループ
		SequentialGroup hGroup=gl.createSequentialGroup();
		for(int i=0;i<components[0].length;i++){
			ParallelGroup pg=gl.createParallelGroup();
			for(int j=0;j<components.length;j++){
				if(components[j][i]==null) components[j][i]=new JLabel("");
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
	@Override
	public String getTypeName(){
		return WindowCode;
	}
	@Override
	public void setFps(float fps){
		sc.fps.setFPS(fps);
	}
	@Override
	public FrameRate getFps(){
		return sc.fps;
	}
	@Override
	public int getWindowX(){
		return wm.WindowX;
	}
	@Override
	public int getWindowY(){
		return wm.WindowY;
	}
	@Override
	public Thread getThread(){
		return sc.thread;
	}
	@Override
	public KI getKeyInput(){
		return sc.Keyinput;
	}
	@Override
	public MI getMouseInput(){
		return sc.Mouseinput;
	}
	@Override
	public WindowMode getWindowMode(){
		return wm;
	}
	@Override
	public long getSleep(){
		return sc.sleep;
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
	public void setKeyInput(KI input){
		if(sc.Keyinput==input) return;
		sc.Keyinput=input;
		addKeyListener(input);
		sc.addKeyListener(input);
	}
	@Override
	public void setMouseInput(MI input){
		if(sc.Mouseinput==input) return;
		input.win=this;
		sc.Mouseinput=input;
		addMouseWheelListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
		sc.addMouseWheelListener(input);
		sc.addMouseListener(input);
		sc.addMouseMotionListener(input);
	}
	public void setCanvasMouseInput(MI input){
		if(sc.Mouseinput==input) return;
		input.win=this;
		sc.Mouseinput=input;
		sc.addMouseWheelListener(input);
		sc.addMouseListener(input);
		sc.addMouseMotionListener(input);
	}
	public void setWindowMouseInput(MI input){
		if(DataEditor.contains(input,getMouseWheelListeners())) return;
		if(DataEditor.contains(input,getMouseListeners())) return;
		if(DataEditor.contains(input,getMouseMotionListeners())) return;
		addMouseWheelListener(input);
		addMouseListener(input);
		addMouseMotionListener(input);
	}
	@Override
	public void setRender(Render render){
		this.render=render;
	}
	public void setBackground(Color c){
		super.setBackground(c);
		if(sc!=null) sc.setBackground(c);
	}
	@Override
	public void init(CommonGraphics g){

	}
	private void initFullScreen(int id){
		GraphicsEnvironment ge=GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		initFullScreen(ge.getScreenDevices()[id]);
	}
	private void initFullScreen(GraphicsDevice device){
		try{
			StandardWindow.initFullScreen(this,device,GameLog.StaticLog);
			wm.FullScreen=true;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void setScroll(int scroll){
		CCmouseInput.setData(scroll);
	}
	public Point getPoint(){
		return CCmouseInput.getPoint();
	}
	public void mouseClicked(MouseEvent e){}
	public void keyReleased(KeyEvent e){}
	public void keyPressed(KeyEvent e){}
	public void mouseWheelMoved(MouseWheelEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mousePressed(MouseEvent e){}

	private class JccKeyInput extends KI{
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
			if(!e.isConsumed())SwingWindow.this.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e){
			ccm.keyReleased(e);
			int keycode=e.getKeyCode();
			if(keycode==KeyEvent.VK_SHIFT){
				if(!e.isConsumed()) CCmouseInput.setShift(false);
				keyshift=false;
			}
			if(!e.isConsumed())SwingWindow.this.keyReleased(e);
		}
	}

	private class JccMouse extends MI{
		private Point buf;
		public JccMouse(){
			buf=new Point();
			win=SwingWindow.this;
		}
		@Override
		public synchronized void mouseClicked(MouseEvent e){
			ccm.mouseClicked(e);
			if(!e.isConsumed())SwingWindow.this.mouseClicked(e);
		}
		@Override
		public synchronized void mousePressed(MouseEvent e){
			ccm.mousePressed(e);
			if(!e.isConsumed())SwingWindow.this.mousePressed(e);
		}
		@Override
		public synchronized void mouseReleased(MouseEvent e){
			ccm.mouseReleased(e);
			if(!e.isConsumed())SwingWindow.this.mouseReleased(e);
		}
		@Override
		public void mouseWheelMoved(MouseWheelEvent e){
			ccm.mouseWheelMoved(e);
			if(!e.isConsumed()){
				super.mouseWheelMoved(e);
				if(!e.isConsumed())SwingWindow.this.mouseWheelMoved(e);
			}
		}
		public Point getPoint(){
			buf.setLocation(x,y);
			return buf;
		}
	}

	@Override
	public int canWidth(){
		return sc.getWidth();
	}
	@Override
	public int canHeight(){
		return sc.getHeight();
	}
	@Override
	public String RenderingEngineName(){
		return "Swing";
	}
	@Override
	public Tooltip getTooltip(){
		return tooltip;
	}

	private class SwingWindowCanvas extends JPanel implements Runnable{
		public boolean AutoUpdate=true;
		public MI Mouseinput;
		public KI Keyinput;
		public long sleep;
		public FrameRate fps=new FrameRate();
		public Graphics gBuf;
		public Thread thread;
		protected SwingWindow win;
		protected boolean End=false;
		protected boolean close;
		private int timeout=2000;

		public SwingWindowCanvas(WindowMode wm,SwingWindow win){
			this.win=win;
			setSize(getPreferredSize());
			//SwingWindowCanvas.this.
			setBackground(Color.white);
			//setSize(wm.WindowX,wm.WindowY);
			AutoUpdate=wm.AutoUpdate;
			End=false;
		}
		public void initThread(WindowMode wm){
			if(wm.NoNewThread){
				thread=Thread.currentThread();
			}else{
				thread=new Thread(this,wm.name);
				thread.start();
			}
		}
		public void del(){
			del(getBackground());
		}
		@Override
		public Dimension getPreferredSize(){
			WindowMode wm=win.getWindowMode();
			return new Dimension(wm.WindowX,wm.WindowY);
		}

		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			gBuf=g;
			win.setCursor=false;
			KeyboardFocusManager kfm=KeyboardFocusManager.getCurrentKeyboardFocusManager();
			Window w=kfm.getActiveWindow();
			if(w!=win.getTooltip().frame&&w!=win&&CCmouseInput.x>=0) {
				CCmouseInput.x=-1;
				CCmouseInput.y=-1;
				win.getTooltip().hide();
			}
			win.Update(gBuf);
			GraphicsAWT cg=new GraphicsAWT(gBuf).setWindow(win);
			win.getWindowLog().draw(cg,win,win.getScroll());
			win.getCanvasComponentManager().draw(cg,win.CCmouseInput.getPoint());
			if(!win.setCursor)win.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		@Override
		public void run(){
			while(!End&&AutoUpdate){
				repaint();
				win.getFps().count();
				win.getFps().sleep();
			}
			close=true;
		}
		public void End(){
			End=true;
			while(!close&&timeout>0){
				timeout-=100;
				try{
					Thread.sleep(100);
				}catch(InterruptedException e){
				}
			}
		}
		public void del(Color color){
			gBuf.setColor(color);
			gBuf.fillRect(0,0,win.getWindowX(),win.getWindowY());
		}
		public void ReSize(){
			WindowMode wm=win.getWindowMode();
			if(wm.WindowX!=win.getWidth()||wm.WindowY!=win.getHeight()){
				this.setSize(win.getWidth(),win.getHeight());
				wm.WindowX=getWidth();
				wm.WindowY=getHeight();
			}
		}
	}
}