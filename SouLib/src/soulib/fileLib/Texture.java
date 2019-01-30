package soulib.fileLib;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import soulib.windowLib.CommonGraphics;
import soulib.windowLib.GraphicsAWT;

/**GLTexture互換のテクスチャ*/
public class Texture implements TextureBase{
	private BufferedImage img;
	private Double rec;
	private int size;
	public Texture(String Name,boolean errorlog){
		Load(Name,10,errorlog);
	}
	public Texture(String Name){
		Load(Name,10,true);
	}
	public Texture(String Name,int size){
		Load(Name,size,true);
	}
	public Texture(BufferedImage i){
		Load(i);
	}
	public Texture(Image Image){
		Load((BufferedImage) Image);
	}
	public Texture(File f){
		Load(f,10,true);
	}
	public Texture(JarLocalFile f){
		try{
			size=10;
			img=new BufferedImage(size,size,BufferedImage.TYPE_4BYTE_ABGR);
			img=ImageIO.read(f.getURL());
		}catch(Exception ex){
			img=null;
		}
		rec=new Rectangle2D.Double(0,0,size,size);
	}
	public Texture(URL url) {
		try{
			size=10;
			img=new BufferedImage(size,size,BufferedImage.TYPE_4BYTE_ABGR);
			img=ImageIO.read(url);
		}catch(Exception ex){
			img=null;
		}
		rec=new Rectangle2D.Double(0,0,size,size);
	}
	private void Load(BufferedImage i){
		img=i;
		rec=new Rectangle2D.Double(0,0,size,size);
	}
	private void Load(String Name,int size,boolean errorlog){
		File f=new File(Name);
		Load(f,size,errorlog);
	}
	private void Load(File f,int size,boolean errorlog){
		this.size=size;
		img=new BufferedImage(size,size,BufferedImage.TYPE_4BYTE_ABGR);
		/*
		try {
		  img = ImageIO.read(f);
		}catch (IOException ex){
		  ex.printStackTrace();
		}
		*/
		try{
			img=ImageIO.read(f);
		}catch(IOException ex){
			if(errorlog) ex.printStackTrace();
			img=null;
		}
		rec=new Rectangle2D.Double(0,0,size,size);
	}
	public Double getRec(){
		return rec;
	}
	@Override
	public BufferedImage getImg(){
		return img;
	}
	public int getsize(){
		return size;
	}
	public void drawTexture(Graphics g,int x,int y,int w,int h){
		if(img!=null){
			((Graphics2D) g).setPaint(new TexturePaint(img,rec));
			g.drawImage(img,x,y,w,h,null);
		}
	}
	public void drawTexture(Graphics g,double x,double y,double size){
		if(img!=null){
			((Graphics2D) g).setPaint(new TexturePaint(img,rec));
			g.drawImage(img,(int) (x-(size)/2),(int) (y-(size)/2),(int) (size),(int) (size),null);
		}
	}
	public void drawTexture(Graphics g,double x,double y){
		drawTexture(g,x,y,38);
	}
	public void drawTexture(Graphics g,int x,int y,int w,int h,float toumei){
		if(img!=null){
			((Graphics2D) g).setComposite(
					AlphaComposite.getInstance(AlphaComposite.SRC_OVER,toumei));
			g.drawImage(img,x,y,w,h,null);
			((Graphics2D) g).setComposite(
					AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1F));
		}
	}
	public void drawTexture(Graphics g,double degree,int x,int y,int w,int h){
		AffineTransform aff=((Graphics2D) g).getTransform();
		AffineTransform af=new AffineTransform(aff);

		//af.scale(0.2,0.2);
		//affin.scale(w, h);
		//イメージの移動
		//af.translate(x-w/2, y-h/2);
		//イメージを中心位置で回転
		af.rotate(Math.toRadians(degree),x+w/2,y+h/2);

		//affin.translate(-(img.getWidth()-img.getHeight())/2, (img.getWidth()-img.getHeight())/2);
		//affin.rotate(Math.toRadians(degree),img.getWidth()/2d,img.getHeight()/2d);
		((Graphics2D) g).setTransform(af);
		//((Graphics2D)g).drawImage(img,af,null);
		drawTexture(g,x,y,w,h);
		((Graphics2D) g).setTransform(aff);

		//drawTexture(g, x, y, w, h);
	}
	@Override
	public void dispose(){
		if(img!=null)img.flush();
	}
	@Override
	public void draw(CommonGraphics g,int x,int y,int w,int h){
		drawTexture(((GraphicsAWT)g).g,x,y,w,h);
	}
	@Override
	public void draw(CommonGraphics g,int x,int y){
		drawTexture(((GraphicsAWT)g).g,x,y);
	}
	@Override
	public void draw(CommonGraphics g,float x,float y){
		drawTexture(((GraphicsAWT)g).g,x,y);
	}
	@Override
	public void draw(CommonGraphics g,float x,float y,float w,float h){
		drawTexture(((GraphicsAWT)g).g,(int)x,(int)y,(int)w,(int)h);
	}
	@Override
	public void draw(CommonGraphics g,double degree,float x,float y,float w,float h){
		drawTexture(((GraphicsAWT)g).g,degree,(int)x,(int)y,(int)w,(int)h);
	}
	@Override
	public String getClassName() {
		return "AWT";
	}
}
