package soulib.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class KeyServer implements Runnable{
	public static int LastServerID;
	public ArrayList<SrvThread> Connect=new ArrayList<SrvThread>();
    public ServerSocket serverSoc = null;
	public HashMap<String, Event> map;
	public HashMap<String,Event> eventmap;
	private boolean flag;
	private int port;
	public boolean log=true;
	public int LastID;
	public int ServerID;
	private Thread thread;
	public KeyServer(int port){
		thread=new Thread(this,"Server"+LastServerID);
		this.port=port;
		ServerID=LastServerID;
		LastServerID++;
		map=new HashMap<String,Event>();
		eventmap=new HashMap<String,Event>();
		thread.start();
	}
	public void run(){
    try{
      //ソケットを作成
      serverSoc = new ServerSocket(port);
      flag=true;
      //クライアントからの接続を待機するaccept()メソッド。
      //accept()は、接続があるまで処理はブロックされる。
      //もし、複数のクライアントからの接続を受け付けるようにするには
      //スレッドを使う。
      //accept()は接続時に新たなsocketを返す。これを使って通信を行なう。
      System.out.println("StartServer");
      while(flag){
        Socket socket=null;
        socket = serverSoc.accept();
        //accept()は、クライアントからの接続要求があるまでブロックする。
        //接続があれば次の命令に移る。
        //スレッドを起動し、クライアントと通信する。
        SrvThread t = newThread(socket,this);
        t.start();
        LastID++;
        Connect.add(t);
        if(log)System.out.println("Waiting for New Connection. ");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally{
      try{
        if (serverSoc != null){
          serverSoc.close();
        }
      }
      catch (IOException ioex) {
        ioex.printStackTrace();
      }
    }
 }
	public int getPort(){
		return port;
	}
	protected SrvThread newThread(Socket socket, KeyServer keyServer){
		return new SrvThread(socket,keyServer);
	}
	public void event(String key){
		eventmap.get(key).run();
	}
}