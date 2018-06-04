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
public class Worker {

    private static int wordsInText = 0;
    private static String findWord;
    private static String textLine;
    private static final int SIZE = 12000;
    private static final int CORE = 4;
    private static List<String> results = new ArrayList<String>();
    private static volatile int count = 0;

    public static synchronized void increment() {
        count++;
    }

    public void main() throws IOException {
        System.out.println("Starting...");
        long start = System.currentTimeMillis();

        System.out.println("Enter the word you want to find: ");
        Scanner scan = new Scanner(System.in);
        findWord = scan.nextLine();

        processFile("src/testText.txt");

        System.out.println("The word " + findWord + " appears " + wordsInText + " times in the given text");

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
            }
            if (results.size() <= 10) {
                results.add(textLine);
                wordsInText = (countWithThreads(results));
                results.clear();
            }
        }
    }

    public static void createThreadPool(){
        ExecutorService executorService = Executors.newFixedThreadPool(CORE);

        ArrayList<Future<Integer>> futureResults = new ArrayList<Future<Integer>>();

        for (int i = 0; i < SIZE; i++){

        }

        for (Future<Integer> result : futureResults)
            try
            {
                count += result.get();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return null;
            }
        });


    }


    public static int countWithThreads(List<String> list) {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size() / 2; i++) {
                    for (String element : list.get(i).split(" ")) {
                        if (element.equalsIgnoreCase(findWord)) {
                            increment();
                        }
                    }
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = list.size() / 2; i < list.size(); i++) {
                    for (String element : list.get(i).split(" ")) {
                        if (element.equalsIgnoreCase(findWord)) {
                            increment();
                        }
                    }
                }
            }
        });

        t1.start();
//        t2.start();

        try {
            t1.join();
//            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return count;
    }


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
