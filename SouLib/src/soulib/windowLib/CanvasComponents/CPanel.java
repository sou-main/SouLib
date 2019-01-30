package soulib.windowLib.CanvasComponents;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import soulib.windowLib.CanvasComponentManager;
import soulib.windowLib.CommonGraphics;

public class CPanel extends CanvasComponent{

	public CanvasComponentManager ccm;
	public boolean move;
	public boolean frame;
	private boolean celect;
	private int celectX;
	private int celectY;
	private String name;

	public CPanel(){
		this(false,false);
	}
	public CPanel(boolean frame){
		this(frame,false);
	}
	public CPanel(boolean frame,boolean move){
		this.frame=frame;
		this.move=move;
		ccm=new CanvasComponentManager();
		setBackground(new Color(0,0,0,0));
		setColor(Color.black);
	}
	@Override
	public void draw(CommonGraphics g,Point p){
		if(!active)return;
		ReSize();
		if(getBackground().getAlpha()>=0) {
			g.setColor(getBackground());
			g.fillRect(getX(),getY(),getW(),getH());
		}
		ccm.draw(g,p);
		if(frame){
			g.setColor(getColor());
			g.drawRect(getX(),getY(),getW(),getH());
		}
		if(celect&&move&&p.x>0&&p.y>0){
			Point p2=new Point(p);
			p2.translate(celectX,celectY);
			setPoint(p2);
		}
	}
	/**現在のデータに合わせて大きさを取得*/
	public int[] getSize(){
		int[] size=new int[2];
		CanvasComponent[] ccs=ccm.getCanvasComponent();
		for(int i=0;i<ccs.length;i++){
			int w=ccs[i].getX()+ccs[i].getW();
			int h=ccs[i].getY()+ccs[i].getH();
			if(getX()+size[0]<w) size[0]=w-getX();
			if(getY()+size[1]<h) size[1]=h-getY();
		}
		return size;
	}
	@Override
	public void setPoint(int x,int y){
		if(x==this.x&&y==this.y) return;
		CanvasComponent[] ccs=ccm.getCanvasComponent();
		for(int i=0;i<ccs.length;i++)
			if(ccs[i]!=null) ccs[i].movePoint(-this.getX(),-this.getY());
		super.setX(x);
		super.setY(y);
		for(int i=0;i<ccs.length;i++)
			if(ccs[i]!=null) ccs[i].movePoint(x,y);
	}
	@Override
	public void setY(int y){
		setPoint(getX(),y);
	}
	@Override
	public void setX(int x){
		setPoint(x,getY());
	}
	public void ReSize(){
		int[] size=getSize();
		setSize(size[0],size[1]);
	}
	/** 複数回呼び出すより効率が良い */
	public void addCanvasComponent(CanvasComponent[] cc){
		ccm.addCanvasComponent(cc);
		ReSize();
	}
	public void addCanvasComponent(CanvasComponent cc,int x,int y){
		ccm.addCanvasComponent(cc,x,y);
		ReSize();
	}
	public void addCanvasComponent(CanvasComponent cc){
		ccm.addCanvasComponent(cc);
		ReSize();
	}
	public CanvasComponent[] getCanvasComponent(){
		return ccm.getCanvasComponent();
	}
	public void removeAllCanvasComponent(){
		ccm.removeAllCanvasComponent();
		ReSize();
	}
	public void removeCanvasComponent(CanvasComponent cc){
		ccm.removeCanvasComponent(cc);
		ReSize();
	}
	public void removeCanvasComponent(CanvasComponent[] cc){
		ccm.removeCanvasComponent(cc);
		ReSize();
	}
	@Override
	public void mouseReleased(MouseEvent e){
		if(!active)return;
		ccm.mouseReleased(e);
		celect=false;
	}
	@Override
	public void mousePressed(MouseEvent e){
		if(!active)return;
		ccm.mousePressed(e);
		if(!e.isConsumed()&&isInside(e.getPoint())){
			celect=true;
			celectX=getX()-e.getX();
			celectY=getY()-e.getY();
		}
	}
	@Override
	public void mouseClicked(MouseEvent e){
		if(!active)return;
		ccm.mouseClicked(e);
	}
	@Override
	public void keyPressed(KeyEvent e){
		if(!active)return;
		ccm.keyPressed(e);
	}
	@Override
	public void keyReleased(KeyEvent e){
		if(!active)return;
		ccm.keyReleased(e);
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		if(!active)return;
		ccm.mouseWheelMoved(e);
	}
	public int getComponents(){
		return ccm.getCanvasComponent().length;
	}
	public int getActiveMin(){
		return ccm.getActiveMin();
	}
	public void setActiveMin(int activeMin){
		ccm.setActiveMin(activeMin);
	}
	public int getActiveMax(){
		return ccm.getActiveMax();
	}
	public void setActiveMax(int activeMax){
		ccm.setActiveMax(activeMax);
	}
	@Override
	public void setPriority(int priority){
		super.setPriority(priority);
		ccm.setPriority(priority);
	}
	public void setCanvasComponent(CanvasComponent[] cc){
		ccm.setCanvasComponent(cc);
	}
	public void sort(){
		ccm.sort();
	}
	public boolean isEmpty(){
		return ccm.isEmpty();
	}
	public boolean contains(CanvasComponent cc){
		return ccm.contains(cc);
	}
	public int getIndex(CanvasComponent cc){
		return ccm.getIndex(cc);
	}
	public void setBackground(Color c){
		super.setBackground(c);
		//ccm.setBackground(c);
	}
	public void setColor(Color c){
		ccm.setColor(c);
	}
	public String toString(){
		if(name!=null)return name;
		return ccm.toString();
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
}
