package soulib.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SrvThread extends Thread{
	public Socket soc;
	public KeyServer ks;
	public int id;
	private BufferedReader reader;
	private PrintWriter sendout;
	private boolean Close=false;
	public int nc=0;
	public SrvThread(Socket sct, KeyServer keyServer){
		super("Connect"+keyServer.LastID);
		id=keyServer.LastID;
		keyServer.LastID++;
		soc=sct;
		ks=keyServer;
		nc=0;
		System.out.println("Thread is Generated.  Connect to "+soc.getInetAddress());
	}
	public void run(){
		try{
			//socketからのデータはInputStreamReaderに送り、さらに
			//BufferedReaderによってバッファリングする。
			reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			//Clientへの出力用PrintWriter
			sendout = new PrintWriter(soc.getOutputStream(), true);
			while(!Close){
			//データ読み取りと表示。
			String key = reader.readLine();
			if(key==null) {
				if(nc>10) {
					if(soc != null){
						soc.close();
					}
					Close=true;
				}
				nc++;
			}
			if(ks.log)System.out.println("Key from client :" + key);
			try{
				ks.event(key);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			String Value="Error";
			if(ks==null)Value="ServerNull";
			else if(ks.map==null)Value="MapNull";
			else if(key!=null){
				nc=0;
				Value=ks.map.get(key).run();
				if(Value==null)Value="Null";
				if(Value.isEmpty())Value="Null";
			}
			//Clientにメッセージ送信
			sendout.println(Value);
			}
		}catch(IOException ioex){
			ioex.printStackTrace();
			try{
				if(soc != null){
					soc.close();
				}
			}catch (IOException ex){
				ex.printStackTrace();
			}
		}
	}
}