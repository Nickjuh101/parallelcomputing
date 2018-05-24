package parallelsolution;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by Nick on 0016 16 mei 2018.
 */
public class Worker {

    private Object lock1 = new Object();
    private Object lock2 = new Object();
    private Object lock3 = new Object();

    private static int wordsInText = 0;
    private static int totalWordCount = 0;
    private static String findWord;
    private static String text;
    private static List<String> results = new ArrayList<String>();
    private static int count = 0;


    public void stageOne() {
        synchronized (lock1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stageTwo() {
        synchronized (lock2) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stageThree() {
        synchronized (lock3) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void process() {
        stageOne();
        stageTwo();
        stageThree();
    }

    public void main() throws IOException {
        System.out.println("Starting...");
        long start = System.currentTimeMillis();
        processFile("src/text.txt");

        System.out.println(count);

        Scanner findword = new Scanner(System.in);
    }

    private static void processFile(String fileName) throws IOException {

        FileReader file = new FileReader(fileName);
        BufferedReader br = new BufferedReader(file);

        String text = "";
        while ((text = br.readLine()) != null) {
           /* Remove punctuation from the text, except of punctuation that is useful for certain words.
         * Examples of these words are don't or re-enter */
            text = text.replaceAll("[[\\W]&&[^']&&[^-]]", " ");

        /* Replace all double whitespaces with single whitespaces.
         * We will split the text on these whitespaces later */
            text = text.replaceAll("\\s\\s+", " ");

            text = text.replace("\\n", "").replace("\\r", "");

            count += countWords(text);
//            Pattern p = Pattern.compile("\\b.{1," + (50 - 1) + "}\\b\\W?");
//            Matcher m = p.matcher(text);
//
//            while (m.find()) {
//                m.group().trim();
//                results.add(m.group());
//            }
        }
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


//        Thread t1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                process();
//            }
//        });
//        Thread t2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                process();
//            }
//        });
//        Thread t3 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                process();
//            }
//        });
//
//        t1.start();
//        t2.start();
//        t3.start();
//
//        try {
//            t1.join();
//            t2.join();
//            t3.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        long end = System.currentTimeMillis();
//
//        System.out.println("Time take: " + (end - start));


}
