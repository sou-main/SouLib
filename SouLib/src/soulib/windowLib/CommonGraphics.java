package soulib.windowLib;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Polygon;
/**グラフィックス処理を共通化します*/
public interface CommonGraphics{
	/**レンダリングエンジンの名前を返します。
	 * @return レンダリングエンジンの名前*/
	public String getClassName();
	/**現在の色を指定した色に変更します。
	 * @param color 新しい色*/
	public void setColor(Color color);
	/**指定した座標に線で円を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param w 幅
	 * @param h 高さ*/
	public void drawOval(int x,int y,int w,int h);
	/**指定した座標に塗りつぶした円を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param w 幅
	 * @param h 高さ*/
	public void fillOval(int x,int y,int w,int h);
	/**指定した座標に線で四角形を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param w 幅
	 * @param h 高さ*/
	public void drawRect(int x,int y,int w,int h);
	/**指定した座標に塗りつぶした四角形を描きます。
	 * @param x X座標
	 * @param y Y座標
	 * @param w 幅
	 * @param h 高さ*/
	public void fillRect(int x,int y,int w,int h);
	/**指定した座標に文字を描きます
	 * @param str 描く文字
	 * @param x X座標
	 * @param y Y座標
	 * */
	public int drawString(String str,int x,int y);
	/**指定した座標に文字を描きます
	 * @param x1 点1のX座標
	 * @param y1 点1のY座標
	 * @param x2 点2のX座標
	 * @param y2 点2のY座標
	 * */
	public void drawLine(int x1,int y1,int x2,int y2);
	/**現在の背景色を指定した色に変更します。
	 * @param color 新しい色*/
	public void setBackground(Color color);
	/**現在の背景色を返します。
	 * @return 現在の背景色*/
	public Color getBackground();
	/**現在の描画色を返します。
	 * @return 現在の描画色*/
	public Color getColor();
	/**現在のフォントを返します。
	 * @return 現在のフォント*/
	public Font getFont();
	/**現在のフォントを指定したフォントに変更します。
	 * @param font 新しく設定されるフォント*/
	public void setFont(Font font);
	/**全てのバッファを初期化します。<br>
	 * <font size=2 color=red><b>レンダリングエンジンによっては使われない場合があります。</b></font>*/
	public void Buffclear();
	/**現在の奥行きを指定した値に変更します。
	 * <font size=2 color=red><b>レンダリングエンジンによっては使われない場合があります。</b></font>*/
	public void setZ(int z);
	/**現在の奥行きを取得します。
	 * <font size=2 color=red><b>レンダリングエンジンによっては常に0を返す場合があります。</b></font>
	 * @return 現在の奥行き、対応していない場合常に0*/
	public int getZ();
	public void fillOval(float x,float y,float w,float h);
	public void drawOval(float x,float y,float w,float h);
	public float drawString(String l,float x,float y);
	public float getWidth();
	public float getHeight();
	public WindowBase getWindow();
	public void drawPolygon(Polygon p);
	public void fillPolygon(Polygon p);
	public void fill3DRect(int x,int y,int w,int h,boolean b);
	public int getPaintWidth(String s);
	public void fillRect(float x,float y,float w,float h);
	public void drawRect(float x,float y,float w,float h);
	public void drawLine(float x,float y,float w,float h);
	public void setColor(float[] c);
	public void fillPolygon(int[] is,int[] is2,int i);
	public void del();
	public void del(Color color);
	public void drawImage(Image img,int x,int y,int w,int h);
}