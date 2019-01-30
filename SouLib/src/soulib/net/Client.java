package soulib.net;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	public static void main(String[] args){
		new Client("localhost", -1);
	}
	public Registry registry;
	public Client(String host,int RegistryPort) {
		try {
			if(RegistryPort<0)RegistryPort=Registry.REGISTRY_PORT;
			registry = LocateRegistry.getRegistry(host,RegistryPort);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	public Remote getRemote(String name) throws AccessException, RemoteException, NotBoundException{
		return registry.lookup(name);
	}
}