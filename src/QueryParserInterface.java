import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Interface for Query Parser
 *
 * @author sme777
 * @author Samson Petrosyan
 *
 */
public interface QueryParserInterface {

    /**
     * Converts a seatch query to a JSON file
     *
     * @param path   path paramater
     * @param writer BufferedWriter writer
     * @throws IOException throw an IOException
     */
    void toJson(Path path, BufferedWriter writer) throws IOException;

    /**
     * Searches with the provided query line
     *
     * @param line given line
     */
    void search(String line);


    /**
     * Searches the queries from the provided file
     *
     * @param path given path
     * @throws IOException throws IOException if invalid path
     */
    default void search(Path path) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                search(line);
            }
        }
    }

}
