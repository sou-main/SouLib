package soulib.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import soulib.fileLib.ConfigFile;

//クライアントサンプルプログラム
//サーバーに接続し、メッセージを送信する。
//サーバーアドレスはコマンドラインの第1引数で指定。
//ポートは30000に固定。先にMultiServerSampleを起動しておくこと。
//第2引数で、メッセージを指定する。一行送ってサーバーからの
//メッセージ受信，表示後にプログラム終了する。
//コマンドライン例：java MultiClientSample localhost abcdefg

public class KeyClient implements Runnable{
	private static int LastClientID;
	private Socket socket;
	public boolean log=true;
	public int ClientID;
	private Thread thread;
	private int port;
	private InetAddress serverIP;
	private PrintWriter writer;
	private BufferedReader rd;
	public boolean StartServer;
	public KeyClient(String server,int port) throws UnknownHostException{
		StartServer=false;
		thread=new Thread(this,"Client"+LastClientID);
		this.port=port;
		serverIP=InetAddress.getByName(server);
		ClientID=LastClientID;
		LastClientID++;
	}
	public void start() {
		thread.start();
	}
	public int getNetworkID() throws IOException {
		return ConfigFile.StringtoInt(getValue("NetworkID"));
	}
	public void run(){
		try{
			//アドレス情報を保持するsocketAddressを作成。
			//ポート番号は30000
			InetSocketAddress socketAddress=new InetSocketAddress(serverIP,port);
			//socketAddressの値に基づいて通信に使用するソケットを作成する。
			socket=new Socket();
			//タイムアウトは10秒(10000msec)
			socket.connect(socketAddress, 10000);
			//接続先の情報を入れるInetAddress型のinadrを用意する。
			InetAddress inadr;
			//inadrにソケットの接続先アドレスを入れ、nullである場合には
			//接続失敗と判断する。
			//nullでなければ、接続確立している。
			if ((inadr = socket.getInetAddress()) != null) {
				Connect(inadr);
				//メッセージの送信処理
				//PrintWriter型のwriterに、ソケットの出力ストリームを渡す。(Auto Flush)
				writer = new PrintWriter(socket.getOutputStream(), true);
				//ソケットの入力ストリームをBufferedReaderに渡す。
				rd = new BufferedReader(
				new InputStreamReader( socket.getInputStream()));
				StartServer=true;
			}else {
				ConnectionFailed();
				return;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void Connect(InetAddress inadr){
		if(log)System.out.println("Connect to " + inadr);
	}
	protected void ConnectionFailed(){
		System.out.println("Connection failed.");
		try{
			socket.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	protected void finalize() throws Throwable{
		close();
	}
	public void close() throws IOException{
		if(socket!=null)socket.close();
	}
	public String getVal(String key) throws IOException {
		int br=0;
		while(!StartServer){
			if(br>600)break;
			try{
				Thread.sleep(50);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
			br++;
		}
		return getValue(key);
	}
	public String getValue(String key) throws IOException{
		if(log)System.out.println("Send key: "+key);
		//ソケットから出力する。
		writer.println(key);
		//もしPrintWriterがAutoFlushでない場合は，以下が必要。
		//writer.flush();
		//サーバーからのメッセージ読み取り
		String Value=rd.readLine();
		if(log)System.out.println("Value from Server:" + Value);
		//終了処理
		if(Value.equals("Null"))Value=null;
		return Value;
	}
	public String event(String name) throws IOException{
		return getValue(name);
	}
}