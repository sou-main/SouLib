package soulib.net;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteObject extends Remote{

	String Sample(String string,int i)throws RemoteException;

}