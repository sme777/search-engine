import java.io.IOException;
import java.nio.file.Path;

/**
 * InvertedIndexBuilder for multi-threading
 *
 * @author sme777
 * @author Samson Petrosyan
 *
 */
public class ConcurrentInvertedIndexBuilder extends InvertedIndexBuilder {

    /**
     * WorkQueue queue object
     */
    private final WorkQueue queue;

    /**
     * index ConcurrentInvertedIndex object
     */
    private final ConcurrentInvertedIndex index;

    /**
     * The constructor for ConcurrentInvertedIndexBuilder
     *
     * @param index ConcurrentInvertedIndex parameter
     * @param queue WorkQueue parameter
     */
    public ConcurrentInvertedIndexBuilder(ConcurrentInvertedIndex index, WorkQueue queue) {
        super(index);
        this.index = index;
        this.queue = queue;
    }

    /**
     * Builds an index concurrently by a worker queue execution
     *
     * @param inputFile Path inputFile
     * @throws IOException throws an IOException
     */
    @Override
    public void buildIndex(Path inputFile) throws IOException {
        super.buildIndex(inputFile);
        queue.finish();
    }

    @Override
    public void buildFromFile(Path inputFile) throws IOException {
        queue.execute(new Builder(inputFile));
    }

    /**
     * Builder class for processing each file
     */
    private class Builder implements Runnable {
        /**
         * Path of the given file
         */
        private final Path path;

        /**
         * constructor for builder class
         *
         * @param path required path for making the ConcurrentInvertedIndex
         */
        public Builder(Path path) {
            this.path = path;
        }

        @Override
        public void run() {
            try {
                InvertedIndex secondIndex = new InvertedIndex();
                InvertedIndexBuilder.buildIndex(path, secondIndex);
                index.addAll(secondIndex);
            } catch (IOException e) {
                System.out.println("Builder failed to add a new InvertedIndex with path: " + path);
            }
        }
    }
}
