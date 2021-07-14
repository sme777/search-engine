import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses URL links from the anchor tags within HTML text.
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class LinkParser {

    /**
     * REGEX pattern checks for a href and a link
     */
    private static final String HTML_A_HREF_TAG_PATTERN = "(?mis)(?:<\\s*a[^>]*href\\s*=\\s*\")([^\"]*)";

    /**
     * Removes the fragment component of a URL (if present), and properly encodes
     * the query string (if necessary).
     *
     * @param url the url to normalize
     * @return normalized url
     * @throws URISyntaxException    if unable to craft new URI
     * @throws MalformedURLException if unable to craft new URL
     */
    public static URL normalize(URL url) throws MalformedURLException, URISyntaxException {
        return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
                url.getQuery(), null).toURL();
    }

    /**
     * Returns a list of all the valid HTTP(S) links found in the href attribute of
     * the anchor tags in the provided HTML. The links will be converted to absolute
     * using the base URL and normalized (removing fragments and encoding special
     * characters as necessary).
     *
     * Any links that are unable to be properly parsed (throwing an
     * {@link MalformedURLException}) or that do not have the HTTP/S protocol will
     * not be included.
     *
     * @param base the base url used to convert relative links to absolute3
     * @param html the raw html associated with the base url
     * @return list of all valid http(s) links in the order they were found
     */
    public static ArrayList<URL> getValidLinks(URL base, String html) {

        Pattern pattern = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
        Matcher regexMatcher = pattern.matcher(html);
        ArrayList<URL> validLinks = new ArrayList<>();

        while (regexMatcher.find()) {
            try {
                URL newUrl;
                String href = regexMatcher.group(1); // href
                newUrl = normalize(new URL(base, href));
                if (newUrl.getProtocol().startsWith("https") || newUrl.getProtocol().startsWith("http")) {
                    validLinks.add(newUrl);
                }
            } catch (MalformedURLException | URISyntaxException e) {
                System.out.println("Invalid Url");
            }

        }
        return validLinks;
    }
}