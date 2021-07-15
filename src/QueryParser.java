import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Parses through a query file
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class QueryParser implements QueryParserInterface {

    /**
     * QueryResults is the TreeMap object
     */
    private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> queryResults;

    /**
     * Generic function that either calls exact search or partial search
     */
    private final Function<Set<String>, ArrayList<InvertedIndex.SearchResult>> searchFunction;

    /**
     * Constructor of QueryParser
     *
     * @param index given an InvertedIndex
     * @param exact whether the search is exact or not
     */
    public QueryParser(InvertedIndex index, boolean exact) {
        this.queryResults = new TreeMap<>();
        this.searchFunction = exact ? index::exactSearch : index::partialSearch;
    }

    /**
     * Converts a search query to a JSON file
     *
     * @param path   path paramater
     * @param writer BufferedWriter writer
     * @throws IOException throw an IOException
     */
    @Override
    public void toJson(Path path, BufferedWriter writer) throws IOException {
        SimpleJsonWriter.asObject(queryResults, writer, 1);
    }

    /**
     * Searches inverted index line by line from provided query path
     *
     * @param line provided line
     */
    @Override
    public void search(String line) {
        TreeSet<String> cleanSet = TextFileStemmer.uniqueStems(line);
        if (cleanSet.isEmpty()) {
            return;
        }
        String joined = String.join(" ", cleanSet);
        if (queryResults.containsKey(joined)) {
            return;
        }
        ArrayList<InvertedIndex.SearchResult> results = searchFunction.apply(cleanSet);
        queryResults.put(joined, results);
    }

}