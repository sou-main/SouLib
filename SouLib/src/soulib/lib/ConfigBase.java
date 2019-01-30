package soulib.lib;

import java.io.File;
import java.util.Locale;

public abstract class ConfigBase{

	public int miss=-1;
	public boolean debugMode=false;
	public boolean notNewConfig=false;
	public String Kugiri=":";
	public String file;
	{
		
	}
	public abstract boolean saveConfig(File file);
	public abstract String getConfigDataString(String s,String DD);
	public abstract boolean setConfigDataString(String cate,String Data);
	public abstract boolean isMod();

	/**引数にnullを指定することと等価*/
	public boolean saveConfig() {
		if(!isMod())return true;
		return saveConfig(null);
	}
	public String getConfigDataString(String string){
		return getConfigDataString(string,null);
	}
	public String getConfigDataString(String string,String DD,ConfigBase miss){
		return getConfigDataString(string,miss.getConfigDataString(string,DD));
	}
	public boolean getConfigDataBoolean(String s){
		return getConfigDataBoolean(s,false);
	}
	public boolean getConfigDataBoolean(String s,boolean defalutData){
		try{
			String s0=getConfigDataString(s);
			s0=s0.toLowerCase(Locale.getDefault());
			if(s0.indexOf("true")!=-1){
				return true;
			}else if(s0.indexOf("false")!=-1){
				return false;
			}
		}catch(Exception e) {
			
		}
		return defalutData;
	}
	public double getConfigDataDouble(String s){
		return getConfigDataDouble(s,miss);
	}
	public double getConfigDataDouble(String s,double DD){
		try {
			return Double.parseDouble(getConfigDataString(s));
		}catch(Exception e) {
			
		}
		return DD;
	}
	public long getConfigDataLong(String s){
		return getConfigDataLong(s,miss);
	}
	public long getConfigDataLong(String s,long DD){
		try {
			return Long.parseLong(getConfigDataString(s));
		}catch(Exception e) {
			
		}
		return DD;
	}
	public int getConfigDataInt(String s){
		return getConfigDataInt(s,miss);
	}
	public int getConfigDataInt(String s,int DD){
		try {
			return Integer.parseInt(getConfigDataString(s));
		}catch(Exception e) {
			
		}
		return DD;
	}
	public boolean setConfigDataBoolean(String s,boolean b){
		return setConfigDataString(s,b?"true":"false");
	}
	public boolean setConfigDataDouble(String s,double Data){
		return setConfigDataString(s,Double.toString(Data));
	}
	public boolean setConfigDataLong(String s,long Data){
		return setConfigDataString(s,Long.toString(Data));
	}
	public boolean setConfigDataInt(String s,int Data){
		return setConfigDataString(s,Integer.toString(Data));
	}
	public abstract void remove(String cate);
	public abstract boolean read(File file);
	public boolean contains(String s) {
		return getConfigDataString(s,null)==null?false:true;
	}
}
