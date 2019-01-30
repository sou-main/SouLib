package soulib.logLib;

import soulib.windowLib.WindowMode;

public class LogMode{
	public boolean FileAdd=false;
	public String FileName="";
	public String name="GameLog";
	public WindowMode LogWindowMode=null;
	public LogMode(){
		this((String)null);
	}
	public LogMode(String name){
		this(false,0,0,name);
	}
	public LogMode(String name,String fn){
		this(false,0,0,name,fn);
	}
	public LogMode(boolean LogWindow,int Xsize,int Ysize,String name){
		this(LogWindow?new WindowMode(Xsize,Ysize):null,name,null);
	}
	public LogMode(boolean LogWindow,int Xsize,int Ysize,String name,String fn){
		this(LogWindow?new WindowMode(Xsize,Ysize):null,name,fn);
	}
	public LogMode(WindowMode logWindow,String name2,String fileName2){
		LogWindowMode=logWindow;
		name=name2;
		FileName=fileName2;
	}
}
