package soulib.logLib;

import java.awt.Color;
import java.util.ArrayList;

public class LogData {
	public int fontSize=20;
	private String Data;
	private java.util.logging.Level level=null;
	private int color=Color.BLACK.getRGB();
	public long date;
	public LogData(String Data){
		this(Data,null,null);
	}
	public LogData(String Data,java.util.logging.Level level){
		this(Data,level,null);
	}
	public LogData(String Data,java.util.logging.Level level,Color color){
		this.Data=Data;
		this.level=level;
		if(color!=null)this.color=color.getRGB();
	}
	public int getColorRaw() {
		return color;
	}
	public Color getColor(){
		return new Color(color);
	}
	public void setData(String Data){
		this.Data=Data;
	}
	public void setLevel(java.util.logging.Level level){
		this.level=level;
	}
	/**toStringと同じ*/
	public String getData(){
		return toString();
	}
	public java.util.logging.Level getLevel(){
		return level;
	}
	/** ログの文字を返す
	 * @return ログの内容*/
	@Override
	public String toString(){
		return Data;
	}
	public static String[] toStrings(LogData[] dataList){
		String[] r=new String[dataList.length];
		for(int i=0;i<dataList.length;i++)if(dataList[i]!=null)r[i]=dataList[i].toString();
		return r;
	}
	public static LogData[] toLogDatas(String[] dataList2){
		LogData[] DataList=new LogData[dataList2.length];
		int l=DataList.length;
		if(l>dataList2.length)l=dataList2.length;
		for(int i=0;i<l;i++){
			DataList[i]=new LogData(dataList2[i]);
		}
		return DataList;
	}
	public static LogData[] toLogDatas(ArrayList<LogData> dataList){
		LogData[] DataList=new LogData[dataList.size()];
		for(int i=0;i<dataList.size();i++){
			DataList[i]=dataList.get(i);
		}
		return DataList;
	}
}
