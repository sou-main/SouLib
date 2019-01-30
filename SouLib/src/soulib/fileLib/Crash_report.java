package soulib.fileLib;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import soulib.net.ReportToServer;

public class Crash_report{
	private File f=null;
	public static ArrayList<String> server=new ArrayList<String>(1);
	public Crash_report(Throwable Throwable,String SaveName){
		f=FileEditor.FE.crash_report(Throwable,SaveName);
	}
	public File getFile(){
		return f;
	}
	private static String ip=null;
	/**失敗した場合nullを返す*/
	public static String getServerIP(boolean reload){
		return getServerIP(reload,null);
	}
	/**失敗した場合引数のmissを返す*/
	public static String getServerIP(boolean reload,String miss){
		if(ip!=null&&!reload)return ip;
		if(check("http://localhost"))return ip="localhost";
		for(String s:server)if(check(s))return ip=s;
		if(check("http://"+DefaultIP))return ip=DefaultIP;
		if(check("http://"+BackupIP))return ip=BackupIP;
		String[] data;
		try{
			data=FileEditor.FE.ReadFileURL0(dbURL);
			ConfigFile cfg=new ConfigFile(data);
			String IP=cfg.getConfigDataString("ServerIP");
			if(IP.isEmpty())ip=IP;
			return IP.isEmpty()?miss:IP;
		}catch(IOException e){}
		return miss;
	}
	/**接続テスト<br>trueが成功*/
	public static boolean check(String IP) {
		try {
			URL url = new URL(IP);
			url.openConnection().connect();
			return true;
		}catch(Exception e) {}
		return false;
	}
	public static final String dbURL="https://dl.dropboxusercontent.com:443/s/uuzl0znyewm9vs0/web.json";
	public static final String CodeZipFile="00000";
	public static final String CodeTxtFile="00001";
	public static final String AutoFile="auto5";
	public static final int DefaultPort=8001;
	public static final String DefaultIP="sousoft.dip.jp";
	public static final String BackupIP="sousoft.jpn.ph";
	public static final int AutoK=20;
	public static long ping(String IP,int Port) throws Exception{
		//final int    PORT = 8001;        // 接続先ポート番号
		if(Port<0) Port=getDefaultPort(Port);
		if(IP==null) IP=getServerIP(false);
		if(IP=="") IP=getServerIP(false);
		if(IP==null)throw new NullPointerException("サーバーアドレス取得失敗");
		// ソケットの準備
		Socket socket=new Socket(IP,Port);
		// ストリームの準備
		DataInputStream dis=new DataInputStream(socket.getInputStream());
		OutputStream outputStream=socket.getOutputStream();
		outputStream.write("ping0".getBytes("Unicode"),0,"00000".getBytes("Unicode").length);
		long l=dis.readLong();
		long nt=System.currentTimeMillis();
		socket.close();
		return l-nt;
	}
	public static void ReportToServer(File file,String Code,String IP,int Port) throws Exception{
		ReportToServer(file, Code, IP, Port,"");
	}
	public static void ReportToServer(File file,String Code,String IP,int Port,String dir) throws Exception{
		new ReportToServer().ToServer(file,Code,IP,Port,dir);
	}
	public static synchronized void MakingReport(File file,String ServerIP,int ServerPort){
		try{
			ReportToServer2(file,"00000",ServerIP,ServerPort);
			System.out.println("サーバーにデータを送信しました");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("サーバーにデータを送信できませんでした");
		}
	}
	public static void ReportToServer2(File file,String Code,String IP,int Port) throws Exception{
		if(IP==null||IP==""||Port<0){
			String[] data=FileEditor.FE.ReadFileURL0(dbURL);
			ConfigFile cfg=new ConfigFile(data);
			if(IP==null||IP=="") IP=cfg.getConfigDataString("ServerIP")!="" ? cfg.getConfigDataString("ServerIP") : IP;
			if(Port<0) Port=cfg.getConfigDataString("ServerPort")!="" ? cfg.getConfigDataInt("ServerPort") : Port;
		}
		if(Code==null||Code=="") Code="auto1";//データタイプが指定されていないときは自動。
		//final int    PORT = 8001;        // 接続先ポート番号
		ReportToServer(file,Code,IP,Port);
	}
	private static int portBuff=-1;
	public static void resetPortBuff() {
		portBuff=-1;
	}
	public static int getDefaultPort(int missData) {
		try{
			return getDefaultPortT(missData);
		}catch(IOException e){
			e.printStackTrace();
		}
		return missData;
	}
	public static int getDefaultPortT(int missData) throws IOException {
		if(portBuff>=0)return portBuff;
		String[] ReadURL=FileEditor.FE.ReadFileURL0(dbURL);
		ConfigFile cf=new ConfigFile(ReadURL);
		int PB=cf.getConfigDataInt("ServerPort");
		if(PB>=0)portBuff=PB;
		missData=PB<0 ? missData : PB;
		return missData;
	}
}