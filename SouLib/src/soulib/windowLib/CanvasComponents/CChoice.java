package soulib.windowLib.CanvasComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import soulib.windowLib.CommonGraphics;

public class CChoice extends CButton{

	private CPanel panel;
	/** このオブジェクトが選択されているか */
	private int celect;
	private boolean open;
	private int view=6;
	private CScrollbar bar;

	public CChoice(){
		super();
		newthread=false;
		autoSize=false;
		bar=new CScrollbar();
		panel=new CPanel(){
			@Override
			public void ReSize(){
				super.ReSize();
				Resize(getW());
			}
		};
		panel.setBackground(new Color(0,0,0,0));
		AutoPriority();
	}
	public void AutoPriority(){
		this.setPriority(10000-getY());
	}
	@Override
	public void setPriority(int priority){
		super.setPriority(priority);
		panel.setPriority(priority);
		bar.setPriority(priority);
	}
	private void Resize(int w){
		int barsize=bar.getFrameSize()-getH()/2*(panel.getComponents()-view);
		if(barsize>0) bar.setBarSize(barsize);
		bar.setH(getH()*view);
		bar.setPoint(getX()+w,getY()+getH());
		int w0=w+bar.getW();
		if(w0>getW()) setW(w0);
	}
	@Override
	public void setW(int w){
		if(getW()!=w){
			CanvasComponent[] cc=panel.getCanvasComponent();
			for(int i=0;i<cc.length;i++)
				cc[i].setW(w);
		}
		super.setW(w);
	}
	@Override
	public void setH(int h){
		if(getH()!=h){
			CanvasComponent[] cc=panel.getCanvasComponent();
			for(int i=0;i<cc.length;i++)
				cc[i].setW(h);
		}
		super.setH(h);
	}
	@Override
	protected void drawText(CommonGraphics g){
		g.setFont(getFont());
		int w0=g.drawString(name,getX()+5,getY()+fs)+10;
		if(w0>getW()) setW(w0);
	}
	@Override
	public void draw(CommonGraphics g,Point p){
		super.draw(g,p);
		if(open){
			bar.setActive(panel.getComponents()>view);
			if(bar.isActive()){
				bar.setCelect(open);
				double p0=(double)getH()/(panel.getComponents()*getH());
				bar.setMoveBlock((int)(bar.getFrameSize()*p0+0.5));
				bar.draw(g,p);
				int b=bar.getFomat(0,panel.getComponents()-view);
				panel.setActiveMin(b);
				panel.setActiveMax(panel.getActiveMin()+view-1);
				panel.setY(getY()+b*-getH()+getH());
			}
			panel.draw(g,p);
		}
	}
	public int getCelect(){
		return this.celect;
	}
	public void setCelect(int celect){
		this.celect=celect;
		CanvasComponent[] cc=panel.getCanvasComponent();
		if(cc.length>celect)name=cc[celect].toString();
	}
	@Override
	public void setX(int x){
		super.setX(x);
		panel.setX(x);
	}
	@Override
	public void setY(int y){
		super.setY(y);
		panel.setY(y+getH());
		this.AutoPriority();
	}
	public CPanel getPanel(){
		return panel;
	}
	public void add(String s){
		if(s==null) return;
		if(s.isEmpty()) return;
		final bt bt=new bt(s);
		bt.draw3d=false;
		bt.ue=true;
		bt.setFont(getFont());
		bt.addActionListener(actionListener);
		bt.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				setCelect(bt.id);
			}
		});
		bt.id=panel.getComponents();
		panel.addCanvasComponent(bt,getX(),panel.getH());
		panel.setPriority(getPriority());
		panel.ReSize();
	}
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if(panel==null)return;
		CanvasComponent[] cc=panel.getCanvasComponent();
		int y=0;
		for(int i=0;i<cc.length;i++) {
			cc[i].setFont(font);
			cc[i].setY(y);
			y+=cc[i].getH();
		}
		panel.ReSize();
	}

	private class bt extends CButton{
		protected int id;
		public bt(String s){
			super(s);
		}
	}
	@Override
	protected void click(){

	}
	@Override
	public void mousePressed(MouseEvent e){
		this.AutoPriority();
		super.mousePressed(e);
		if(open){
			panel.mousePressed(e);
			bar.mousePressed(e);
			e.consume();
		}
	}
	@Override
	public void mouseClicked(MouseEvent e){
		super.mouseClicked(e);
		if(open){
			panel.mouseClicked(e);
			bar.mouseClicked(e);
		}
		if(!e.isConsumed()){
			boolean flag=open;
			open=isInside(e.getPoint());
			if(flag)e.consume();
		}
	}
	@Override
	public void mouseReleased(MouseEvent e){
		super.mouseReleased(e);
		if(open){
			panel.mouseReleased(e);
			bar.mouseReleased(e);
		}
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		super.mouseWheelMoved(e);
		if(open){
			panel.mouseWheelMoved(e);
			bar.mouseWheelMoved(e);
		}
	}
	public int getView(){
		return view;
	}
	public void setView(int view){
		this.view=view;
	}
	@Override
	public int getW(){
		return w;
	}
}
