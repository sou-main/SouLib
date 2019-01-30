package soulib.windowLib.CanvasComponents;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import soulib.windowLib.CommonGraphics;

public class CScrollbar extends CanvasComponent{
	private ScrollButton Up;
	private ScrollButton Bar;
	private ScrollButton Frame;
	private ScrollButton Down;
	private thread thread;
	/** カーソルの位置(単位px) */
	protected int cursor;
	/** true有効 */
	private boolean active=true;
	/** true横向き */
	protected final boolean yoko;
	/** 太さ */
	private int width;
	/** 矢印と枠の隙間 */
	private int space=2;
	/** このオブジェクトが選択されているか */
	private boolean celect;
	/** クリックされた場所とオブジェクト中央の差(X座標) */
	private int clickX;
	/** クリックされた場所とオブジェクト中央の差(Y座標) */
	private int clickY;
	/** 矢印ボタン1回で動くカーソルの量(px) */
	private int moveBlock=5;

	public int getMoveBlock(){
		return this.moveBlock;
	}
	public void setMoveBlock(int moveBlock){
		this.moveBlock=moveBlock;
	}
	/**
	 * 最低値と最高値の間に調整して返す。
	 *
	 * @param min 最低値
	 * @param max 最高値
	 */
	public int getFomat(int min,int max){
		double p=(double)getCursor()/getMax();
		int d=(int)(p*(max-min));
		return d+min;
	}
	public CScrollbar(){
		this(false);
	}
	/**
	 * @param b 横向き
	 */
	public CScrollbar(boolean b){
		yoko=b;
		Up=new ScrollButton(b?ScrollButton.muki_Left:ScrollButton.muki_Up);
		Bar=new ScrollButton();
		Frame=new ScrollButton(){
			@Override
			public void mousePressed(MouseEvent e){
				super.mousePressed(e);
				if(isInside(e.getPoint())){
					setCursor(e.getPoint());
				}
			}
		};
		Frame.draw3d=false;
		Down=new ScrollButton(b?ScrollButton.muki_Right:ScrollButton.muki_Down);
		setBackground(Color.lightGray);
		setColor(Color.gray);
		setBarSize(50);
		setW(20);
		setH(200);
		setCursor(0);
		setActive(true);
	}
	protected void setCursor(Point p){
		if(yoko) setCursor(p.x-getX()-Bar.getW()/2-width);
		else setCursor(p.y-getY()-Bar.getH()/2-width);
	}
	/** 見た目の幅を変更 */
	@Override
	public void setW(int w){
		width=w;
		Up.setW(w);
		Down.setW(w);
		Up.setH(w);
		Down.setH(w);
		if(yoko){
			Frame.setW(h-w*2);
			Bar.setH(w);
		}else{
			Frame.setW(w);
			Bar.setW(w);
		}
		setH(h);
		space=w/10;
	}
	/** 見た目の幅を取得 */
	public int getWidth(){
		return width;
	}
	/** 内部的な幅を取得 */
	@Override
	public int getW(){
		return yoko?h:width;
	}
	/** 見た目の長さを取得 */
	public int getHeight(){
		return h;
	}
	/** 内部的な高さを取得 */
	@Override
	public int getH(){
		return yoko?width:h;
	}
	/** 見た目の長さを変更(ボタンを含む) */
	@Override
	public void setH(int max){
		this.h=max;
		if(yoko){
			super.setW(max);
			Frame.setW(max-width*2);
			Frame.setH(width);
		}else{
			super.setW(width);
			Frame.setW(width);
			Frame.setH(max-width*2);
		}
		setX(getX());
		setY(getY());
	}
	/** 操作する部分の長さを変更 */
	public void setBarSize(int size){
		if(yoko) Bar.setW(size);
		else Bar.setH(size);
	}
	public int getBarSize(){
		if(yoko) return Bar.getW();
		else return Bar.getH();
	}
	public int getSpace(){
		return this.space;
	}
	public void setSpace(int space){
		this.space=space;
	}
	@Override
	public void setY(int y){
		super.setY(y);
		Up.setY(y);
		if(yoko){
			Frame.setY(y);
			Down.setY(y);
			Bar.setY(y);
		}else{
			Bar.setY(y+cursor+width);
			Frame.setY(y+width);
			Down.setY(y+getH()-width);
		}
	}
	@Override
	public void setX(int x){
		super.setX(x);
		Up.setX(x);
		if(yoko){
			Down.setX(x+h-width);
			Frame.setX(x+width);
			Bar.setX(x+cursor+width);
		}else{
			Frame.setX(x);
			Down.setX(x);
			Bar.setX(x);
		}
	}
	public int getCursor(){
		return this.cursor;
	}
	public int getMax(){
		return getHeight()-width*2-(yoko?Bar.getW():Bar.getH());
	}
	public void setMax(int max){
		setH(max+width*2+(yoko?Bar.getW():Bar.getH()));
	}
	public synchronized void addCursor(int cursor){
		setCursor(getCursor()+cursor);
	}
	public synchronized void setCursor(int cursor){
		if(cursor<0){
			cursor=0;
		}else if(cursor>getMax()){
			cursor=getMax();
		}
		this.cursor=cursor;
		if(yoko) Bar.setX(getX()+cursor+width);
		else Bar.setY(getY()+cursor+width);
	}
	public boolean isActive(){
		return this.active;
	}
	public void setActive(boolean active){
		Up.setColor(active?Color.black:Color.gray);
		Down.setColor(active?Color.black:Color.gray);
		this.active=active;
	}
	public boolean isCelect(){
		return celect;
	}
	public void setCelect(boolean celect){
		this.celect=celect;
	}
	@Override
	public void setColor(Color c){
		super.setColor(c);
		Bar.setBackground(c);
	}
	@Override
	public void setBackground(Color c){
		super.setBackground(c);
		Frame.setBackground(c);
		Up.setBackground(c);
		Down.setBackground(c);
	}
	@Override
	public void draw(CommonGraphics g,Point p){
		if(Up.active)Up.draw(g,p);
		if(Frame.active)Frame.draw(g,p);
		if(active){
			Point p0=new Point(p);
			p0.translate(clickX,clickY);
			if(Bar.active) {
				if(Bar.Pressed) setCursor(p0);
				Bar.draw(g,p);
			}
		}
		if(Down.active)Down.draw(g,p);
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		if(celect) {
			addCursor(e.getWheelRotation()*moveBlock);
			e.consume();
		}
	}
	@Override
	public void mousePressed(MouseEvent e){
		if(active){
			celect=isInside(e.getPoint());
			if(Up.active)Up.mousePressed(e);
			if(Bar.active)Bar.mousePressed(e);
			if(Down.active)Down.mousePressed(e);
			if(!e.isConsumed()&&Frame.active) Frame.mousePressed(e);
		}
	}
	@Override
	public void mouseClicked(MouseEvent e){
		if(active){
			if(Up.active)Up.mouseClicked(e);
			if(Bar.active)Bar.mouseClicked(e);
			if(Down.active)Down.mouseClicked(e);
			if(!e.isConsumed()&&Frame.active) Frame.mouseClicked(e);
		}
	}
	@Override
	public void mouseReleased(MouseEvent e){
		if(active){
			if(Up.active)Up.mouseReleased(e);
			if(Bar.active)Bar.mouseReleased(e);
			if(Down.active)Down.mouseReleased(e);
			if(!e.isConsumed()&&Frame.active) Frame.mouseReleased(e);
		}
	}
	@Override
	public void keyPressed(KeyEvent e){
		if(active){
			if(Up.active)Up.keyPressed(e);
			if(Bar.active)Bar.keyPressed(e);
			if(Down.active)Down.keyPressed(e);
			if(!e.isConsumed()&&Frame.active) Frame.keyPressed(e);
		}
	}
	@Override
	public void keyReleased(KeyEvent e){
		if(active){
			if(Up.active)Up.keyReleased(e);
			if(Bar.active)Bar.keyReleased(e);
			if(Down.active)Down.keyReleased(e);
			if(!e.isConsumed()&&Frame.active) Frame.keyReleased(e);
		}
	}
	@Override
	protected void finalize(){
		if(thread!=null) thread.interrupt();
	}

	private class thread extends Thread{
		private boolean obj;
		public thread(){
			super("ScrollRoopPressed");
		}
		@Override
		public void run(){
			while(true){
				try{
					Thread.sleep(400);
				}catch(InterruptedException e1){
					//e1.printStackTrace();
				}
				while(Up.Pressed||Down.Pressed){
					if(obj) addCursor(-moveBlock);
					else addCursor(moveBlock);
					try{
						Thread.sleep(60);
					}catch(InterruptedException e1){
						//e1.printStackTrace();
					}
				}
				try{
					Thread.sleep(3000);
					break;
				}catch(InterruptedException e){

				}
			}
			thread=null;
		}
	}
	public class ScrollButton extends CButton{
		private static final int muki_None=-1;
		/** 上 */
		private static final int muki_Up=0;
		/** 下 */
		private static final int muki_Down=1;
		/** 右 */
		private static final int muki_Right=2;
		/** 左 */
		private static final int muki_Left=3;
		private final int muki;
		private boolean Pressed;

		public ScrollButton(){
			this(muki_None);
		}
		public ScrollButton(int muki){
			this.muki=muki;
			setColor(active?Color.black:Color.gray);
			this.newthread=false;
			draw3d=true;
		}
		@Override
		protected void drawText(CommonGraphics g){
			Polygon p=new Polygon();
			if(muki==muki_Up){
				p.addPoint(getX()+getW()/2,getY()+space);
				p.addPoint(getX()+space,getY()+getH()-space);
				p.addPoint(getX()+getW()-space,getY()+getH()-space);
			}else if(muki==muki_Down){
				p.addPoint(getX()+space,getY()+space);
				p.addPoint(getX()+getW()/2,getY()+getH()-space);
				p.addPoint(getX()+getW()-space,getY()+space);
			}else if(muki==muki_Right){
				p.addPoint(getX()+space,getY()+space);
				p.addPoint(getX()+space,getY()+getH()-space);
				p.addPoint(getX()+getW()-space,getY()+getH()/2);
			}else if(muki==muki_Left){
				p.addPoint(getX()+space,getY()+getH()/2);
				p.addPoint(getX()+getW()-space,getY()+getH()-space);
				p.addPoint(getX()+getW()-space,getY()+space);
			}
			if(muki!=muki_None) g.fillPolygon(p);
		}
		@Override
		public void mousePressed(MouseEvent e){
			super.mousePressed(e);
			if(isInside(e.getPoint())){
				Pressed=true;
				clickX=getX()-e.getX()+getW()/2;
				clickY=getY()-e.getY()+getH()/2;
				e.consume();
				if(equals(Up)||equals(Down)){
					sc();
				}
			}
		}
		protected void sc(){
			if(equals(Up)) addCursor(-moveBlock);
			else addCursor(moveBlock);
			if(thread==null){
				thread=new thread();
				thread.obj=equals(Up);
				thread.start();
			}else{
				thread.obj=equals(Up);
				thread.interrupt();
			}
		}
		@Override
		protected void drawPressed(CommonGraphics g){
			if(!equals(Frame)) super.drawPressed(g);
		}
		@Override
		public void mouseClicked(MouseEvent e){
			super.mouseClicked(e);
			if(isInside(e.getPoint())) e.consume();
		}
		@Override
		public void mouseReleased(MouseEvent e){
			super.mouseReleased(e);
			Pressed=false;
			if(thread!=null) thread.interrupt();
			// if(isInside(e.getPoint()))e.consume();
		}
		@Override
		public int getW(){
			return w;
		}
		@Override
		public int getH(){
			return h;
		}
	}
	public int getFrameSize(){
		return yoko?Frame.getW():Frame.getH();
	}
	public ScrollButton getUp(){
		return Up;
	}
	public ScrollButton getBar(){
		return Bar;
	}
	public ScrollButton getFrame(){
		return Frame;
	}
	public ScrollButton getDown(){
		return Down;
	}
}
