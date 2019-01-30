package soulib.windowLib;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;

import soulib.windowLib.CanvasComponents.CanvasComponent;

public class CanvasComponentManager{

	protected CanvasComponent[] ccs;
	private int activeMin;
	private int activeMax;

	public CanvasComponentManager(){
		ccs=new CanvasComponent[0];
		activeMax=10000;
	}
	public int getActiveMin(){
		return this.activeMin;
	}
	public void setActiveMin(int activeMin){
		this.activeMin=activeMin;
	}
	public int getActiveMax(){
		return this.activeMax;
	}
	public void setActiveMax(int activeMax){
		this.activeMax=activeMax;
	}
	/** 現在のコンポーネントを破棄して引数のコピーを新しく設定する */
	public void setCanvasComponent(CanvasComponent[] cc){
		ccs=Arrays.copyOf(cc,cc.length);
		Arrays.sort(ccs);
	}
	/** 複数回呼び出すより効率が良い */
	public void addCanvasComponent(CanvasComponent[] cc){
		final int oldL=ccs.length;
		ccs=Arrays.copyOf(ccs,ccs.length+cc.length);
		for(int i=0;i<cc.length;i++){
			ccs[oldL+i]=cc[i];
		}
		Arrays.sort(ccs);
	}
	public void addCanvasComponent(CanvasComponent cc,int x,int y){
		cc.setPoint(x,y);
		addCanvasComponent(cc);
	}
	public void addCanvasComponent(CanvasComponent cc){
		if(contains(cc))return;
		ccs=Arrays.copyOf(ccs,ccs.length+1);
		ccs[ccs.length-1]=cc;
		Arrays.sort(ccs);
	}
	public void sort() {
		Arrays.sort(ccs);
	}
	public CanvasComponent[] getCanvasComponent(){
		return ccs;
	}
	public boolean isEmpty() {
		return ccs.length<1;
	}
	public void removeAllCanvasComponent(){
		ccs=new CanvasComponent[0];
	}
	public boolean contains(CanvasComponent cc) {
		return getIndex(cc)>=0;
	}
	public int getIndex(CanvasComponent cc) {
		for(int i=0;i<ccs.length;i++) {
			if(cc==null) {
				if(cc==ccs[i])return i;
			}else if(cc.equals(ccs[i]))return i;
		}
		return -1;
	}
	public void removeCanvasComponent(CanvasComponent cc){
		int size=ccs.length;
		for(int i=0;i<ccs.length;i++){
			if(cc==null?ccs[i]==null:cc.equals(ccs[i])){
				ccs[i]=null;
				size--;
				for(int j=i;j<ccs.length-1;j++){
					try{
						ccs[j]=ccs[j+1];
					}catch(Throwable t){
						t.printStackTrace();
					}
				}
				break;
			}
		}
		CanvasComponent[] buf=new CanvasComponent[size];
		for(int i=0;i<buf.length;i++) {
			buf[i]=ccs[i];
		}
		ccs=buf;
	}
	public void removeCanvasComponent(CanvasComponent[] cc){
		for(int i=0;i<cc.length;i++)removeCanvasComponent(cc[i]);
	}
	public void mouseReleased(MouseEvent e){
		if(ccs.length>0) for(int i=activeMin;i<ccs.length;i++){
			if(i>activeMax) break;
			if(ccs[i]!=null) ccs[i].mouseReleased(e);
			if(e.isConsumed()) break;
		}
	}
	public void mousePressed(MouseEvent e){
		if(ccs.length>0) for(int i=activeMin;i<ccs.length;i++){
			if(i>activeMax) break;
			if(ccs[i]!=null) ccs[i].mousePressed(e);
			if(e.isConsumed()) break;
		}
	}
	public void mouseClicked(MouseEvent e){
		if(ccs.length>0) for(int i=activeMin;i<ccs.length;i++){
			if(i>activeMax) break;
			if(ccs[i]!=null) ccs[i].mouseClicked(e);
			if(e.isConsumed()) break;
		}
	}
	public void draw(CommonGraphics g,Point p){
		int i=ccs.length-1;
		if(i>activeMax) i=activeMax;
		if(ccs.length>0) for(;i>-1;i--){
			if(i<activeMin) break;
			if(ccs[i]!=null) ccs[i].draw(g,p);
		}
	}
	public void keyPressed(KeyEvent e){
		if(ccs.length>0) for(int i=activeMin;i<ccs.length;i++){
			if(i>activeMax) break;
			if(ccs[i]!=null) ccs[i].keyPressed(e);
			if(e.isConsumed()) break;
		}
	}
	public void keyReleased(KeyEvent e){
		if(ccs.length>0) for(int i=activeMin;i<ccs.length;i++){
			if(i>activeMax) break;
			if(ccs[i]!=null) ccs[i].keyReleased(e);
			if(e.isConsumed()) break;
		}
	}
	public void setBackground(Color c){
		if(ccs.length>0) for(int i=activeMin;i<ccs.length;i++){
			if(i>activeMax) break;
			if(ccs[i]!=null) ccs[i].setBackground(c);
		}
	}
	public void setColor(Color c){
		if(ccs.length>0) for(int i=activeMin;i<ccs.length;i++){
			if(i>activeMax) break;
			if(ccs[i]!=null) ccs[i].setColor(c);
		}
	}
	public void mouseWheelMoved(MouseWheelEvent e){
		if(ccs.length>0) for(int i=activeMin;i<ccs.length;i++){
			if(i>activeMax) break;
			if(ccs[i]!=null) ccs[i].mouseWheelMoved(e);
			if(e.isConsumed()) break;
		}
	}
	public void setPriority(int priority){
		for(int i=0;i<ccs.length;i++) {
			if(ccs[i]!=null) ccs[i].setPriority(priority);
		}
	}
	public int size(){
		return ccs.length;
	}
}
