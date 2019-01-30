package soulib.windowLib;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class CommonCursor{
	private Point hotSpot=new Point();
	private BufferedImage image=null;
	private String name="Cursor";
	private Cursor cursor=null;
	public CommonCursor(BufferedImage img,Point p,String name) {
		image=img;
		if(image==null)image=new BufferedImage(2,2, BufferedImage.TYPE_INT_ARGB);
		if(p!=null)hotSpot=p;
		if(name!=null)this.name=name;
	}
	public Cursor getCursor(){
		if(cursor!=null)return cursor;
		return cursor=Toolkit.getDefaultToolkit().createCustomCursor(image,hotSpot,name);
	}
	public Point getHot() {
		return hotSpot;
	}
	public BufferedImage getImage(){
		return image;
	}
	public String getName() {
		return name;
	}
}
