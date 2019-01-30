package soulib.windowLib;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import soulib.fileLib.ConfigFile;
import soulib.fileLib.FileEditor;

/**ウィンドウに関連する静的関数の集まり*/
public class WindowLib{
	/**黄緑を表します*/
	public static final Color lightGreen=new Color(192,255,82);
	/**エメラルドグリーンを表します*/
	public static final Color emeraldGreen=new Color(0,255,128);
	/**深緑を表します*/
	public static final Color darkGreen=new Color(0,90,0);
	/**薄紫を表します*/
	public static final Color lightPaple=new Color(162,82,162);
	/**紫を表します*/
	public static final Color paple=new Color(192,0,192);
	/**深紫を表します*/
	public static final Color darkPaple=new Color(92,0,92);
	public static FontManager FM=new FontManager();

	public static Font getITALIC_Font(int size){
		return FM.get(Font.ITALIC,size);
	}
	public static Font getPLAIN_Font(int size){
		return FM.get(Font.PLAIN,size);
	}
	public static Font getBOLD_Font(int size){
		return FM.get(Font.BOLD,size);
	}
	public static File OpenFileWindow(File Default){
		return OpenFileWindow(Default,JFileChooser.FILES_ONLY);
	}
	public static File OpenFileWindow(File Default,int Mode){
		return OpenFileWindow(Default,Mode,(FileNameExtensionFilter)null);
	}
	public static File OpenFileWindow(File Default,int Mode,FileNameExtensionFilter... fnef){
		if(Default==null)Default=new File(".");
		JFileChooser fc=new JFileChooser(Default);
		fc.setFileSelectionMode(Mode); // Modeで指定した物のみを選択可能にする
		fc.setDialogTitle("開く");
		if(fnef!=null){
			FileFilter[] f=fc.getChoosableFileFilters();
			for(int i=0;i<f.length;i++)
				fc.removeChoosableFileFilter(f[i]);
			for(int i=0;i<fnef.length;i++)
				fc.addChoosableFileFilter(fnef[i]);
		}
		int ret=fc.showOpenDialog(null);
		if(ret!=JFileChooser.APPROVE_OPTION)
			return null; // 何もせずに戻る
		return fc.getSelectedFile();
	}
	public static File SaveFileWindow(File Default){
		return SaveFileWindow(Default,JFileChooser.FILES_ONLY);
	}
	public static File SaveFileWindow(File Default,int Mode,FileNameExtensionFilter... fnef){
		//if(Default==null)Default=new File("."+FileEditor.Dir()+"Default");
		JFileChooser fc=new JFileChooser(Default);
		fc.setFileSelectionMode(Mode); // Modeで指定した物のみを選択可能にする
		fc.setDialogTitle("保存");
		fc.setSelectedFile(Default);
		if(fnef!=null) {
			for(FileNameExtensionFilter f:fnef)fc.addChoosableFileFilter(f);
		}
		int ret=fc.showSaveDialog(null);
		if(ret!=JFileChooser.APPROVE_OPTION)return null; // 何もせずに戻る
		return fc.getSelectedFile();
	}
	public static boolean Yes_NoWindow(String message,String TitleText){
		return JOptionPane.showConfirmDialog(null,message,
				TitleText,
				JOptionPane.YES_NO_OPTION)==0?true:false;
	}
	public static boolean Yes_NoWindow(String TitleText,String... Text){
		return Yes_NoWindow(Text,TitleText);
	}
	public static boolean Yes_NoWindow(Object massage,String TitleText){
		return JOptionPane.showConfirmDialog(null,massage,
				TitleText,
				JOptionPane.YES_NO_OPTION)==0?true:false;
	}
	public static Boolean Yes_No_CancelWindow(String TitleText,String... Text){
		return Yes_No_CancelWindow(TitleText,(Object)Text);
	}
	public static Boolean Yes_No_CancelWindow(String TitleText,Object massage){
		int rt=JOptionPane.showConfirmDialog(null,massage,
				TitleText,
				JOptionPane.YES_NO_CANCEL_OPTION);
		if(rt==JOptionPane.CANCEL_OPTION) return null;
		return rt==JOptionPane.YES_OPTION?true:false;
	}
	public static void InfoWindow(Object massage,String TitleText){
		new showMessageDialog(massage,TitleText,JOptionPane.INFORMATION_MESSAGE).start();
	}
	public static void ErrorWindow(Object massage,String TitleText){
		new showMessageDialog(massage,TitleText,JOptionPane.ERROR_MESSAGE).start();
	}
	public static void WarningWindow(Object massage,String TitleText){
		new showMessageDialog(massage,TitleText,JOptionPane.WARNING_MESSAGE).start();
	}
	/** 表示中は呼び出したスレッドが止まる */
	public static void InfoWindowStop(Object massage,String TitleText){
		JOptionPane.showMessageDialog(null,massage,TitleText,JOptionPane.INFORMATION_MESSAGE);
	}
	/** 表示中は呼び出したスレッドが止まる */
	public static void ErrorWindowStop(Object massage,String TitleText){
		JOptionPane.showMessageDialog(null,massage,TitleText,JOptionPane.ERROR_MESSAGE);
	}
	/** 表示中は呼び出したスレッドが止まる */
	public static void WarningWindowStop(Object massage,String TitleText){
		JOptionPane.showMessageDialog(null,massage,TitleText,JOptionPane.WARNING_MESSAGE);
	}
	public static String InputWindow(String Title){
		return JOptionPane.showInputDialog(Title);
	}
	public static String InputWindow(String title,String Default){
		return InputWindow(title,null,Default);
	}
	public static String InputWindow(String title,String[] Celect,String Default){
		return (String)JOptionPane.showInputDialog(null,null,title,
				JOptionPane.QUESTION_MESSAGE,null,Celect,Default==null?Celect[0]:Default);
	}
	public static void ErrorMassage(String MassageTitle,Throwable e){
		ErrorMassage(MassageTitle,e,false);
	}
	public static void ErrorMassageStop(String MassageTitle,Throwable e){
		ErrorMassage(MassageTitle,e,true);
	}
	public static void ErrorMassage(String MassageTitle,Throwable e,boolean stop){
		StringWriter stringwriter=new StringWriter();
		PrintWriter printwriter=new PrintWriter(stringwriter);
		if(MassageTitle==null)MassageTitle="エラー";
		printwriter.println(MassageTitle);
		printwriter.println();
		e.printStackTrace(printwriter);
		if(stop)ErrorWindowStop(stringwriter.toString(),MassageTitle);
		else ErrorWindow(stringwriter.toString(),MassageTitle);
		e.printStackTrace();
	}
	public static String[] InputWindow(String name,String[] Default){
		return InputArrayWindow(name,Default);
	}
	public static String[] InputArrayWindow(String name,String[] Default){
		String arr=InputArrayWindow(name,FileEditor.FE.StringsToString(Default));
		return FileEditor.FE.StringToStrings(arr);
	}
	public static String InputArrayWindow(String name,String Default){
		WindowMode wm=new WindowMode();
		wm.name=name;
		return InputArrayWindow(wm,Default);
	}
	public static String InputArrayWindow(WindowMode wm,String Default){
		class iw extends StandardWindow{
			public boolean binalyMode=false;
			public String re=null;
			public TextArea ta;
			public Thread t;
			public iw(String s,WindowMode wm){
				super(wm);
				if(s!=null) re=s;
				if(ta!=null&&re!=null){
					//d=FileEditor.FE.StringsToString(re);
					ta.setText(re);
					updateMode();
					//ta.setRows(8);
				}
				//mb();
			}
			@SuppressWarnings("unused")
			public void mb() {
				MenuBar mb=new MenuBar();
				Menu m=new Menu("モード");
				final CheckboxMenuItem mi=new CheckboxMenuItem("バイナリモード");
				mi.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e){
						binalyMode=mi.getState();
						updateMode();
					}
				});
				m.add(mi);
				mb.add(m);
				this.setMenuBar(mb);
			}
			private void updateMode(){
				re=ta.getText();
				if(binalyMode) {
					StringBuilder sb=new StringBuilder();
					append(sb,re.getBytes(StandardCharsets.UTF_8));
					re=sb.toString();
					ta.setText(re);
				}
			}
			private String toString(String s) throws IOException {
				if(s==null)return s;
				if(s.isEmpty())return s;
				ByteArrayOutputStream baos=new ByteArrayOutputStream();
				StringReader sr=new StringReader(s);
				int r;
				byte b=0;
				int i=0;
				byte[] shift= {1,2,4,8,16,32,64,-1};
				while(sr.ready()) {
					r=sr.read();
					if(r==-1)break;
					if(r=='0')i++;
					else if(r=='1') {
						i++;
						b=(byte) (b|shift[i]);
					}
					if(i==7) {
						baos.write(b);
						b=0;
						i=0;
					}
				}
				if(i!=0)baos.write(b);
				return baos.toString("utf-8");
			}
			private void append(StringBuilder sb,byte[] b) {
				byte[] mask={1,2,4,8,16,32,64,-1};
				for(int i=0;i<b.length;i++) {
					for(int j=0;j<8;j++) {
						sb.append((b[i]&mask[j])!=0?1:0);
					}
				}
			}
			@Override
			protected Component[][] mpcomponents(){
				ta=new TextArea(re);
				Button b2=new Button("OK");
				b2.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e){
						String c=ta.getText();
						if(c==null) re=null;
						else re=c;//FileEditor.FE.StringToStrings(c,500);
						close();
					}
				});
				//ta.setSize(200,100);
				Component[][] components={
						{ta},
						{b2}
				};
				return components;
			}
			@Override
			public WindowListener setWindowListener(){
				return new WindowAdapter(){
					@Override
					public void windowClosing(WindowEvent evt){
						re=null;
						close();
					}
				};
			}
			@Override
			public void close(){
				super.close();
				t.interrupt();
			}
			@Override
			public void init(){
				//set();
				Component[][] arr=mpcomponents();
				this.add(arr[0][0],BorderLayout.CENTER);
				this.add(arr[1][0],BorderLayout.SOUTH);
				pack();
				//setResizable(false);
			}
			public String toString() {
				if(binalyMode) {
					try{
						return toString(re);
					}catch(IOException e){
						e.printStackTrace();
					}
				}
				return re;
			}
		};
		wm.NoCanvas=true;
		iw i=new iw(Default,wm);
		i.setFps(1);
		i.pack();
		i.t=Thread.currentThread();
		while(true){
			try{
				Thread.sleep(200);
			}catch(InterruptedException e){
				break;
			}
		}
		return i.toString();
	}
	public static synchronized StandardWindow ConfigWindow(ConfigFile file,WindowMode mode){
		return new ConfigWindow(mode,file);
	}
	public static void InfoWindow(String title,String... massage){
		InfoWindow(massage,title);
	}
	public static Color getColor(int i){
		Color color=Color.white;
		switch(i){
			case (0):
				color=Color.white;
			break;
			case (1):
				color=Color.blue;
			break;
			case (2):
				color=Color.darkGray;
			break;
			case (3):
				color=Color.green;
			break;
			case (4):
				color=Color.red;
			break;
			case (5):
				color=Color.magenta;
			break;
			case (6):
				color=Color.orange;
			break;
			case (7):
				color=Color.pink;
			break;
			case (8):
				color=Color.yellow;
			break;
			case (9):
				color=Color.cyan;
			break;
		}
		return color;
	}
	public static void setCenter(Window w){
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device=ge.getDefaultScreenDevice();
		DisplayMode dm=device.getDisplayMode();
		w.setBounds(dm.getWidth()/2-w.getWidth()/2,dm.getHeight()/2-w.getHeight()/2,w.getWidth(),w.getHeight());
	}
	public static DisplayMode getDisplayMode(){
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device=ge.getDefaultScreenDevice();
		return device.getDisplayMode();
	}
	public static void drawManual(Graphics g,String[] strings){
		drawManual(g,strings,50);
	}
	public static void drawManual(Graphics g,String[] strings,int StartY){
		drawManual(g,strings,StartY,null);
	}
	public static void drawManual(Graphics g,String[] strings,int StartY,Font f){
		drawManual(new GraphicsAWT((Graphics2D)g),strings,new Point(50,StartY),f);
	}
	public static void drawManual(Graphics g,String[] strings,Point Start){
		drawManual(new GraphicsAWT((Graphics2D)g),strings,Start,null);
	}
	public static void drawManual(Graphics g,String[] strings,Point Start,Font f){
		drawManual(new GraphicsAWT((Graphics2D)g),strings,Start,f);
	}
	public static void drawManual(CommonGraphics g,String[] strings){
		drawManual(g,strings,50);
	}
	public static void drawManual(CommonGraphics g,String[] strings,int StartY){
		drawManual(g,strings,StartY,null);
	}
	public static void drawManual(CommonGraphics g,String[] strings,int StartY,Font f){
		Point p=new Point(50,StartY);
		drawManual(g,strings,p,null);
	}
	public static void drawManual(CommonGraphics g,String[] strings,Point Start){
		drawManual(g,strings,Start,null);
	}
	public static void drawManual(CommonGraphics g,String[] strings,Point Start,Font f){
		try{
			if(f==null) f=new Font("sansserif",Font.BOLD,20);
			g.setColor(Color.black);
			g.setFont(f);
			int i=0;
			while(!(i>=strings.length)){
				if(strings[i]!=null){
					g.drawString(strings[i],Start.x,(i+1)*(f.getSize()+5)+(Start.y));
				}
				if(i>100) break;
				i++;
			}
		}catch(Throwable t){

		}
	}
	private static Graphics2D g=null;
	public static int getPaintWidth(Font font,String s) {
		return cg().getFontMetrics(font).stringWidth(s);
	}
	public static int getPaintHeight(Font font) {
		return cg().getFontMetrics(font).getHeight();
	}
	public static FontMetrics getFontMetrics(Font font) {
		return cg().getFontMetrics(font);
	}
	private static Graphics2D cg(){
		if(g!=null)return g;
		BufferedImage bi=new BufferedImage(2,2,BufferedImage.TYPE_4BYTE_ABGR);
		g=bi.createGraphics();
		return g;
	}
	public static void setLookAndFeel(String LFName,Window w){
		String newLFClassName=UIManager.getSystemLookAndFeelClassName();
		if(LFName!=null){
			LookAndFeelInfo[] lf=UIManager.getInstalledLookAndFeels();
			for(LookAndFeelInfo info:lf){
				if(LFName.equals(info.getName())){
					newLFClassName=info.getClassName();
					break;
				}
			}
		}
		try{
			UIManager.setLookAndFeel(newLFClassName); // ルックアンドフィールの変更
			if(w!=null){
				SwingUtilities.updateComponentTreeUI(w); // ルックアンドフィールの更新通知
				//w.pack(); //適切サイズに変更
			}
		}catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}catch(InstantiationException ex){
			ex.printStackTrace();
		}catch(IllegalAccessException ex){
			ex.printStackTrace();
		}catch(UnsupportedLookAndFeelException ex){
			ex.printStackTrace();
		}
	}
}
class showMessageDialog extends Thread{
	private Object mas;
	private String Title;
	private int messageType;
	public showMessageDialog(Object massage,String TitleText,int mt){
		super(TitleText);
		Title=TitleText;
		mas=massage;
		messageType=mt;
	}
	@Override
	public void run(){
		JOptionPane.showMessageDialog(null,mas,Title,messageType);
	}
}