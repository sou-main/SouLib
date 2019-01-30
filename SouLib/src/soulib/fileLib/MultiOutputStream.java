package soulib.fileLib;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Vector;

/**複数の出力ストリームに出力するストリームを作成します*/
public class MultiOutputStream extends OutputStream{
	private Vector<OutputStream> out;
	private OutputStream[] outA;
	public MultiOutputStream(){
		out=new Vector<OutputStream>();
	}
	/**出力先を追加します。<br>
	 * 既に登録されている場合追加せずに登録されているindexを返します
	 * @param add 追加する出力先
	 * @return 追加した出力先のindex*/
	public synchronized int add(OutputStream add){
		if(out.contains(add)) {
			return out.indexOf(add);
		}else {
			out.add(add);
			setA();
			return out.size()-1;
		}
	}
	public synchronized void addAll(Collection<? extends OutputStream> add) {
		out.addAll(add);
		setA();
	}
	/**指定したindexに出力先を追加します。<br>
	 * 追加した出力先以降の出力先のインデックスに1を加算します<br>
	 * 既に登録されている場合追加せずに登録されているindexを返します
	 * @param add 追加する出力先
	 * @return 追加した出力先のindex*/
	public synchronized int add(int index,OutputStream add){
		if(out.contains(add)) {
			return out.indexOf(add);
		}else {
			out.add(index,add);
			setA();
			return out.size()-1;
		}
	}
	/***/
	public void setA() {
		outA=new OutputStream[out.size()];
		for(int i=0;i<outA.length;i++){
			outA[i]=out.get(i);
		}
	}
	/**@param del 登録から削除する出力先
	 * @return 削除した場合true(存在した場合true)*/
	public synchronized boolean del(OutputStream del){
		if(out.remove(del)) {
			setA();
			return true;
		}
		return false;
	}
	public synchronized boolean delAll(Collection<? extends OutputStream> del){
		if(out.removeAll(del)) {
			setA();
			return true;
		}
		return false;
	}
	/**@param index 登録から削除するindex
	 * @return 削除された出力先*/
	public synchronized OutputStream del(int index){
		OutputStream o=out.remove(index);
		if(o!=null) {
			setA();
			return o;
		}
		return null;
	}
	/**指定されたindexにある出力先を返します。
	 * @param index index
	 * @return 指定されたindexにある出力先*/
	public synchronized OutputStream get(int index) {
		return out.get(index);
	}
	/**出力先のindexを返します。
	 * @param os 検索する出力先
	 * @return 出力先のindex。出力先が登録されていない場合-1*/
	public synchronized int index(OutputStream os) {
		return out.indexOf(os);
	}
	/**出力先を管理しているVectorを返します。
	 * @return 全ての出力先*/
	protected Vector<OutputStream> getVector(){
		return out;
	}
	@Override
	public synchronized void write(int b) throws IOException{
		for(OutputStream os:outA){
			try{
				os.write(b);
			}catch(IOException e) {
				exception(e,os);
			}
		}
	}
	@Override
	public synchronized void write(byte b[], int off, int len) throws IOException {
		for(OutputStream os:outA){
			try{
				os.write(b,off,len);
			}catch(IOException e) {
				exception(e,os);
			}
		}
	}
	@Override
	public synchronized void close() throws IOException {
		for(OutputStream os:outA){
			try{
				os.close();
			}catch(IOException e) {
				exception(e,os);
			}
		}
	}
	@Override
	public synchronized void flush() throws IOException {
		for(OutputStream os:outA){
			try{
				os.flush();
			}catch(IOException e) {
				exception(e,os);
			}
		}
	}
	protected void exception(IOException e,OutputStream out)throws IOException{
		throw e;
	}
}