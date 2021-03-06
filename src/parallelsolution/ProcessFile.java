package parallelsolution;

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

    private static final int SIZE = 12000;
    private static final int CORE = 4;

    private static ExecutorService executorService = Executors.newFixedThreadPool(CORE);

    public void main() throws IOException {
        System.out.println("Enter the word you want to find: ");
        Scanner scan = new Scanner(System.in);
        findWord = scan.nextLine();

        System.out.println("Starting...");
        long startTime = System.currentTimeMillis();
        processFile("src/text.txt");

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
                    createThreadPool(results);
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
        createThreadPool(results);
        executorService.shutdown();
        results.clear();
    }

    /* SplitWorker class that */
    public static class Worker implements Callable<Integer> {

        private List<String> workerList;
        private int id;

        Worker(List<String> list, int id){
            this.workerList = list;
            this.id = id;
        }

        @Override
        public Integer call() throws Exception {
            int count = 0;
            for (int i = id * SIZE / CORE; i < (id + 1) * SIZE / CORE; i++) {
                    for (String element : workerList.get(i).split(" ")) {
                        if (element.equalsIgnoreCase(findWord)) {
                            count++;
                        }
                    }
            }
            return count;
        }
    }

    public static void createThreadPool(List<String> list) {
        List<Future<Integer>> futureResults = new ArrayList<Future<Integer>>();

        /* Create CORE-amount of running threads in the Threadpool */
        for (int i = 0; i < CORE; i++) {
            futureResults.add(executorService.submit(new Worker(list, i)));
        }

        /* Get the result of the running threads */
        for (Future<Integer> result : futureResults){
            try {
                wordsInText += result.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
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
