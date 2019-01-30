package soulib.windowLib.CanvasComponents;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import soulib.windowLib.CommonGraphics;

public class CTree extends CanvasComponent{
	private CButton bt;
	private CPanel panel;
	public CTree() {
		this("");
	}
	public CTree(String label) {
		panel=new CPanel();
		bt=new CToggleButton() {
			@Override
			protected void click(){
				panel.active=getToggle();
			}
		};
		this.setLabel(label);
	}
	@Override
	public void draw(CommonGraphics g,Point p){
		if(!active)return;
		bt.draw(g,p);
		panel.draw(g,p);
	}
	@Override
	public void setX(int x) {
		super.setX(x);
		bt.setX(x);
		panel.setX(x);
	}
	@Override
	public void setY(int y) {
		super.setY(y);
		bt.setY(y);
		panel.setY(y+bt.getH());
	}
	@Override
	public void setH(int h) {
		super.setH(h);
		panel.setY(bt.getH()+h);
	}
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		bt.setFont(f);
		panel.setFont(f);
	}
	public String getLabel(){
		return bt.name;
	}
	public void setLabel(String label){
		bt.name = label;
	}
	@Override
	public void mouseClicked(MouseEvent e){
		super.mouseClicked(e);
		panel.mouseClicked(e);
		bt.mouseClicked(e);
	}
	@Override
	public void mouseReleased(MouseEvent e){
		super.mouseReleased(e);
		panel.mouseReleased(e);
		bt.mouseReleased(e);
	}
	@Override
	public void mousePressed(MouseEvent e){
		super.mousePressed(e);
		panel.mousePressed(e);
		bt.mousePressed(e);
	}
	@Override
	public void keyPressed(KeyEvent e){
		super.keyPressed(e);
		panel.keyPressed(e);
		bt.keyPressed(e);
	}
	@Override
	public void keyReleased(KeyEvent e){
		super.keyReleased(e);
		panel.keyReleased(e);
		bt.keyReleased(e);
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e){
		super.mouseWheelMoved(e);
		panel.mouseWheelMoved(e);
		bt.mouseWheelMoved(e);
	}
	@Override
	public int getH() {
		return bt.getH()+panel.getH();
	}
	public void addCanvasComponent(CanvasComponent cc){
		cc.setY(panel.getH());
		panel.addCanvasComponent(cc);
	}
	public void removeAllCanvasComponent(){
		panel.removeAllCanvasComponent();
	}
	@Override
	public void setPriority(int priority){
		super.setPriority(priority);
		bt.setPriority(priority);
		panel.setPriority(priority);
	}
}
