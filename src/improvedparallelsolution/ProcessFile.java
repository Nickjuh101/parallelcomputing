package improvedparallelsolution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;


/**
 * Created by Nick on 0016 16 mei 2018.
 */
public class ProcessFile {

    private static int wordsInText = 0;
    private static String findWord;
    private static String textLine;
    private static List<String> results = new ArrayList<String>();
    private static volatile int count = 0;

    private static final int SIZE = 20;
    private static final int CORE = 4;

    private static BlockingQueue<List<String>> arrayBlockingQueueInput = new ArrayBlockingQueue<>(15);

    private static ExecutorService executorService = Executors.newFixedThreadPool(CORE);

    public void main() throws IOException {
        System.out.println("Enter the word you want to find: ");
        Scanner scan = new Scanner(System.in);
        findWord = scan.nextLine();

        System.out.println("Starting...");
        long startTime = System.currentTimeMillis();
        processFile("src/testText.txt");

        long stopTime = System.currentTimeMillis();

        System.out.println("The word " + findWord + " appears " + wordsInText + " times in the given text");

        System.out.println("Elapsed time was " + (stopTime - startTime) + " milliseconds.");
    }

    private static void processFile(String fileName) throws IOException {

        FileReader file = new FileReader(fileName);
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
                    fillBlockingQueue(results);
//                    createThreadPool(results);
                    workBlockingQueue(arrayBlockingQueueInput);
                    results.clear();
                }
            }
        }
        /* Count the remaining words in the list
        *  (last lines of the file do perhaps not fill up until the given SIZE, therefore need to be counted here)
        *  Fill the list with empty items if the size of the list does not match with the given SIZE */
        while(results.size() != SIZE){
            results.add("");
        }
//        createThreadPool(results);
        workBlockingQueue(arrayBlockingQueueInput);
        executorService.shutdown();
        results.clear();
    }

    public static class Consumer implements Runnable{

        private final BlockingQueue queue;

        public Consumer(BlockingQueue q) {
            queue = q;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    consume((List<String>) queue.take());
                }
            } catch (InterruptedException ex){
                // Catch ex
            }
        }

        void consume(List<String> list){
            int count = 0;
            for (int i = 0; i < list.size(); i++){
                for (String element : list.get(i).split(" ")){
                    if (element.equalsIgnoreCase(findWord)){
                        count++;
                    }
                }
            }
            wordsInText += count;
        }

    }
    /* Adds full lists to arrayBlockingQueue */
    public static void fillBlockingQueue(List<String> list){
        try {
            arrayBlockingQueueInput.put(list);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void workBlockingQueue(BlockingQueue blockingQueue){
//        List<Future<Integer>> futureResults = new ArrayList<Future<Integer>>();
        Consumer consumer = new Consumer(blockingQueue);
        for (int i = 0; i < CORE; i++){
            executorService.execute(consumer);
        }
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

