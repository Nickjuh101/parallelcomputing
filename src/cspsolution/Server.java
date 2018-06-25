package cspsolution;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Nick on 0022 22 juni 2018.
 */
public class Server extends UnicastRemoteObject implements Service {

    public static final String DefaultMasterNodeName = "localhost";
    public static final String ServiceName = "SimpleMessenger";
    public static final int Port = 1199;

    private static String localHostname;

    public Server() throws RemoteException {
    }

    public static Service connect(String host) {

        Service remoteService = null;

        try {
            System.out.println("Connecting to " + host);

            Registry registry = LocateRegistry.getRegistry(host, Server.Port);
            remoteService = (Service) registry.lookup(Server.ServiceName);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return remoteService;
    }

    public void ping() throws RemoteException{
        return;
    }

    public String sendMessage(String message) throws RemoteException {
        System.out.println(message + " received at " + localHostname);
        return message + " (ECHO)";
    }

    public <T> T executeTask(Task<T> t) throws RemoteException{
        System.out.println("Executing task");
        return t.execute();
    }

    public static void main(String[] args) {
        try {
            // get the hostname of this node
            localHostname = InetAddress.getLocalHost().getHostName();

            // start a new server object
            Server server = new Server();

            // start the registry service on this node
            Registry registry = LocateRegistry.createRegistry(Server.Port);

            // add binding to this server object and use a specific ServiceName to reference it
            registry.bind(Server.ServiceName, server);


            System.out.println(Server.ServiceName + " running on " + localHostname);

        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}


