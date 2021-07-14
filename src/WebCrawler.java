import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sme777
 * @author Samson Petrosyan
 *
 */
public class WebCrawler {
    /**
     * multi thread inverted index
     */
    private final ConcurrentInvertedIndex index;

    /**
     * worker queue for web crawler
     */
    private final WorkQueue queue;

    /**
     * number of redirect limits
     */
    private final int redirectLimit;

    /**
     * list of visited URLs
     */
    private HashSet<URL> visitedLinks;

    /**
     * constructor for web crawler
     *
     * @param index given inverted index
     * @param queue given worker queue
     * @param redirectLimit number of redirect limits
     */
    public WebCrawler(ConcurrentInvertedIndex index, WorkQueue queue, int redirectLimit) {
        this.index = index;
        this.queue = queue;
        this.redirectLimit = redirectLimit;//max num of links to process
        this.visitedLinks = new HashSet<>();
    }

    /**
     * builds an inverted index from a specified url
     *
     * @param url given URL
     * @throws MalformedURLException throws an MalformedURLException if improper URL
     */
    public void buildFromURL(URL url) throws MalformedURLException {
        String htmlContents = HtmlFetcher.fetch(url, 3);
        if(htmlContents == null) {
            return;
        }
        htmlContents = HtmlCleaner.stripBlockElements(htmlContents);
        ArrayList<URL> parsedURLs = LinkParser.getValidLinks(url, htmlContents);
        htmlContents = HtmlCleaner.stripHtml(htmlContents);
        //System.out.println(htmlContents);
        //System.out.println(htmlContents);
        //Pattern pattern = Pattern.compile("href=[\"'](.*?)[\"']"); //look at linkparser!
        //Matcher matcher = pattern.matcher(htmlContents);


        crawl(url, htmlContents);
        for(URL newURL: parsedURLs) {
            if(visitedLinks.contains(newURL)) {
                continue;
            }
            if(visitedLinks.size() >= redirectLimit) {
                break;
            }
            buildFromURL(newURL);
        }
    }

    /**
     * @param url
     * @param path
     * @return
     * @throws MalformedURLException
     */
    private URL makeURL(String url, String path) throws MalformedURLException{
        Pattern pattern = Pattern.compile("http.*\\/"); //dont need regex
        Pattern pattern2 = Pattern.compile("(.*)#");
        Matcher matcher = pattern.matcher(url);
        Matcher matcher2 = pattern2.matcher(path);
        if(matcher.find()) {
            String cleanURL = matcher.group();
            if(matcher2.find()) {
                String cleanPath = matcher2.group(1);
                //System.out.println(path);
                //System.out.println(cleanPath);
                return new URL(cleanURL + cleanPath);
            }
            return new URL(cleanURL + path);
        }
        return null;
    }

    /**
     * executes a worker for a given URL
     *
     * @param url given URL
     * @param htmlContents
     */
    public void crawl(URL url, String htmlContents) {
        //visitedLinks.add(url); //need to add links here before calling run()
        queue.execute(new Crawler(url, htmlContents));
    }

    /**
     * @author dudesqueak
     *
     */
    private class Crawler implements Runnable{
        /**
         *
         */
        private URL url;

        /**
         *
         */
        private String htmlContents;

        /**
         *
         */
        private InvertedIndex localIndex;

        /**
         * @param url
         * @param htmlContents
         */
        private Crawler(URL url, String htmlContents) {
            this.url = url;
            this.localIndex = new InvertedIndex();
            this.htmlContents = htmlContents;
        }

        @Override
        public void run() { //alot of the work should be done here; this should benefit for multi thread when downloading web pages
            //System.out.println("gets here!");
            synchronized(visitedLinks) {
                if(visitedLinks.contains(url) || visitedLinks.size() >= redirectLimit) {
                    System.out.println(visitedLinks.size());
                    return;
                } else {
                    visitedLinks.add(url);
                }
            }
            //System.out.println("sfsv");
            ArrayList<String> htmlStemmed = TextFileStemmer.listStems(htmlContents);
            //System.out.println(htmlStemmed);
            localIndex.addAll(htmlStemmed, url.toString(), 1);
            index.addAll(localIndex);
        }

    }

}