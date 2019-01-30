package soulib.fileLib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import soulib.lib.MapConfig;
import soulib.lib.Version;

public class AutoUpdate{
	private MapConfig map;
	private String keyVersion="version";
	private String keyDownload="download";
	private String keyName="name";
	private String keyExtension="extension";
	private String DLname="DL";//変更推奨
	/** 拡張子 */
	private String extension=".jar";
	private boolean read=false;
	private Version nowVer;
	private Version newVer=new Version("0");
	private URL url;
	public AutoUpdate(Version now,String url) throws MalformedURLException{
		this(now,new URL(url));
	}
	public AutoUpdate(Version now,URL url){
		nowVer=now;
		this.url=url;
		map=new MapConfig();
		map.Kugiri="=";
	}
	public void read() throws IOException{
		InputStream con=url.openStream();
		try{
			map.read(con,null);
		}finally{
			con.close();
		}
		if(!map.map.isEmpty()){
			String v=map.getConfigDataString(keyVersion,"0");
			newVer=new Version(v);
			read=true;
		}else System.err.println("AutoUpdateReadError");
	}
	public void update() throws IOException{
		if(nowVer==null){
			System.err.println("[AutoUpdate]nowVer=null");
			return;
		}
		if(!read) read();
		if(!read) return;
		if(nowVer.isNew(newVer)){
			String DownloadURLText=map.getConfigDataString(keyDownload);
			if(DownloadURLText==null) return;
			if(DownloadURLText.charAt(0)=='/') {
				StringBuilder sb=new StringBuilder();
				sb.append(url.getProtocol());
				sb.append("://");
				sb.append(url.getAuthority());
				sb.append(DownloadURLText);
				DownloadURLText=sb.toString();
			}
			URL DLurl=new URL(DownloadURLText);
			extension=map.getConfigDataString(keyExtension,extension);
			DLname=map.getConfigDataString(keyName,DLname);
			File dl=new File(DLname+newVer.toString()+extension);
			FileEditor.FE.copyResourceToFile(DLurl,dl,512);
		}
	}
	/**テスト用*/
	public static void main(String[] args) {
		/**実行中のバージョン*/
		Version ver=new Version("1.0.0");
		/**更新確認用のURL　ローカルのファイルでも可*/
		String url="http://localhost/update/test.txt";
		try{
			new AutoUpdate(ver, url).update();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public MapConfig getMap(){
		return map;
	}
	public void setKeyVersion(String keyVersion){
		this.keyVersion=keyVersion;
	}
	public boolean isRead(){
		return read;
	}
	public Version getNowVer(){
		return nowVer;
	}
	public void setNowVer(Version nowVer){
		this.nowVer=nowVer;
	}
	public Version getNewVer(){
		return newVer;
	}
	public void setUrl(URL url){
		this.url=url;
	}
	public String getDLname(){
		return DLname;
	}
	public void setDLname(String dLname){
		DLname=dLname;
	}
	public void setKeyDownload(String keyDownload){
		this.keyDownload=keyDownload;
	}
	public void setKeyName(String keyName){
		this.keyName=keyName;
	}
	public String getExtension(){
		return extension;
	}
	public void setExtension(String extension){
		this.extension=extension;
	}
	public void setKeyExtension(String keyExtension){
		this.keyExtension=keyExtension;
	}
}
