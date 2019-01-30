package soulib.net;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import soulib.windowLib.StandardWindow;

public class Server{

	private static StandardWindow window;
	public Registry registry;
	private int port;
	public static void main(String[] args){
		try{
			Server server = new Server(-1);
			class ro implements RemoteObject{
				private int X;
				public String Sample(String s, int i){
					X++;
					if(window!=null)window.addData(s+X);
					return i+s+i;
				}
			}
			server.addRemote(new ro(),"sample");
		}catch (RemoteException e){
			e.printStackTrace();
		}
		window=new StandardWindow(300,300);
	}
	public Server(int port) throws RemoteException{
		if(port<0)port=Registry.REGISTRY_PORT;
		this.port=port;
		registry = LocateRegistry.createRegistry(port);
	}
	public void addRemote(Remote r,String name) throws AccessException, RemoteException,NullPointerException{
		r= UnicastRemoteObject.exportObject(r, 0);
		registry.rebind(name, r);
	}
	public void delRemote(String name) throws AccessException, RemoteException, NotBoundException,NullPointerException{
		registry.unbind(name);
	}
	public int getPort(){
		return port;
	}
}
