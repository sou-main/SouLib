package soulib.lib;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author SOU<br>
 *         バージョン情報を管理します
 */
public class Version{
	private final long verA;
	private final int[] verB;
	private String S="";
	/**
	 * これはベータ版であることを示します。<br>
	 */
	public boolean bata=false;
	private String date;
	/**
	 * @author SOU
	 * 
	 *         <pre>
	 * new Version("1.2.3")
	 * set 1,2,3
	 *         </pre>
	 */
	public Version(String ver1,String s){
		this(ver1);
		S=s;
	}
	public Version(String ver1,String s,String date){
		this(ver1);
		S=s;
		this.date=date;
	}
	public Version(String ver1){
		String[] ver=ver1.split(Pattern.quote("."));
		verB=new int[ver.length-1];
		try{
			verA=Long.parseLong(ver[0]);
		}catch(Throwable e){
			throw new RuntimeException("VerA");
		}
		for(int i=0;i<verB.length;i++) {
			try{
				verB[i]=Integer.parseInt(ver[i+1]);
			}catch(Throwable e){
			
			}
			if(verB[i]<0) throw new IndexOutOfBoundsException("Ver"+i+"<0: "+verB[i]);
		}
	}
	/**
	 * @author SOU
	 * @param a 一桁目
	 * @param b 二桁目、99までの制限あり
	 * @param c 三桁目、99までの制限あり
	 */
	public Version(int a,int b,int c){
		verA=a;
		verB=new int[]{b,c};
	}
	/**
	 * @author SOU
	 * @param s 文字列を数値に変換する
	 */
	public int StringtoInt(String s){
		try{
			return Integer.parseInt(s);
		}catch(Throwable e){
			System.out.println("StringtoInt-"+s+"-"+e);
			return -1;
		}
	}
	/**
	 * <br>
	 * バージョンの文字列表現を返します。<br>
	 * 
	 * @return バージョンの文字列表現
	 * @author SOU
	 */
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append(verA);
		for(int i=0;i<verB.length;i++) {
			sb.append(".").append(verB[i]);
		}
		if(S!=null&&!S.isEmpty())sb.append(S);
		return sb.toString();
	}
	/**
	 * これはtoString()を呼び出します。<br>
	 * 
	 * @author SOU
	 */
	public String getString(){
		return toString();
	}
	/**
	 * @author SOU バージョンをdoubleで表した値を返します。
	 */
	public double getdouble(){
		return verA+(verB[0]*0.01)+(verB[1]*0.0001);
	}
	/**
	 * @author SOU <br>
	 * 		このバージョンと引数のバージョンを比較して、 引数のバージョンが新しい場合trueを返します。
	 */
	public boolean isNew(Version v){
		if(verA<v.verA)return true;
		else if(verA>v.verA)return false;
		for(int i=0;i<verB.length;i++) {
			if(verB[i]<v.verB[i])return true;
			else if(verB[i]>v.verB[i])return false;
		}
		return false;
	}
	public Version setBata(boolean b){
		bata=b;
		return this;
	}
	public String getDate(){
		return date;
	}
	public void setDate(String date){
		this.date = date;
	}
	@Override
	public int hashCode(){
		final int prime=31;
		int result=1;
		result=prime*result+((S==null) ? 0 : S.hashCode());
		result=prime*result+(bata ? 1231 : 1237);
		result=prime*result+((date==null) ? 0 : date.hashCode());
		result=prime*result+(int) (verA^(verA>>>32));
		result=prime*result+Arrays.hashCode(verB);
		return result;
	}
	@Override
	public boolean equals(Object obj){
		if(this==obj) return true;
		if(obj==null) return false;
		if(getClass()!=obj.getClass()) return false;
		Version other=(Version) obj;
		if(S==null){
			if(other.S!=null) return false;
		}else if(!S.equals(other.S)) return false;
		if(bata!=other.bata) return false;
		if(date==null){
			if(other.date!=null) return false;
		}else if(!date.equals(other.date)) return false;
		if(verA!=other.verA) return false;
		if(!Arrays.equals(verB,other.verB)) return false;
		return true;
	}
}
