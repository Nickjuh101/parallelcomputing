package cspsolution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;


/**
 * Created by Nick on 0016 16 mei 2018.
 */
public class ProcessFile {

    private static int wordsInText = 0;
    private static String findWord;
    private static String textLine;
    private static List<String> results = new ArrayList<String>();

    private static final int BLOCKINGQUEUESIZE = 15;

    private static final int SIZE = 12000;
    private static final int CORE = 4;

    private static final String HALT = "HALT";

    private static BlockingQueue<List<String>> arrayBlockingQueueInput = new ArrayBlockingQueue<>(BLOCKINGQUEUESIZE);

    public static class Producer implements Runnable {

        @Override
        public void run() {
            try {
                FileReader file = new FileReader("src/text.txt");
                BufferedReader br = new BufferedReader(file);

                while ((textLine = br.readLine()) != null) {

                    if (textLine.isEmpty()) {
                        continue;
                    }

                    /* Remove punctuation from the text, except of punctuation that is useful for certain words.
                    * Examples of these words are don't or re-enter */
                    textLine = textLine.replaceAll("[[\\W]&&[^']&&[^-]]", " ");

                    /* Replace all double whitespaces with single whitespaces.
                    * We will split the text on these whitespaces later */
                    textLine = textLine.replaceAll("\\s\\s+", " ");

                    textLine = textLine.replaceAll("\\n", "").replaceAll("\\r", "");

                    if (results.isEmpty()) {
                        results.add(textLine);
                        continue;
                    }
                    if (results.size() <= SIZE) {
                        results.add(textLine);
                        if (results.size() == SIZE) {

                            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost/61616");
                            Destination destination = new ActiveMQQueue("SomeQueue");
                            Connection connection = connectionFactory.createConnection();
                            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                            try{
                                ObjectMessage objectMessage = session.createObjectMessage((Serializable) results);
                                MessageProducer producer = session.createProducer(destination);

                                producer.send(objectMessage);
                                session.close();
                                results.clear();
                            } finally {
                                if (connection != null){
                                    connection.close();
                                    results.clear();
                                }
                            }
                        }
                    }
                }
                /* Count the remaining words in the list
                 *  (last lines of the file do perhaps not fill up until the given SIZE, therefore need to be counted here)
                 *  Fill the list with empty items if the size of the list does not match with the given SIZE */
                while (results.size() != SIZE) {
                    results.add("");
                }
                arrayBlockingQueueInput.put(new ArrayList<String>(results));
                List<String> list = new ArrayList<String>();
                list.add(HALT);
                arrayBlockingQueueInput.put(list);
                results.clear();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }


    public static class Consumer implements Callable<Integer> {

        int count = 0;

        @Override
        public Integer call() throws Exception {

            try {
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost/61616");
                Destination destination = new ActiveMQQueue("SomeQueue");
                Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                try{
                    MessageConsumer consumer = session.createConsumer(destination);
                    connection.start();
                    ObjectMessage objectMessage = (ObjectMessage) consumer.receive();
                    if (objectMessage instanceof List){
                        List<String> list = new ArrayList<>();
                        do {
                            list = (List<String>) objectMessage;
                            if (!list.get(0).equals(HALT)) {
                                for (int i = 0; i < list.size(); i++) {
                                    for (String element : list.get(i).split(" ")) {
                                        if (element.equalsIgnoreCase(findWord)) {
                                            count++;
                                            session.close();
                                        }
                                    }
                                }
                            } else {

                            }
                        } while (!list.get(0).equals(HALT));
                        return count;
                    }
                } finally {
                    if (connection != null){
                        connection.close();
                    }
                }
                } catch (JMSException e) {
                e.printStackTrace();
            }
            return count;
        }
    }

    public void main() throws IOException, InterruptedException {
        System.out.println("Enter the word you want to find: ");
        Scanner scan = new Scanner(System.in);
        findWord = scan.nextLine();

        System.out.println("Starting...");
        long startTime = System.currentTimeMillis();
        Thread producer = new Thread(new Producer());
        producer.start();
        ExecutorService executorService = Executors.newFixedThreadPool(CORE);

        List<Future<Integer>> futureResults = new ArrayList<Future<Integer>>();

        for (int i = 0; i < CORE; i++) {
            futureResults.add(executorService.submit(new Consumer()));
        }

        executorService.shutdown();

        for (Future<Integer> result : futureResults) {
            try {
                wordsInText += result.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        producer.join();

        long stopTime = System.currentTimeMillis();

        System.out.println("The word " + findWord + " appears " + wordsInText + " times in the given text");

        System.out.println("Elapsed time was " + (stopTime - startTime) + " milliseconds.");
    }

    /* Saved for possible later use
    * Counts the total words in a given string */
    public static int countWords(String s) {

        int wordCount = 0;

        boolean word = false;
        int endOfLine = s.length() - 1;

        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;

                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;

    }
}

