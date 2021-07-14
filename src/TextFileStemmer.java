import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @author sme777
 * @author Samson Petrosyan
 *
 * @see TextParser
 */
public class TextFileStemmer {
    /** The default stemmer algorithm used by this class. */
    public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

    /**
     * Returns a list of cleaned and stemmed words parsed from the provided line.
     *
     * @param line    the line of words to clean, split, and stem
     * @param stemmer the stemmer to use
     * @return a list of cleaned and stemmed words
     *
     * @see Stemmer#stem(CharSequence)
     * @see TextParser#parse(String)
     */
    public static ArrayList<String> listStems(String line, Stemmer stemmer) {
        ArrayList<String> stems = new ArrayList<>();
        stemLine(line, stemmer, stems);
        return stems;
    }

    /**
     * Returns a list of cleaned and stemmed words parsed from the provided line.
     *
     * @param line the line of words to clean, split, and stem
     * @return a list of cleaned and stemmed words
     *
     * @see SnowballStemmer
     * @see #DEFAULT
     * @see #listStems(String, Stemmer)
     */
    public static ArrayList<String> listStems(String line) {
        return listStems(line, new SnowballStemmer(DEFAULT));
    }

    /**
     * Reads a file line by line, parses each line into cleaned and stemmed words,
     * and then adds those words to a list.
     *
     * @param inputFile the input file to parse
     * @return a sorted set of stems from file
     * @throws IOException if unable to read or parse file
     *
     * @see #uniqueStems(String)
     * @see TextParser#parse(String)
     */
    public static ArrayList<String> listStems(Path inputFile) throws IOException {
        ArrayList<String> listStems = new ArrayList<>();
        SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
        try (BufferedReader br = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                stemLine(line, stemmer, listStems);
            }
        }
        return listStems;
    }

    /**
     * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
     * the provided line.
     *
     * @param line the line of words to clean, split, and stem
     * @return a sorted set of unique cleaned and stemmed words
     *
     * @see SnowballStemmer
     * @see #DEFAULT
     * @see #uniqueStems(String, Stemmer)
     */
    public static TreeSet<String> uniqueStems(String line) {
        return uniqueStems(line, new SnowballStemmer(DEFAULT));
    }

    /**
     * Returns a set of unique (no duplicates) cleaned and stemmed words parsed from
     * the provided line.
     *
     * @param line    the line of words to clean, split, and stem
     * @param stemmer the stemmer to use
     * @return a sorted set of unique cleaned and stemmed words
     *
     * @see Stemmer#stem(CharSequence)
     * @see TextParser#parse(String)
     */
    public static TreeSet<String> uniqueStems(String line, Stemmer stemmer) {
        TreeSet<String> stems = new TreeSet<>();
        stemLine(line, stemmer, stems);
        return stems;
    }

    /**
     * Reads a file line by line, parses each line into cleaned and stemmed words,
     * and then adds those words to a set.
     *
     * @param inputFile the input file to parse
     * @return a sorted set of stems from file
     * @throws IOException if unable to read or parse file
     *
     * @see #uniqueStems(String)
     * @see TextParser#parse(String)
     */

    public static TreeSet<String> uniqueStems(Path inputFile) throws IOException {
        TreeSet<String> setStems = new TreeSet<>();
        SnowballStemmer stemmer = new SnowballStemmer(DEFAULT);
        try (BufferedReader br = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                stemLine(line, stemmer, setStems);
            }
        }
        return setStems;
    }

    /**
     * This method is used for cleaning stemmed word with the given stemmer and
     * existing stems.
     *
     * @param line    the line of words to clean, split, and stem
     * @param stemmer the stemmer to use
     * @param stems   collection of stemmed words
     */
    public static void stemLine(String line, Stemmer stemmer, Collection<String> stems) {
        String[] cleanedArray = TextParser.parse(line);
        for (String word : cleanedArray) {
            stems.add(stemmer.stem(word).toString());
        }
    }
}
