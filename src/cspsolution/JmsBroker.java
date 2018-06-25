package cspsolution;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Nick on 0025 25 juni 2018.
 */
public class JmsBroker {
    public static void main(String[] args) throws URISyntaxException, Exception {
        BrokerService broker = BrokerFactory.createBroker(new URI(
                "broker:(tcp://localhost:61616)"));
        broker.start();
    }
}
