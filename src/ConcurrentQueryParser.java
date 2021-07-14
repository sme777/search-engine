import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Query Parser for multi threading
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class ConcurrentQueryParser implements QueryParserInterface {

    /**
     * QueryResults is the TreeMap object
     */
    private final TreeMap<String, ArrayList<InvertedIndex.SearchResult>> queryResults;

    /**
     * Generic function that either calls exact search or partial search
     */
    private final Function<Set<String>, ArrayList<InvertedIndex.SearchResult>> searchFunction;

    /**
     * Worker queue for parsing querys
     */
    private final WorkQueue queue;

    /**
     * Constructor for ConcurrentQueryParser
     *
     * @param index provided ConcurrentInvertedIndex
     * @param exact checks to see if it's an exact or partial search
     * @param queue worker queue
     */
    public ConcurrentQueryParser(ConcurrentInvertedIndex index, boolean exact, WorkQueue queue) {
        this.queue = queue;
        this.searchFunction = exact ? index::exactSearch : index::partialSearch;
        this.queryResults = new TreeMap<>();
    }

    /**
     * Converts a seatch query to a JSON file
     *
     * @param path   path paramater
     * @param writer BufferedWriter writer
     * @throws IOException throw an IOException
     */
    @Override
    public void toJson(Path path, BufferedWriter writer) throws IOException {
        SimpleJsonWriter.asObject(queryResults, writer, 1);
    }

    @Override
    public void search(Path path) throws IOException {
        QueryParserInterface.super.search(path);
        queue.finish();
    }

    @Override
    public void search(String line) {
        queue.execute(new Searcher(line));
    }

    /**
     * Searcher class for processing each search
     */
    private class Searcher implements Runnable {

        /**
         * Query for searching
         */
        private final String query;

        /**
         * Constructor for Searcher class
         *
         * @param query query parameter
         */
        public Searcher(String query) {
            this.query = query;
        }

        @Override
        public void run() {
            TreeSet<String> cleanSet = TextFileStemmer.uniqueStems(query);
            if (cleanSet.isEmpty()) {
                return;
            }
            String joined = String.join(" ", cleanSet);
            synchronized (queryResults) {
                if (queryResults.containsKey(joined)) {
                    return;
                }
            }

            ArrayList<InvertedIndex.SearchResult> results = searchFunction.apply(cleanSet);
            synchronized (queryResults) {
                queryResults.put(joined, results);
            }
        }
    }

}