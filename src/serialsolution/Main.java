package serialsolution;

import java.util.Scanner;

/**
 * Created by Nick on 0016 16 mei 2018.
 */
public class Main {

    private static int wordsInText = 0;
    private static int totalWordCount = 0;
    private static String findWord;
    private static String text;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        /* Ask for a text */
        System.out.println("Please enter a text you want to find a word in: ");
        text = scan.nextLine();

        /* Remove punctuation from the text, except of punctuation that is useful for certain words.
         * Examples of these words are don't or re-enter */
        text = text.replaceAll("[[\\W]&&[^']&&[^-]]", " ");

        /* Replace all double whitespaces with single whitespaces.
         * We will split the text on these whitespaces later */
        text = text.replaceAll("\\s\\s+"," ");

        /* Ask for a word to find in the text */
        System.out.println("Please enter the word you want to count in the text: ");
        findWord = scan.nextLine();

        /* Count total words in the text, count the amount of times the given word is in the text */
        for (String element : text.split(" ")){
            totalWordCount++;
            if (element.equalsIgnoreCase(findWord)){
                wordsInText++;
            }
        }

        /* Print the useful lines of the algorithm */
        System.out.println("The total word count in the text is: " + totalWordCount);
        System.out.println("The word " + findWord + " appears " + wordsInText + " times in the given text");
    }

}
