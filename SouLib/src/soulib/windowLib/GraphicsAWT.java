package soulib.windowLib;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;

public class GraphicsAWT implements CommonGraphics{

	public CanvasWindow win;
	public Graphics2D g;
	private int z;
	/**Graphics2Dにキャストします*/
	public GraphicsAWT(Graphics g){
		this((Graphics2D)g);
	}
	public GraphicsAWT(Graphics2D g){
		this.g=g;
	}
	public GraphicsAWT setWindow(CanvasWindow win) {
		this.win=win;
		return this;
	}
	@Override
	public String getClassName(){
		return win.RenderingEngineName();
	}

	@Override
	public void setColor(Color c){
		g.setColor(c);
	}

	@Override
	public void drawOval(int x,int y,int w,int h){
		g.drawOval(x,y,w,h);
	}

	@Override
	public void fillOval(int x,int y,int w,int h){
		g.fillOval(x,y,w,h);
	}

	@Override
	public void drawRect(int x,int y,int w,int h){
		g.drawRect(x,y,w,h);
	}

	@Override
	public void fillRect(int x,int y,int w,int h){
		g.fillRect(x,y,w,h);
	}

	@Override
	public int drawString(String l,int x,int y){
		FontMetrics fm=g.getFontMetrics();
		int w=fm.stringWidth(l);//表示したい文字列の幅を取得
		if(y<0)return w;//表示開始位置が画面上端より上のとき表示しない
		if(win==null) {//ウィンドウの情報を取得できないときそのまま表示する
			g.drawString(l,x,y);
			return w;
		}
		int h=fm.getHeight();//表示したい文字列の高さを取得
		
		if(y>win.canHeight()+h)return w;//表示したいY座標が画面の下端より下のとき表示しない
		if(x>0&&win.canWidth()>x+w) {//すべて画面内に収まるときはそのまま表示
			g.drawString(l,x,y);
			return w;
		}
		int block=10;//まとめて処理する文字数
		//最初の表示終了文字数。合計文字数がブロックサイズより小さい時は合計文字数
		int endIndex=l.length()<block?l.length():block;
		int beginIndex=0;//表示開始文字数
		int w2=x;//表示開始X座標
		int pw;
		while(true) {//表示終了文字数が合計文字数
			String s=l.substring(beginIndex,endIndex);
			pw=fm.stringWidth(s);;
			if(w2+pw>0)g.drawString(s,w2,y);
			w2+=pw;
			if(w2>win.canWidth())break;
			beginIndex+=block;
			endIndex=beginIndex+block;
			if(beginIndex>=l.length())break;
			if(endIndex>l.length())endIndex=l.length();
		}
		return w;
	}

	@Override
	public void drawLine(int x1,int y1,int x2,int y2){
		g.drawLine(x1,y1,x2,y2);
	}

	@Override
	public void setBackground(Color color){
		g.setBackground(color);
	}

	@Override
	public Color getBackground(){
		return g.getBackground();
	}

	@Override
	public Color getColor(){
		return g.getColor();
	}

	@Override
	public Font getFont(){
		return g.getFont();
	}

	@Override
	public void setFont(Font font){
		g.setFont(font);
	}

	@Override
	public void Buffclear(){
		// TODO 自動生成されたメソッド・スタブ
	}

	@Override
	public void setZ(int z){
		this.z=z;
	}

	@Override
	public int getZ(){
		return z;
	}

	@Override
	public void fillOval(float x,float y,float w,float h){
		fillOval((int)x,(int)y,(int)w,(int)h);
	}

	@Override
	public void drawOval(float x,float y,float w,float h){
		drawOval((int)x,(int)y,(int)w,(int)h);
	}

	@Override
	public float drawString(String l,float x,float y){
		FontMetrics fm=g.getFontMetrics();
		int w=fm.stringWidth(l);//表示したい文字列の幅を取得
		if(y<0)return w;//表示開始位置が画面上端より上のとき表示しない
		if(win==null) {//ウィンドウの情報を取得できないときそのまま表示する
			g.drawString(l,x,y);
			return w;
		}
		int h=fm.getHeight();//表示したい文字列の高さを取得
		
		if(y>win.canHeight()+h)return w;//表示したいY座標が画面の下端より下のとき表示しない
		if(x>0&&win.canWidth()>x+w) {//すべて画面内に収まるときはそのまま表示
			g.drawString(l,x,y);
			return w;
		}
		int block=10;//まとめて処理する文字数
		//最初の表示終了文字数。合計文字数がブロックサイズより小さい時は合計文字数
		int endIndex=l.length()<block?l.length():block;
		int beginIndex=0;//表示開始文字数
		float w2=x;//表示開始X座標
		while(true) {//表示終了文字数が合計文字数
			String s=l.substring(beginIndex,endIndex);
			g.drawString(s,w2,y);
			w2+=fm.stringWidth(s);
			if(w2>win.canWidth())break;
			beginIndex+=block;
			endIndex=beginIndex+block;
			if(beginIndex>=l.length())break;
			if(endIndex>l.length())endIndex=l.length();
		}
		return w;
	}

	@Override
	public float getWidth(){
		return win.canWidth();
	}

	@Override
	public float getHeight(){
		return win.canHeight();
	}

	@Override
	public WindowBase getWindow(){
		return win;
	}

	@Override
	public void drawPolygon(Polygon p){
		g.drawPolygon(p);
	}

	@Override
	public void fillPolygon(Polygon p){
		g.fillPolygon(p);
	}

	@Override
	public void fill3DRect(int x,int y,int width,int height,boolean raised){
		g.fill3DRect(x,y,width,height,raised);
	}

	@Override
	public int getPaintWidth(String s){
		return g.getFontMetrics().stringWidth(s);
	}

	@Override
	public void fillRect(float x,float y,float w,float h){
		fillRect((int)x,(int)y,(int)w,(int)h);
	}

	@Override
	public void drawRect(float x,float y,float w,float h){
		drawRect((int)x,(int)y,(int)w,(int)h);
	}

	@Override
	public void drawLine(float x,float y,float w,float h){
		drawLine((int)x,(int)y,(int)w,(int)h);
	}
	@Override
	public void setColor(float[] c){
		Color color=null;
		if(c.length>3)color=new Color(c[0],c[1],c[2],c[3]);
		else if(c.length>2)color=new Color(c[0],c[1],c[2]);
		if(color==null)return;
		setColor(color);
	}
	@Override
	public void fillPolygon(int[] is,int[] is2,int i){
		g.fillPolygon(is,is2,i);
	}
	@Override
	public void del(){
		win.del();
	}
	@Override
	public void del(Color color){
		win.del(color);
	}
	@Override
	public void drawImage(Image img,int x,int y,int w,int h){
		g.drawImage(img,x,y,w,h,null);
	}
}
