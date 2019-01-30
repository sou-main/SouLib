package soulib.windowLib;

/**表示処理*/
public interface Render{
	public void Update(CommonGraphics g);
	/**Updateの前に呼ばれる事が保証されています*/
	public void init(WindowBase win);
}
