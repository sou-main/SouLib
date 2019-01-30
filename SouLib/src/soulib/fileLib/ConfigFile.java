package soulib.fileLib;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import soulib.lib.ConfigBase;
import soulib.logLib.GameLog;

public class ConfigFile extends ConfigBase{
	public static int DefaultMaxCate=50;
	public static final String[] DataType= {"S","I","L","D","F"};
	public int MaxCate=DefaultMaxCate;
	public static int Miss=-1;
	public static String DefaultEncode="UTF-8";
	protected String[] ConfigFileData= {};
	private boolean error;
	private boolean mod;
	public FileEditor f=new FileEditor();
	/**後で読み込む必要がある*/
	public ConfigFile() {
		
	}
	public ConfigFile(String[] s) throws NullPointerException{
		if(s==null) throw new NullPointerException();
		ConfigFileData=s;
	}
	public ConfigFile(File file,int MaxCate) throws NullPointerException{
		if(file==null) throw new NullPointerException();
		this.file=file.getAbsolutePath();
		ConfigFileData=f.ReadFile0(file,MaxCate);
	}
	public ConfigFile(File file,String[] DefaultData) throws NullPointerException{
		if(file==null) throw new NullPointerException();
		if(!file.exists()){
			f.SaveFile(DefaultData,file);
		}
		this.file=file.getAbsolutePath();
		ConfigFileData=f.ReadFile0(file,MaxCate);
		if(ConfigFileData==null)ConfigFileData=new String[0];
	}
	public ConfigFile(File file){
		this.file=file.getAbsolutePath();
		ConfigFileData=f.ReadFile0(file,MaxCate);
		if(ConfigFileData==null)ConfigFileData=new String[0];
	}
	public ConfigFile(File file,boolean JP) throws NullPointerException{
		if(file==null) throw new NullPointerException();
		this.file=file.getAbsolutePath();
		if(JP)	ConfigFileData=f.ReadFileJP(file);
		else ConfigFileData=f.ReadFile0(file,MaxCate);
		if(ConfigFileData==null)ConfigFileData=new String[0];
	}
	@Override
	public boolean saveConfig(File file){
		if(!mod)return true;
		if(file!=null) this.file=file.getAbsolutePath();
		if(file==null) {
			if(this.file==null)return false;
			else file=new File(this.file);
		}
		return f.SaveFile(ConfigFileData,file,0);
	}
	public boolean isMod() {
		return mod;
	}
	public void saveConfigT(File file) throws IOException{
		if(!mod)return;
		if(file!=null) this.file=file.getAbsolutePath();
		if(file==null) file=new File(this.file);
		f.SaveFileT(ConfigFileData,file,0);
	}
	public void setMaxCate(int MaxCate){
		this.MaxCate=MaxCate;
	}
	public String[] getConfigFileData(){
		return ConfigFileData;
	}
	@Override
	public boolean setConfigDataBoolean(String cate,boolean b){
		return setConfigData(cate,b ? "true" : "false","B");
	}
	@Override
	public boolean setConfigDataInt(String cate,int Data){
		return setConfigData(cate,Data+"","I");
	}
	@Override
	public boolean setConfigDataLong(String cate,long Data){
		return setConfigData(cate,Data+"","L");
	}
	@Override
	public boolean setConfigDataDouble(String cate,double Data){
		return setConfigData(cate,Data+"","D");
	}
	@Override
	public boolean setConfigDataString(String cate,String Data){
		return setConfigData(cate,Data+"","S");
	}
	public boolean setConfigData(String cate,String s,String type){
		int buf1=getConfigLine(cate+Kugiri);
		String buf2=type+Kugiri+cate+Kugiri+s;
		if(buf1!=miss){
			if(ConfigFileData[buf1]==buf2)return true;
			ConfigFileData[buf1]=buf2;
			mod=true;
			return true;
		}else{
			newConfigFileData(cate,buf2);
		}
		return false;
	}
	public int getConfigLine(String cate){
		int j=miss;
		for(int i=0;i<ConfigFileData.length;i++){
			if(ConfigFileData[i]!=null) if(ConfigFileData[i].indexOf(cate)!=-1){
				j=i;
			}
		}
		return j;
	}
	public void newConfigFileData(String cate,String DefaltData){
		if(notNewConfig) return;
		System.out.println("newConfig name="+cate);
		int buf0=ConfigFileData.length+1;
		GameLog.Log("newConfig=====Save--Line="+buf0+" DD "+DefaltData);
		String[] buf1=new String[buf0];
		for(int i=0;i<ConfigFileData.length;i++)
			buf1[i]=ConfigFileData[i];
		buf1[buf0-1]=DefaltData;
		ConfigFileData=buf1;
		mod=true;
		//f.SaveFile(buf1,new File("Test.txt"));
	}
	@Override
	public String getConfigDataString(String s,String DD){
		String Data=DD;
		String buf=s+Kugiri;
		//if(Game.debugMode)new GameLog("------------"+s+"-------------");
		for(int i=0;i<ConfigFileData.length;i++){
			//if(Game.debugMode)System.out.println(i);
			if(ConfigFileData[i]==null) continue;
			if(ConfigFileData[i]=="") continue;
			/*
				String buf3=ConfigFileData[i].substring(1,2);
				String buf4=ConfigFileData[i];
				if(buf3.indexOf(Kugiri)!=-1){
					buf4=ConfigFileData[i].substring(2,ConfigFileData[i].length());
				}
				int buf6=buf4.indexOf(Kugiri);
				String buf5=buf4.substring(0,buf6);
				boolean flag0=false;
				if(flag0){
					System.out.println("----------------------");
					System.out.println("s="+s);
					System.out.println("buf="+buf);
					System.out.println("buf3="+buf3);
					System.out.println("buf4="+buf4);
					System.out.println("buf5="+buf5);
					System.out.println("buf6="+buf6);
					System.out.println("======================");
				}
				*/
			if(ConfigFileData[i].indexOf(buf)!=-1){
				//new GameLog(i);
				try{
					int buf0=ConfigFileData[i].indexOf(buf)+buf.length();
					int buf1=ConfigFileData[i].length();
					Data=ConfigFileData[i].substring(buf0,buf1);
				}catch(Throwable t){
					GameLog.Log(t);
					if(error){
						new Crash_report(t,"getConfig_String");
					}
				}
			}
		}
		//if(Game.debugMode)new GameLog(Data);
		if(error&&(Data==null||Data.equals(DD))){
			throw new Error();
		}
		return Data;
	}
	@Override
	public int getConfigDataInt(String s,int DD){
		int data=DD;
		try{
			String buf=getConfigDataString(s);
			if(buf!="") data=StringtoInt(buf);
		}catch(Throwable t){
			t.printStackTrace();
		}
		if(data==DD){
			setConfigDataInt(s,DD);
			data=DD;
		}
		return data;
	}
	@Override
	public int getConfigDataInt(String s){
		return getConfigDataInt(s,miss);
	}
	@Override
	public long getConfigDataLong(String string){
		return getConfigDataLong(string,miss);
	}
	@Override
	public long getConfigDataLong(String s,long DD){
		Long Data=DD;
		try{
			String buf=getConfigDataString(s);
			if(buf!="") Data=StringtoLong(buf);
		}catch(Throwable t){
			new Crash_report(t,"getConfig_Long");
		}
		//if(Game.debugMode)new GameLog(Data);
		if(Data==DD){
			if(error){
				throw new Error();
			}
		}
		return Data;
	}
	@Override
	public double getConfigDataDouble(String s,double DD){
		Double Data=DD;
		try{
			String buf=getConfigDataString(s);
			if(buf!="") Data=StringtoDouble(buf);
		}catch(Throwable t){
			new Crash_report(t,"getConfig_Double");
		}
		//if(Game.debugMode)new GameLog(Data);
		if(Data==DD){
			setConfigDataDouble(s,DD);
			if(error){
				throw new Error();
			}
			return DD;
		}else{
			return Data;
		}
	}
	@Override
	public boolean getConfigDataBoolean(String s){
		return getConfigDataBoolean(s,false);
	}
	@Override
	public boolean getConfigDataBoolean(String s,boolean defalutData){
		String buf=getConfigDataString(s);
		boolean Data=defalutData;
		try{
			Data=StringtoBoolean(buf);
		}catch(Throwable t){
			if(debugMode) t.printStackTrace();
		}
		setConfigDataBoolean(s,Data);
		return Data;
	}
	public static boolean StringtoBoolean(String s) throws IllegalArgumentException{
		s.toLowerCase(Locale.getDefault());
		if(s.indexOf("true")!=-1){
			return true;
		}else if(s.indexOf("false")!=-1){
			return false;
		}
		throw new IllegalArgumentException("Not true Or false - "+s);
	}
	public static boolean StringToBoolean(String s){
		return Boolean.parseBoolean(s);
	}
	public static double StringtoDouble(String s){
		try{
			double buf=Double.valueOf(s);
			return buf;
		}catch(Throwable e){
			e.printStackTrace();;
			return Miss;
		}
	}
	public static byte StringtoNumber(String s){
		try{
			return Byte.valueOf(s);
		}catch(Throwable e){
			System.out.println(e.toString());
			return (byte) Miss;
		}
	}
	public static short StringtoShort(String s){
		try{
			return Short.parseShort(s);
		}catch(Throwable e){
			System.out.println(e.toString());
			return (short) Miss;
		}
	}
	public static int StringtoInt(String s){
		try{
			return Integer.parseInt(s);
		}catch(Throwable e){
			System.out.println(e.toString());
			return Miss;
		}
	}
	public String getDataType(String Cate){
		String DT="S";
		int line=getConfigLine(Cate);
		int endIndex=ConfigFileData[line].indexOf(Kugiri);
		if(endIndex==1) DT=ConfigFileData[line].substring(0,1);
		return DT;
	}
	public ConfigFile error(boolean b){
		error=b;
		return this;
	}
	public String[] getData(){
		return ConfigFileData;
	}
	@Override
	public String getConfigDataString(String string){
		return getConfigDataString(string,"");
	}
	public HashMap<String, String> getMapString(){
		HashMap<String, String> map=new HashMap<String, String>();
		for(int i=0;i<ConfigFileData.length;i++){
			String key=getName(i);
			map.put(key,getConfigDataString(key));
		}
		return map;
	}
	/** 指定された行のデータを取得する **/
	public String getData(int line){
		return getConfigDataString(getName(line));
	}
	/** 指定された行の項目名を取得する **/
	public String getNameV2(int line){
		String[] list=ConfigFileData[line].split(Kugiri);
		if(list.length<2)return "";
		boolean dt=false;
		for(int i=0;i<DataType.length;i++)if(list[0]==DataType[i]) {
			dt=true;
			break;
		}
		return dt?list[0]:list[1];
	}
	/** 指定された行の項目名を取得する **/
	public String getName(int line){
		if(line<0) return "";
		int li=ConfigFileData[line].lastIndexOf(Kugiri);
		if(li==-1) return "";
		String buf=ConfigFileData[line].substring(0,li);
		int in=buf.indexOf(Kugiri);
		if(in<0) return buf;
		return buf.substring(in+1,buf.length());
	}
	public static int StringtoInt(String input,int Default){
		int r=StringtoInt(input);
		return r==Miss ? Default : r;
	}
	public static long StringtoLong(String value){
		try{
			long buf=Long.valueOf(value);
			return buf;
		}catch(Throwable e){
			return Miss;
		}
	}
	@Override
	public double getConfigDataDouble(String string){
		return getConfigDataDouble(string,miss);
	}
	@Override
	public void remove(String cate){
		remove(getConfigLine(cate));
	}
	public void remove(int i){
		ConfigFileData[i]=null;
	}
	@Override
	public boolean read(File file){
		if(file==null||!file.isFile())return false;
		this.file=file.getAbsolutePath();
		String[] tmp=f.ReadFile0(file,MaxCate);
		if(tmp==null)return false;
		ConfigFileData=tmp;
		return true;
	}
}
