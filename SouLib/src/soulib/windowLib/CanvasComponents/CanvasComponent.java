package soulib.windowLib.CanvasComponents;

import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import soulib.windowLib.CommonGraphics;
import soulib.windowLib.WindowBase;
import soulib.windowLib.WindowLib;

public abstract class CanvasComponent implements Comparable<CanvasComponent>{
	private Color Background;
	private Color color;
	protected int x;
	protected int y;
	protected int w;
	protected int h;
	private Font font;
	public boolean autoSize;
	protected ActionListener actionListener;
	protected WindowBase win;
	private int priority;
	public boolean active=true;
	public static Font defaultFont=WindowLib.getPLAIN_Font(15);

	public int getPriority(){
		return this.priority;
	}

	public void setPriority(int priority){
		this.priority=priority;
	}
	/**
	 * フォントサイズ=15<br>
	 * 自動大きさ調整=有効<br>
	 * 背景色=白<br>
	 * 色=黒<br>
	 * の設定で新しいコンポーネントを作成します。
	 */
	public CanvasComponent(){
		font=defaultFont;
		autoSize=true;
		Background=Color.white;
		color=Color.black;
	}
	/** X座標を取得します。
	 * @return 現在のX座標
	 */
	public int getX(){
		return x;
	}
	/** Y座標を取得します。 
	 * @return 現在のY座標
	 */
	public int getY(){
		return y;
	}
	/** 幅を取得します。
	 * @return 現在の幅
	 */
	public int getW(){
		return w;
	}
	/** 高さを取得します。
	 * @return 現在の高さ
	 */
	public int getH(){
		return h;
	}
	/**
	 * 座標を変更します。
	 * @param p 新しい座標
	 */
	public void setPoint(Point p){
		setPoint(p.x,p.y);
	}
	/**
	 * 座標を変更します。
	 * @param x 新しいX座標
	 * @param y 新しいY座標
	 */
	public void setPoint(int x,int y){
		this.setX(x);
		this.setY(y);
	}
	/** フォントを変更します。
	 * @param font 新しいフォント
	 */
	public void setFont(Font font){
		this.font=font;
	}
	/** フォントを取得します。 
	 * 現在のフォント
	 */
	public Font getFont(){
		return font;
	}
	/**
	 * 色を取得します。<br>
	 * コンポーネントによって色の意味は異なります。
	 * @return 現在の色
	 */
	public Color getColor(){
		return color;
	}
	/**
	 * 色を変更します。<br>
	 * コンポーネントによって色の意味は異なります。
	 * @param c 新しい色
	 */
	public void setColor(Color c){
		color=c;
	}
	/**
	 * 背景色を取得します。<br>
	 * コンポーネントによって背景色の意味は異なります。
	 * @return 現在の背景色
	 */
	public Color getBackground(){
		return Background;
	}
	/**
	 * 背景色を変更します。<br>
	 * コンポーネントによって背景色の意味は異なります。
	 * @param c 新しい背景色
	 */
	public void setBackground(Color c){
		Background=c;
	}
	public void actionPerformed(ActionEvent e){
		if(actionListener!=null) actionListener.actionPerformed(e);
	}
	/**
	 * 指定したアクションリスナーを追加します。<br>
	 * @param l 追加対象 nullの場合は何もしない。
	 */
	public synchronized void addActionListener(ActionListener l){
		if(l==null){
			return;
		}
		actionListener=AWTEventMulticaster.add(actionListener,l);
	}
	/**
	 * 指定したアクションリスナーを削除します。<br>
	 * @param l 削除対象 nullの場合は何もしない。
	 */
	public synchronized void removeActionListener(ActionListener l){
		if(l==null){
			return;
		}
		actionListener=AWTEventMulticaster.remove(actionListener,l);
	}
	/**
	 * 点が内側に存在するか判定します。
	 * @param p 判定する点
	 * @return 内側に存在する場合true
	 */
	public boolean isInside(Point p){
		if(!active)return false;
		if(getX()<p.x&&getX()+getW()>p.x)
			if(getY()<p.y&&getY()+getH()>p.y){
			return true;
			}
		return false;
	}
	/**
	 * 幅と高さを変更します。
	 * @param w 幅
	 * @param h 高さ
	 */
	public void setSize(int w,int h){
		setW(w);
		setH(h);
	}
	/**
	 * 座標を移動させます。<br>
	 * 新しい座標は、<br>
	 * <b>X座標=</b>getX()+x2<br>
	 * <b>Y座標=</b>getY()+y2<br>
	 * になります。
	 * @param x2 移動する分のX座標
	 * @param y2 移動する分のY座標
	 */
	public void movePoint(int x2,int y2){
		setX(getX()+x2);
		setY(getY()+y2);
	}
	/** Y座標を変更します。 
	 * @param y 新しいY座標
	 */
	public void setY(int y){
		this.y=y;
	}
	/** X座標を変更します。
	 * @param x 新しいX座標
	 */
	public void setX(int x){
		this.x=x;
	}
	/** 幅を変更します。 
	 * @param w 新しい幅
	 */
	public void setW(int w){
		this.w=w;
	}
	/** 高さを変更します。
	 * @param h 新しい高さ
	 */
	public void setH(int h){
		this.h=h;
	}
	/** 優先順位で並べ替えます。 */
	@Override
	public int compareTo(CanvasComponent s){
		return s.priority-priority;
	}
	/**
	 * このコンポーネントを表示します。
	 * @param p カーソルの位置
	 * @param g 表示対象
	 */
	public abstract void draw(CommonGraphics g,Point p);
	/** マウスのボタンを押した時に呼ばれます。
	 * @param e イベント*/
	public void mousePressed(MouseEvent e){}
	/** マウスのボタンを押して離した(クリックした)時呼ばれます。
	 * @param e イベント*/
	public void mouseClicked(MouseEvent e){}
	/** マウスのボタンを離した時に呼ばれます。
	 * @param e イベント*/
	public void mouseReleased(MouseEvent e){}
	/** キーボードのキーを押した時に呼ばれます。
	 * @param e イベント*/
	public void keyPressed(KeyEvent e){}
	/** キーボードのキーを離した時に呼ばれます。
	 * @param e イベント*/
	public void keyReleased(KeyEvent e){}
	/** マウスホイールを動かした時に呼ばれます。
	 * @param e イベント*/
	public void mouseWheelMoved(MouseWheelEvent e){}
}
