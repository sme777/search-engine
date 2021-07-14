import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class Driver {

    /**
     * Generates a output file for the given path
     *
     * @param outputFile it's the output
     * @param index      it's the InvertedIndex object
     * @param type       type
     */
    public static void outputFile(Path outputFile, InvertedIndex index, Type type) {
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
            switch (type) {
                case COUNT:
                    index.countToJson(outputFile, writer);
                    break;
                default:
                    index.toJson(outputFile, writer);
            }
        } catch (IOException e) {
            System.out.println("Error when writing to the file " + e.getMessage());
        }
    }

    /**
     * Generates a output file for the given path
     *
     * @param outputFile it's the output
     * @param parser     it's the query parser
     */
    public static void outputFile(Path outputFile, QueryParserInterface parser) {
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
            parser.toJson(outputFile, writer);
        } catch (IOException e) {
            System.out.println("Error when writing to the file " + e.getMessage());
        }
    }

    /**
     * Initializes the classes necessary based on the provided command-line
     * arguments. This includes (but is not limited to) how to build or search an
     * inverted index.
     *
     * @param args flag/value pairs used to start this program
     */
    public static void main(String[] args) {
        ArgumentMap map = new ArgumentMap(args);
        InvertedIndex index;
        InvertedIndexBuilder builder;
        QueryParserInterface parser;
        WorkQueue queue = null;
        boolean isMultiThread = map.hasFlag("-threads");
        boolean isExactSearch = map.hasFlag("-exact");
        if (isMultiThread) {
            int numOfThreads = map.getInteger("-threads", 5);
            if (numOfThreads <= 0) {
                return;
            }
            ConcurrentInvertedIndex concurrent = new ConcurrentInvertedIndex();
            index = concurrent;
            queue = new WorkQueue(numOfThreads);
            builder = new ConcurrentInvertedIndexBuilder(concurrent, queue);
            parser = new ConcurrentQueryParser(concurrent, isExactSearch, queue);

        } else {
            index = new InvertedIndex();
            builder = new InvertedIndexBuilder(index);
            parser = new QueryParser(index, isExactSearch);
        }

        if (map.hasFlag("-text")) {
            Path path = map.getPath("-text");
            if (path == null) {
                System.out.println("NO path(s) specified!");
                return;
            }
            try {
                builder.buildIndex(path);
            } catch (IOException e) {
                System.out.println("Unable to build the inverted index from the path: " + path);
            }
        }

        if(map.hasFlag("-html")) {
            String seed = map.getString("-html");
            if(seed == null || seed == "") {
                System.out.println("URL is not provided!");
                return;
            }
            if(!isMultiThread) {
                queue = new WorkQueue(5);
                builder = new ConcurrentInvertedIndexBuilder((ConcurrentInvertedIndex) index, queue);
            }
            try {
                int numURL = map.getInteger("-max", 1);
                WebCrawler crawler = new WebCrawler((ConcurrentInvertedIndex) index, queue, numURL);
                URL urlNew = new URL(seed);
                crawler.buildFromURL(urlNew);
                queue.finish();
            } catch (MalformedURLException e){
                System.out.println("Given URI is malformed!");
                return;
            }

        }

        if (map.hasFlag("-index")) {
            Path path = map.getPath("-index", Path.of("index.json"));
            outputFile(path, index, Type.INDEX);
        }

        if (map.hasFlag("-counts")) {
            Path countPath = map.getPath("-counts", Path.of("counts.json"));
            outputFile(countPath, index, Type.COUNT);
        }

        if (map.hasFlag("-query")) {
            Path queryPath = map.getPath("-query");
            if (queryPath == null) {
                System.out.println("Query Path:" + queryPath + " not provided!");
                return;
            }
            try {
                parser.search(queryPath);
            } catch(IOException e) {
                System.out.println("Unable to search query path: " + queryPath);
            }

        }

        if (map.hasFlag("-results")) {
            Path resultPath = map.getPath("-results", Path.of("results.json"));
            outputFile(resultPath, parser);
        }

        if (queue != null) {
            queue.shutdown();
        }
    }

    /**
     * Enum for types of outputs to JSON files
     *
     * @author dudesqueak
     *
     */
    private enum Type {
        /**
         * if -counts tag flag is present
         */
        COUNT,
        /**
         * if -results tag flag is present
         */
        RESULT,
        /**
         * if -index tag flag is present
         */
        INDEX
    }
}