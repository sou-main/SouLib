package soulib.logLib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;

import soulib.fileLib.FileEditor;
import soulib.lib.DataEditor;
import soulib.windowLib.CommonGraphics;
import soulib.windowLib.FontManager;
import soulib.windowLib.WindowBase;

public class WindowLog{
	private FontManager font;
	public ArrayList<LogData> DataList;
	/**<pre>リストの最初に追加するか
	 * 最後に追加するか
	 * true 最初
	 * false 最後</pre>*/
	public boolean Gyaku=true;
	public GameLog gl=null;
	private int startX;
	private int startY;
	private Color color;
	public WindowLog(WindowBase sw){
		this(sw,200);
	}
	public WindowLog(WindowBase sw,int buf){
		DataList=new ArrayList<LogData>(buf);
		if(sw==null)return;
		sw.setWindowlog(this);
	}
	/**別でウィンドウに関連付けする必要がある*/
	public WindowLog() {
		DataList=new ArrayList<LogData>();
	}
	{
		font=new FontManager();
	}
	public void addData(Throwable t){
		//String[] X = FileEditor.FE.StringToStrings(t.getMessage());
		String[] X =FileEditor.ThrowableToStrings(t);
		for(int i=0;i<X.length;i++){
			if(X[i]!=null)addData(X[i]);
		}
	}
	public WindowLog addData(String[] Data) {
		for(int i=0;i<Data.length;i++){
			addData(Data[i]);
		}
		return this;
	}
	public WindowLog addData(String Data){
		return addData(Data,null);
	}
	public WindowLog addData(String Data,Color c){
		LogData ld = new LogData(Data, null,c);
		ld.date=System.currentTimeMillis();
		if(Gyaku)DataList.add(0,ld);
		else DataList.add(ld);
		if(gl!=null)gl.log(Data);
		return this;
	}
	public WindowLog addData(LogData log){
		if(Gyaku)DataList.add(0,log);
		else DataList.add(log);
		if(gl!=null)gl.log(log.toString());
		return this;
	}
	public void Log(Exception e){
		addData(e);
	}
	/**全てのログの内容を削除*/
	public void clear(){
		delAllLog();
	}
	public void delAllLog(){
		DataList=new ArrayList<LogData>();
	}
	public void setDataList(LogData[] dataList2){
		for(LogData ld:dataList2){
			DataList.add(ld);
		}
	}
	public void setDataListString(String[] dataList2){
		int l=DataList.size();
		int lnn=DataEditor.getLastNotNull(dataList2);
		if(l>lnn) {
			l=dataList2.length;
			for(int i=0;i<l;i++){
				if(dataList2[i]!=null)DataList.get(i).setData(dataList2[i]);
			}
		}
		else if(l<lnn) {
			for(int i=0;i<dataList2.length;i++){
				if(dataList2[i]!=null)DataList.add(new LogData(dataList2[i]));
			}
		}
	}
	public String[] getDataListString(){
		return LogData.toStrings(LogData.toLogDatas(DataList));
	}
	public LogData[] getDataList(){
		return LogData.toLogDatas(DataList);
	}
	/**CommonGraphicsに表示する*/
	public void draw(CommonGraphics g) {
		draw(g,null);
	}
	/**指定のウィンドウに<br>
	 * 表示可能な範囲で、<br>
	 * CommonGraphicsに表示する*/
	public void draw(CommonGraphics g,WindowBase b) {
		draw(g,b,0);
	}
	/**指定のウィンドウに表示可能な範囲で、<br>
	 * 指定の位置からCommonGraphicsに表示する*/
	public void draw(CommonGraphics g,WindowBase b,int start){
		draw(g,b!=null?b.getHeight():-1,start);
	}
	/**指定の位置までの範囲で、<br>
	 * 指定の位置からCommonGraphicsに表示する*/
	public void draw(CommonGraphics g,int h,int start) {
		draw(g,h,30,start);
	}
	public void draw(CommonGraphics g,int h,int x,int start) {
		if(DataList.size()<1)return;
		int nowColor=g.getColor().getRGB();
		g.setFont(font.get(Font.BOLD,DataList.get(0).fontSize));
		int fs=g.getFont().getSize();
		for(int i=0;i<DataList.size();i++) {
			LogData l=DataList.get(i);
			int y=(i+1)*l.fontSize+start+startY;
			if(y<-l.fontSize*2)continue;
			int color=l.getColorRaw();
			if(nowColor!=color) {
				g.setColor(new Color(color));
				nowColor=color;
			}
			if(fs!=l.fontSize) {
				g.setFont(font.get(Font.BOLD,l.fontSize));
				fs=l.fontSize;
			}
			g.drawString(l.toString(),x+startX,y);
			if(y>h)break;
		}
	}
	public void StartPoint(Point point){
		StartPoint(point.x,point.y);
	}
	public int getStartX(){
		return this.startX;
	}
	public int getStartY(){
		return this.startY;
	}
	public void StartPoint(int x,int y){
		StartX(x);
		StartY(y);
	}
	public void StartX(int x) {
		startX=x;
	}
	public void StartY(int y) {
		startY=y;
	}
	public void println(String string){
		addData(string,color);
	}
	public void println(Object x){
		println(String.valueOf(x));
	}
	public void println(int x){
		println(String.valueOf(x));
	}
	public void println(long x){
		println(String.valueOf(x));
	}
	public void println(char[] x){
		println(String.valueOf(x));
	}
	public void println(char x){
		println(String.valueOf(x));
	}
	public void println(double x){
		println(String.valueOf(x));
	}
	public void println(float x){
		println(String.valueOf(x));
	}
	public Color getColor(){
		return color;
	}
	public void setColor(Color color){
		this.color = color;
	}
	public FontManager getFontManager(){
		return font;
	}
}
