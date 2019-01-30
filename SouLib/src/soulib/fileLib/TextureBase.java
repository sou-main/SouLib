package soulib.fileLib;

import java.awt.image.BufferedImage;

import soulib.windowLib.CommonGraphics;

public interface TextureBase{
	BufferedImage getImg();
	void dispose();
	void draw(CommonGraphics g,int x,int y);
	void draw(CommonGraphics g,float x,float y);
	void draw(CommonGraphics g,int x,int y,int w,int h);
	void draw(CommonGraphics g,float x,float y,float w,float h);
	void draw(CommonGraphics g,double degree,float x,float y,float w,float h);
	String getClassName();

}
