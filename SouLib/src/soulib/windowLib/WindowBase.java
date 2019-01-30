package soulib.windowLib;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;

import soulib.logLib.WindowLog;

public interface WindowBase{
	/**背景色で塗りつぶす*/
	public void del();
	/**表示を指定の色で塗りつぶす
	 * @param color 塗りつぶす色*/
	public void del(Color color);
	public void Update(Graphics g);
	public void Update(Graphics2D g);
	/**表示処理。<br>
	 * 特に理由がない場合<br>
	 * 最優先でこれを使う
	 * @param g 表示対象*/
	public void Update(CommonGraphics g);
	/**ウィンドウの名前を取得
	 * @return ウィンドウの名前*/
	public String getName();
	/**ウィンドウを閉じる*/
	public void close();
	/**ディスプレイの中央に移動する*/
	public void setCenter();
	/**初期化*/
	public void init();
	/**背景色を取得する<br>
	 * @return 背景色*/
	public Color getBackground();
	/**このウィンドウに表示する文字列を指定
	 * @param windowLog 指定する文字列管理オブジェクト*/
	public void setWindowlog(WindowLog windowLog);
	/**ウィンドウの文字列管理オブジェクト<br>
	 * に指定の文字を追加する
	 * @param data 追加する文字
	 * @return このウィンドウ*/
	public WindowBase addData(String data);
	/**このウィンドウの文字列管理オブジェクトを取得する。
	 * @return 文字列管理オブジェクト*/
	public WindowLog getWindowLog();
	/**このウィンドウに登録されている文字列
	 * @return 文字列の配列*/
	public String[] getDataList();
	/**表示を更新する*/
	public void repaintCan();
	/**ウィンドウを閉じた時の処理
	 * Overrideして使う*/
	public WindowListener setWindowListener();
	/**このウィンドウが閉じているか
	 * @return 閉じているか*/
	public boolean isClose();
	/**自動でキャンバスの大きさを調整するか
	 * @return 自動調整*/
	public boolean AutoReSize();
	/**このウィンドウの幅を返す
	 * @return このウィンドウの幅*/
	public int getWidth();
	/**このウィンドウの高さを返す
	 * @return このウィンドウの高さ*/
	public int getHeight();
	/**現在のウィンドウの大きさに合わせてキャンバスの大きさを調整する*/
	public void ReSize();
	/**StandardWindowでのみ動作する。<br>
	 * */
	public void createBufferStrategy(int numBuffers);
	/**StandardWindowでのみ動作する。<br>
	 * */
	public BufferStrategy getBufferStrategy();
	/**StandardWindowでのみ動作する。<br>
	 * */
	public void set();
	/**ウィンドウの種類を特定するための名前を返す
	 * @return このウィンドウの識別名*/
	public String getTypeName();
	/**フレームレートの変更
	 * @param fps 新しいフレームレート*/
	public void setFps(float fps);
	/**フレームレートを管理しているオブジェクトを返す
	 * @return フレームレートオブジェクト*/
	public FrameRate getFps();

	public int getWindowX();
	public int getWindowY();
	/**表示処理をしているスレッドを返す*/
	public Thread getThread();
	/**キー入力オブジェクトを返す*/
	public KI getKeyInput();
	/**マウス入力オブジェクトを返す*/
	public MI getMouseInput();
	/**現在のウィンドウモードを返す*/
	public WindowMode getWindowMode();
	/**前回の表示処理に余った時間を返す*/
	public long getSleep();
	/**キャンバスコンポーネントを管理するオブジェクトを返す。<br>
	 * このオブジェクトにキャンバスコンポーネントを追加することで、<br>
	 * ウィンドウにキャンバスコンポーネントを追加できる。*/
	public CanvasComponentManager getCanvasComponentManager();
	public void setCursor(CommonCursor c);
	/**現在のスクロール位置を返す*/
	public int getScroll();
	public void setKeyInput(KI ki);
	public void setMouseInput(MI mi);
	public void setRender(Render render);
	public void init(CommonGraphics g);
	public void moveWindow(int x,int y);
	public void setScroll(int sc);
	public Tooltip getTooltip();
}
