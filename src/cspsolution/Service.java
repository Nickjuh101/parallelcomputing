package cspsolution;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Nick on 0022 22 juni 2018.
 */
public interface Service extends Remote {
    void ping() throws RemoteException;
    String sendMessage(String message) throws RemoteException;
    <T> T executeTask(Task<T> t) throws RemoteException;
}
