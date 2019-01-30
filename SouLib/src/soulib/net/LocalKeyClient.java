package soulib.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalKeyClient extends KeyClient{

	private KeyServer server;
	public LocalKeyClient(KeyServer server,int port) throws UnknownHostException{
		super(null,port);
		this.server=server;
	}
	public void run(){
		try{
			Connect(InetAddress.getLoopbackAddress());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getValue(String key) throws IOException{
		if(log)System.out.println("Send key: "+key);
		String Value=server.map.get(key).run();
		if(log)System.out.println("Value from Server:" + Value);
		return Value;
	}
}
