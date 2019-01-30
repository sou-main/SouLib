package soulib.lib;

public abstract class Command{
	private String Name="";
	public String data1;
	public String data2;
	/**
	 * 指定した名前のコマンドを作成します
	 * @param NAME コマンドの名前
	 */
	public Command(String NAME){
		Name=NAME;
	}
	/**
	 * コマンドの名前を返します
	 * @return コマンドの名前
	 */
	public String getName(){
		return Name;
	}
	/**
	 * getName()と同じです
	 */
	public String toString(){
		return getName();
	}
	/**
	 * コマンドが実行された時に呼ばれます
	 * @param Data パラメータ
	 * @return 終了コード
	 */
	public abstract String Code(String Data,boolean stage)throws Throwable;

/**
 * 2つのパラメータを持つコマンドの
 * 一つ目のパラメータを返します
 * @param Dta コマンド
 * @return コマンドのパラメータ1
 */
	public String getComParm2_1(String Dta){
		String Data = "";
		String Data2 = "";
		String buf=" ";
			if(Dta!=null){
		if(Dta.indexOf(buf)!=-1){
			try{
				int buf0=Dta.indexOf(buf)+buf.length();
				int buf1=Dta.length();
				//String buf2
				Data=Dta.substring(buf0,buf1);
				//String buf3
				Data2=Dta.substring(0,Dta.indexOf(buf));
				//Data=new String(buf2.getBytes("Unicode") , "Unicode" );
				//Data2=new String(buf3.getBytes("Unicode") , "Unicode" );
			}catch(Throwable t){
				t.printStackTrace();
				//Error
			}
		}
		}
		if(Data!=""&&Data2!=""){
			return Data2;
		}
		return "";
	}
	/**
	 * 2つのパラメータを持つコマンドの
	 * 二つ目のパラメータを返します
	 * @param Dta コマンド
	 * @return コマンドのパラメータ2
	 */
	public String getComParm2_2(String Dta){
		String Data = "";
		String Data2 = "";
		String buf=" ";
			if(Dta!=null){
		if(Dta.indexOf(buf)!=-1){
			try{
				int buf0=Dta.indexOf(buf)+buf.length();
				int buf1=Dta.length();
				//String buf2
				Data=Dta.substring(buf0,buf1);
				//String buf3
				Data2=Dta.substring(0,Dta.indexOf(buf));
				//Data=new String(buf2.getBytes("Unicode") , "Unicode" );
				//Data2=new String(buf3.getBytes("Unicode") , "Unicode" );
			}catch(Throwable t){
				t.printStackTrace();
				//Error
			}
		}
		}
		if(Data!=""&&Data2!=""){
			return Data;
		}
		return "";
	}
	public String getComParm3_1(String Dta){
		String Data = "";
		String Data2 = "";
		String Data3 = "";
		String buf=" ";
			if(Dta!=null){
		if(Dta.indexOf(buf)!=-1){
			try{
				Data=Dta.substring(0,Dta.indexOf(buf));
				String bufA=Dta.substring(Data.length()+buf.length(),Dta.length());
				int buf0=bufA.indexOf(buf);
				Data2=bufA.substring(0,buf0);
				String bufB=Dta.substring(Data2.length()+buf.length(),Dta.length());
				int buf2=bufB.indexOf(buf)+buf.length();
				int buf1=bufB.length();
				Data3=bufB.substring(buf2,buf1);
			}catch(Throwable t){
				t.printStackTrace();
				//Error
			}
		}
		}
		if(Data!=""&&Data2!=""&&Data3!=""){
			return Data;
		}
		return "";
	}
	public String getComParm3_2(String Dta){
		String Data = "";
		String Data2 = "";
		String Data3 = "";
		String buf=" ";
			if(Dta!=null){
		if(Dta.indexOf(buf)!=-1){
			try{
				Data=Dta.substring(0,Dta.indexOf(buf));
				String bufA=Dta.substring(Data.length()+buf.length(),Dta.length());
				int buf0=bufA.indexOf(buf);
				Data2=bufA.substring(0,buf0);
				String bufB=Dta.substring(Data2.length()+buf.length(),Dta.length());
				int buf2=bufB.indexOf(buf)+buf.length();
				int buf1=bufB.length();
				Data3=bufB.substring(buf2,buf1);
			}catch(Throwable t){
				t.printStackTrace();
				//Error
			}
		}
		}
		if(Data!=""&&Data2!=""&&Data3!=""){
			return Data2;
		}
		return "";
	}
	public String getComParm3_3(String Dta){
		String Data = "";
		String Data2 = "";
		String Data3 = "";
		String buf=" ";
			if(Dta!=null){
		if(Dta.indexOf(buf)!=-1){
			try{
				Data=Dta.substring(0,Dta.indexOf(buf));
				String bufA=Dta.substring(Data.length()+buf.length(),Dta.length());
				int buf0=bufA.indexOf(buf);
				Data2=bufA.substring(0,buf0);
				String bufB=Dta.substring(Data2.length()+buf.length(),Dta.length());
				int buf2=bufB.indexOf(buf)+buf.length();
				int buf1=bufB.length();
				Data3=bufB.substring(buf2,buf1);
			}catch(Throwable t){
				t.printStackTrace();
				//Error
			}
		}
		}
		if(Data!=""&&Data2!=""&&Data3!=""){
			return Data3;
		}
		return "";
	}
}
