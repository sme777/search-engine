import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Class responsible for creating an index
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class InvertedIndexBuilder {

    /**
     * InvertedIndex object declared
     */
    private final InvertedIndex index;

    /**
     * Constructor
     *
     * @param index InvertedIndex
     */
    public InvertedIndexBuilder(InvertedIndex index) {
        this.index = index;
    }

    /**
     * Method for building an index
     *
     * @param inputFile input file
     * @throws IOException throws IO Exception if there are issues reading the file
     *
     */
    public void buildIndex(Path inputFile) throws IOException {

        if (Files.isDirectory(inputFile)) {

            List<Path> listOfPaths = TextFileFinder.list(inputFile);
            for (Path path : listOfPaths) {
                buildFromFile(path);

            }
        } else {
            buildFromFile(inputFile);

        }
    }

    /**
     * Builds an inverted index from a specified file
     *
     * @param inputFile file path
     * @throws IOException if input/output errors occur
     */
    public void buildFromFile(Path inputFile) throws IOException {
        InvertedIndexBuilder.buildIndex(inputFile, index);
    }

    /**
     * Builds index directly on inverted index
     *
     * @param inputFile  file that need processing
     * @param indexLocal given inverted index
     * @throws IOException occurs when the buffer cannot read the file
     */
    public static void buildIndex(Path inputFile, InvertedIndex indexLocal) throws IOException {
        SnowballStemmer stemmer = new SnowballStemmer(TextFileStemmer.DEFAULT);
        try (BufferedReader br = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
            String line;
            int count = 1;
            String fileName = inputFile.toString();
            while ((line = br.readLine()) != null) {
                String[] cleanedArray = TextParser.parse(line);
                for (String word : cleanedArray) {
                    String stemmedWord = stemmer.stem(word).toString();
                    indexLocal.add(stemmedWord, fileName, count);
                    count++;
                }
            }
        }
    }

}