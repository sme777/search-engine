import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sme7777
 * @author Samson Petrosyan
 */
public class WebCrawler {
    /**
     * Multi thread inverted index
     */
    private final ConcurrentInvertedIndex index;

    /**
     * Worker queue for web crawler
     */
    private final WorkQueue queue;

    /**
     * Number of redirect limits
     */
    private final int redirectLimit;

    /**
     * List of visited URLs
     */
    private final HashSet<URL> visitedLinks;

    /**
     * Constructor for web crawler
     *
     * @param index given inverted index
     * @param queue given worker queue
     * @param redirectLimit number of redirect limits
     */
    public WebCrawler(ConcurrentInvertedIndex index, WorkQueue queue, int redirectLimit) {
        this.index = index;
        this.queue = queue;
        this.redirectLimit = redirectLimit;
        this.visitedLinks = new HashSet<>();
    }

    /**
     * Crawls the web and make an inverted index
     *
     * @param seed provided url
     */
    public void crawl(URL seed) {
        synchronized (visitedLinks) {
            visitedLinks.add(seed);
        }
        queue.execute(new Crawler(seed));
    }
    /**
     * An inner class for Crawler task
     */
    private class Crawler implements Runnable {
        /**
         * Provided Worker url seed
         */
        private final URL seed;
        /**
         * A list of fetched URLs
         */
        private ArrayList<URL> urls;

        private Crawler(URL seed) {
            this.seed = seed;
            urls = new ArrayList<>();
        }

        @Override
        public void run() {
            try {
                String html = HttpsFetcher.fetchURL(seed).toString();
                if (html != null) {
                    synchronized (visitedLinks) {
                        urls = LinkParser.getValidLinks(seed, html);
                        for (URL link : urls) {
                            if (visitedLinks.size() == redirectLimit) {
                                break;
                            }
                            if (!visitedLinks.contains(link)) {
                                visitedLinks.add(link);
                                queue.execute(new Crawler(link));
                            }
                        }
                    }
                    String cleanedHTML = HtmlCleaner.stripHtml(html);
                    ArrayList<String> words = TextFileStemmer.listStems(cleanedHTML);
                    InvertedIndex local = new InvertedIndex();
                    local.addAll(words, seed.toString(), 1);
                    index.addAll(local);
                }

            } catch (UnknownHostException e) {
                System.out.println("UnknownHostException in Crawler.run()");
            } catch (MalformedURLException e) {
                System.out.println("MalformedURLException in Crawler.run()");
            } catch (IOException e) {
                System.out.println("IOException in Crawler.run()");
            }

        }

    }
}