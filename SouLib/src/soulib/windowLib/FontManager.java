package soulib.windowLib;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class FontManager{
	private String name;
	private int type;
	private int size=20;
	private Font buf;

	public FontManager() {
		name="sansserif";
		type=Font.PLAIN;
	}
	public static String[] getFontNames() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font fonts[] = ge.getAllFonts();
		String[] fontName=new String[fonts.length];
		for(int i=0;i<fonts.length;i++)fontName[i] = fonts[i].getName();
		return fontName;
	}
	@Override
	public int hashCode(){
		final int prime=31;
		int result=1;
		result=prime*result+((name==null) ? 0 : name.hashCode());
		result=prime*result+size;
		result=prime*result+type;
		return result;
	}
	@Override
	public boolean equals(Object obj){
		if(this==obj) return true;
		if(obj==null) return false;
		if(getClass()!=obj.getClass()) return false;
		FontManager other=(FontManager) obj;
		if(name==null){
			if(other.name!=null) return false;
		}else if(!name.equals(other.name)) return false;
		if(size!=other.size) return false;
		if(type!=other.type) return false;
		return true;
	}
	public Font get(){
		return get(name,type,size);
	}
	public Font get(int size){
		return get(name,type,size);
	}
	public Font get(int type,int size){
		return get(name,type,size);
	}
	public Font get(String name,int type,int size){
		if(buf!=null&&buf.getStyle()==type&&buf.getSize()==size&&buf.getName().equals(name))return buf;
		return buf=new Font(name,type,size);
	}
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name=name;
	}
	public int getType(){
		return type;
	}
	public void setType(int type){
		this.type=type;
	}
	public int getSize(){
		return size;
	}
	public void setSize(int size){
		this.size = size;
	}
	public void set(Font font){
		if(font.equals(buf))return;
		setType(font.getStyle());
		setSize(font.getSize());
		setName(font.getName());
	}
	public Font bufFont() {
		return buf;
	}
	public void bufFont(Font f) {
		buf=f;
	}
	public boolean equals(Font f) {
		return buf!=null&&buf.equals(f);
	}
}
