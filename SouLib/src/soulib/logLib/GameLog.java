package soulib.logLib;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import soulib.fileLib.FileEditor;
import soulib.lib.DataEditor;
import soulib.windowLib.StandardWindow;
import soulib.windowLib.WindowMode;

public class GameLog{
	public static GameLog StaticLog;
	public Logger logger;
	private Date date=new Date();
	private SimpleDateFormat Format;
	public String FileName="";
	public LogData[] LogData=new LogData[512];
	public StandardWindow LogWindow;
	public boolean UpOrDown;
	private boolean FileAdd;
	private Level lv=Level.ALL;
	private ConsoleHandler consoleHandler;
	public static void init(){
		init(false,0,0);
	}
	public static StandardWindow init(boolean LogWindow,int Xsize,int Ysize){
		return init(LogWindow?new WindowMode(Xsize,Ysize):null);
	}
	public static StandardWindow init(boolean LogWindow,int Xsize,int Ysize,String fn){
		return init(LogWindow?new WindowMode(Xsize,Ysize):null,fn);
	}
	public static StandardWindow init(WindowMode LogWindow){
		return init(new LogMode(LogWindow,"GameLog",null));
	}
	public static StandardWindow init(WindowMode LogWindow,String fn){
		return init(new LogMode(LogWindow,"GameLog",fn));
	}
	public static StandardWindow init(LogMode LogWindow){
		StaticLog=new GameLog(LogWindow);
		return StaticLog.LogWindow;
	}
	public GameLog(){
		this((String)null);
	}
	public GameLog(String name){
		this((WindowMode)null,name,null);
	}
	public GameLog(String name,String fn){
		this((WindowMode)null,name,fn);
	}
	public GameLog(boolean LogWindow,int Xsize,int Ysize,String name){
		this(LogWindow?new WindowMode(Xsize,Ysize):null,name,null);
	}
	public GameLog(boolean LogWindow,int Xsize,int Ysize,String name,String fn){
		this(LogWindow?new WindowMode(Xsize,Ysize):null,name,fn);
	}
	public GameLog(WindowMode LogWindow,String name,String FileName){
		this(new LogMode(LogWindow,name,FileName));
	}
	public GameLog(LogMode mode){
		if(mode.FileName==null) mode.FileName="";
		this.FileName=mode.FileName;
		if(mode.name==null) mode.name="GameLog";
		try{
			Format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String Dir=FileEditor.Dir();
			File FileDir=new File("."+Dir+"Log");
			if(!FileDir.exists()) FileDir.mkdirs();
			if(FileName.isEmpty()) FileName="Log"+Dir+mode.name+"-"+Format.format(date)+".log";
			FileHandler fileHandler=new FileHandler(FileName,true);
			fileHandler.setFormatter(getFormatter(fileHandler));//new SimpleFormatter());
			logger=Logger.getLogger(mode.name);
			logger.addHandler(fileHandler);
			logger.setLevel(lv);
			consoleHandler=new ConsoleHandler0();
			((ConsoleHandler0)consoleHandler).setOutputStream(System.out);;
			consoleHandler.setLevel(lv);
			consoleHandler.setFormatter(getFormatter(consoleHandler));
			logger.addHandler(consoleHandler);
			logger.setUseParentHandlers(false);
		}catch(Throwable t){
			t.printStackTrace();
		}
		if(mode.LogWindowMode!=null) try{
			this.LogWindow=new StandardWindow(mode.LogWindowMode){
				@Override
				public String getName(){
					return logger.getName()!=null?logger.getName():"Log";
				}
				@Override
				public WindowListener setWindowListener(){
					return new WindowAdapter(){
						@Override
						public void windowClosing(WindowEvent evt){
							//null
						}
					};
				}
			};
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
	/** フォーマットを指定する **/
	public Formatter getFormatter(Handler h){
		return new GameLogFormatter();
	}
	public LogMode getLogMode(){
		LogMode lm=new LogMode(LogWindow.getWindowMode(),logger.getName(),FileName);
		lm.FileAdd=this.FileAdd;
		return lm;
	}
	public void addDataNS(Level level,String Data){
		addDataNS(level,Data,null);
	}
	public void addDataNS(Level level,String Data,Color c){
		try{
			for(int i=LogData.length-2;i>=0;i--){
				LogData[i+1]=LogData[i];
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
		LogData buf=new LogData(Data,level,c);
		buf.setData(Data);
		buf.setLevel(level);
		LogData[0]=buf;
		if(LogWindow!=null){
			LogWindow.getWindowLog().addData(Data);
			LogWindow.del();
		}
		//System.out.println("addData");
	}
	public void log(Level level,String string){
		log(level,string,null);
	}
	public void log(Level level,String string,Color c){
		addDataNS(level,string,c);
		logger.log(level,string);
	}
	public void log(){
		log("");
	}
	public void log(String string,Color c){
		log(Level.INFO,string,c);
	}
	public void log(String string){
		log(Level.INFO,string);
	}
	public void log(int i){
		log(Level.INFO,""+i);
	}
	public void log(boolean b){
		log(Level.INFO,""+b);
	}
	public void log(double data){
		log(data+"");
	}
	public void log(Object obj){
		log(Level.INFO,""+obj.toString());
	}
	public void log(Throwable t){
		String[] X=FileEditor.FE.StringToStrings(t.getMessage());
		for(int i=0;i<X.length;i++){
			if(X[i]!=null) logger.log(Level.WARNING,X[i]);
		}
	}
	public void log(String[] data){
		for(int i=0;i<data.length;i++){
			if(data[i]!=null) log(Level.INFO,data[i]);
		}
	}
	public static void addData(Level level,String Data){
		if(StaticLog==null) StaticLog=new GameLog(new LogMode(null,"GameLog",null));
		StaticLog.addDataNS(level,Data);
	}
	public static void Log(Level level,String string){
		if(StaticLog==null) StaticLog=new GameLog(new LogMode(null,"GameLog",null));
		StaticLog.log(level,string);
	}
	public static void Log(){
		Log("");
	}
	public static void Log(String string){
		Log(Level.INFO,string);
	}
	public static void Log(int i){
		Log(Level.INFO,""+i);
	}
	public static void Log(boolean b){
		Log(Level.INFO,""+b);
	}
	public void Log(double data){
		Log(data+"");
	}
	public static void Log(Object obj){
		Log(Level.INFO,""+obj.toString());
	}
	public static void Log(Throwable t){
		if(StaticLog==null) StaticLog=new GameLog(new LogMode(null,"GameLog",null));
		StaticLog.log(t);
	}
	public static void Log(String[] data){
		if(StaticLog==null) StaticLog=new GameLog(new LogMode(null,"GameLog",null));
		StaticLog.log(data);
	}
	public static LogData[] getLogData(){
		//System.out.println("getData");
		return StaticLog.LogData;
	}
	public static String[] getLogDataString(){
		if(StaticLog==null) StaticLog=new GameLog(new LogMode(null,"GameLog",null));
		String[] s=new String[StaticLog.LogData.length];
		for(int i=0;i<s.length;i++){
			if(StaticLog.LogData[i]!=null) s[i]=StaticLog.LogData[i].toString();
		}
		DataEditor.cut(s);
		return s;
	}
	public void Gyaku(boolean b){
		if(LogWindow!=null) LogWindow.getWindowLog().Gyaku=b;
	}
}

class ConsoleHandler0 extends ConsoleHandler{
	private OutputStream stream;
	@Override
	public void setOutputStream(final OutputStream out){
		stream=new OutputStream(){
			@Override
			public void write(int b) throws IOException{
				out.write(b);
			}
		};
		super.setOutputStream(stream);
	}
	@Override
	public void publish(LogRecord record){
		super.publish(record);
	}
	@Override
	public void close(){
		flush();
		try{
			stream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}