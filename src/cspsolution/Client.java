package cspsolution;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.time.LocalDateTime;

/**
 * Created by Nick on 0022 22 juni 2018.
 */

class SlowTask implements Task<Boolean>, Serializable {

    long delay;

    public SlowTask(long delay) {
        this.delay = delay;
    }

    @Override
    public Boolean execute() {
        System.out.println("Start a slow task that takes " + delay + " ms.");
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Slow task done");
        return true;
    }

}


public class Client {

    public static void main(String[] args) throws RemoteException, java.net.UnknownHostException {
        // Who am I?
        String localHostname = InetAddress.getLocalHost().getHostName();
        System.out.println("This is host:" + localHostname );

        // What is the (default) host?
        String serviceHost = Server.DefaultMasterNodeName;

        // Use command line parameter to override the default host
        if (args.length > 0)
            serviceHost = args[0];

        // connect to the host and request the service
        Service service = Server.connect(serviceHost);

        // execute a remote method
        long t1, t2;

        t1 = System.currentTimeMillis();
        service.ping();
        t2 = System.currentTimeMillis();
        System.out.println("Ping took " + (t2-t1) + " ms.");

        t1 = System.currentTimeMillis();
        String greeting = service.sendMessage("Hello World at " + LocalDateTime.now());
        t2 = System.currentTimeMillis();

        System.out.println("Client side:" + greeting);
        System.out.println("SendMessage took " + (t2-t1) + " ms.");

        t1 = System.currentTimeMillis();
        Boolean test = service.executeTask(new SlowTask(3000));
        t2 = System.currentTimeMillis();
        System.out.println("Slow task execution took " + (t2-t1) + " ms.");
    }
}

