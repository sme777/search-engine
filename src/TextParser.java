import java.text.Normalizer;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Utility class for parsing text in a consistent manner.
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class TextParser {
    /** Regular expression that matches any whitespace. **/
    public static final Pattern SPLIT_REGEX = Pattern.compile("(?U)\\p{Space}+");

    /** Regular expression that matches non-alphabetic characters. **/
    public static final Pattern CLEAN_REGEX = Pattern.compile("(?U)[^\\p{Alpha}\\p{Space}]+");

    /**
     * Cleans the text by removing any non-alphabetic characters (e.g. non-letters
     * like digits, punctuation, symbols, and diacritical marks like the umlaut) and
     * converting the remaining characters to lowercase.
     *
     * @param text the text to clean
     * @return cleaned text
     */
    public static String clean(String text) {
        String cleaned = Normalizer.normalize(text, Normalizer.Form.NFD);
        cleaned = CLEAN_REGEX.matcher(cleaned).replaceAll("");
        return cleaned.toLowerCase();
    }

    /**
     * Splits the supplied text by whitespaces.
     *
     * @param text the text to split
     * @return an array of {@link String} objects
     */
    public static String[] split(String text) {
        return text.isBlank() ? new String[0] : SPLIT_REGEX.split(text.strip());
    }

    /**
     * Parses the text into an array of clean words.
     *
     * @param text the text to clean and split
     * @return an array of {@link String} objects
     *
     * @see #clean(String)
     * @see #parse(String)
     */
    public static String[] parse(String text) {
        return split(clean(text));
    }

    /**
     * Merges text
     *
     * @param text first parameter
     * @return mergedText
     */
    public static String merge(String[] text) {
        StringBuilder mergedText = new StringBuilder();
        for (String element : text) {
            if (text[text.length - 1].equals(element)) {
                mergedText.append(element);
            } else {
                mergedText.append(element).append(" ");
            }
        }
        return mergedText.toString();
    }

    /**
     * Helper method for merge
     *
     * @param text inputed text
     * @return mergedText
     */
    public static String merge(TreeSet<String> text) {
        StringBuilder mergedText = new StringBuilder();
        for (String element : text) {
            if (text.last().equals(element)) {
                mergedText.append(element);
            } else {
                mergedText.append(element).append(" ");
            }
        }
        return mergedText.toString();
    }

}
