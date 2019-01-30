package soulib.windowLib.CanvasComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;

import soulib.windowLib.CommonGraphics;
import soulib.windowLib.WindowBase;
import soulib.windowLib.WindowLib;

public class CTextFiled extends CanvasComponent{

	private boolean celect;
	private StringBuffer text;
	private int fs;
	public int textW;
	private long time;
	private int minW=100;
	private int cursor=0;
	private int cursorW=2;
	private boolean bool;
	private int cursorPos;
	protected int maxLine=10;

	/**一行テキスト入力*/
	public CTextFiled(){
		this("");
	}
	public CTextFiled(String text){
		if(text==null)text="";
		this.text=new StringBuffer(text);
		this.setBackground(Color.white);
		this.setColor(Color.black);
		this.setFont(getFont());
		textW=WindowLib.getPaintWidth(getFont(),getText());
	}
	@Override
	public void draw(CommonGraphics g,Point e){
		g.setColor(Color.black);
		g.drawRect(getX(),getY(),getW(),getH());
		g.fillRect(getX(),getY(),getW(),getH());
		g.setColor(getBackground());
		g.fillRect(getX()+1,getY()+2,getW()-1,getH()-1);
		//g.setColor(Color.gray);
		//g.fillRect(getX(),getY(),getW(),getH());
		g.setColor(getColor());
		g.setFont(getFont());
		g.drawString(getText(),getX()+5,getY()+fs+2);
		if(isInside(e)) {
			WindowBase win0=g.getWindow();
			if(win0 instanceof Frame)((Frame)win0).setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		}
		if(celect){
			long now=System.currentTimeMillis();
			if(now-time>500) setCursorPos(!bool);
			if(bool){
				if(cursorW>0) g.fillRect(cursorPos+getX(),getY()+4,cursorW,getH()-6);
				else g.drawLine(cursorPos+getX(),getY()+4,getX()+textW,getY()+getH()-4);
			}
		}
	}
	private void setCursorPos(boolean b){
		textW=WindowLib.getPaintWidth(getFont(),getText());
		cursorPos=5;
		String s="";
		for(int i=0;i<cursor;i++){
			if(i<getText().length()) s+=getText().charAt(i);
		}
		cursorPos+=WindowLib.getPaintWidth(getFont(),s);
		bool=b;
		time=System.currentTimeMillis();
	}
	@Override
	public void mouseClicked(MouseEvent e){
		if(isInside(e.getPoint())) celect=true;
		else{
			celect=false;
			return;
		}
		int w=e.getX()-getX();
		StringBuffer sb=new StringBuffer();
		cursor=0;
		for(int i=0;i<getText().length();i++) {
			sb.append(getText().charAt(i));
			if(w<WindowLib.getPaintWidth(getFont(),sb.toString()))break;
			cursor=i+1;
		}
		//System.out.println("W="+w+"cursor"+cursor+"paint"+WindowLib.getPaintWidth(getFont(),sb.toString()));
		setCursorPos(true);
	}
	/** マウスのボタンを離した時に呼ばれます。
	 * @param e イベント*/
	public void mouseReleased(MouseEvent e){
		this.mouseClicked(e);
	}
	@Override
	public void keyPressed(KeyEvent e){
		if(!celect||e.isConsumed()) return;
		int code=e.getKeyCode();
		char c=e.getKeyChar();
		boolean up=false;
		if(code==KeyEvent.VK_BACK_SPACE){
			int i=cursor;
			if(i>0){
				try{
					text.delete(i-1,i);
				}catch(StringIndexOutOfBoundsException f){
				}
				cursor--;
			}
			text.trimToSize();
			up=true;
		}else if(code==KeyEvent.VK_RIGHT){
			if(text.length()>cursor) cursor++;
		}else if(code==KeyEvent.VK_LEFT){
			if(0<cursor) cursor--;
		}else if(code==KeyEvent.VK_DELETE){
			int i=cursor;
			if(i>=0&&i+1<=text.length()){
				try{
					text.delete(i,i+1);
				}catch(StringIndexOutOfBoundsException f){}
			}
			up=true;
		}else if(c!=KeyEvent.CHAR_UNDEFINED){
			//String s=String.valueOf(c);
			//if(!s.isEmpty()) {
			if(e.isControlDown()) {
				if(code==KeyEvent.VK_V) {
					Clipboard clip=java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable t=clip.getContents(null);
					DataFlavor[] arr=t.getTransferDataFlavors();
					if(arr==null||arr.length==0)return;
					for(DataFlavor raw:arr){
						if(DataFlavor.stringFlavor.equals(raw)){
							try{
								String s=(String) t.getTransferData(raw);
								text.insert(cursor,s);
								cursor+=s.length();
								up=true;
							}catch(UnsupportedFlavorException e1){
								e1.printStackTrace();
							}catch(IOException e1){
								e1.printStackTrace();
							}
							break;
						}
					}
				}else if(code==KeyEvent.VK_C) {
					try{
						Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(new StringSelection(getText()),null);
					}catch(Exception ex) {

					}
				}
			}else if(!e.isAltDown()&&c!='\n') {
				text.insert(cursor,c);
				cursor++;
				up=true;
			}
		}
		setCursorPos(true);
		if(up)updateText();
	}
	public void updateText() {

	}
	@Override
	public String toString(){
		return getText();
	}
	/** 入力された内容 */
	public String getText(){
		return text.toString();
	}
	public void setText(String text){
		this.text=new StringBuffer(text);
		if(text.length()<cursor)cursor=text.length();
		textW=WindowLib.getPaintWidth(getFont(),text);
		updateText();
	}
	@Override
	public void setFont(Font font){
		super.setFont(font);
		fs=WindowLib.getPaintHeight(font);
	}
	@Override
	public int getH(){
		if(h>0&&!autoSize) return h;
		return h=getH(fs);
	}
	@Override
	public int getW(){
		if(w>0&&!autoSize) return w;
		if(textW+50>minW) return w=textW+50;
		return minW;
	}
	public static int getH(int fs){
		return fs+5;
	}
	public static int getW(int name,int fs){
		return name*(fs+2);
	}
	public boolean isCelect(){
		return this.celect;
	}
	public void setCelect(boolean celect){
		this.celect=celect;
	}
	public int getMinWidth(){
		return this.minW;
	}
	public void setMinWidth(int minW){
		this.minW=minW;
	}
	public int getCursor(){
		return this.cursor;
	}
	public void setCursor(int cursor){
		this.cursor=cursor;
	}
	public int getCursorWidth(){
		return this.cursorW;
	}
	public void setCursorWidth(int cursorW){
		this.cursorW=cursorW;
	}
	public boolean isEmpty(){
		return text.length()<1;
	}
}
