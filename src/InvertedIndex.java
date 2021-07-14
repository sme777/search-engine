import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class is responsible for creating the data structure for generating a
 * JSON file type of structure
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class InvertedIndex {

    /**
     * Index data storage object
     */
    private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

    /**
     * Map storing a number of counts for each file
     */
    private final TreeMap<String, Integer> countMap;

    /**
     * Constructor that initializes a InvertedTextFileStemmer object
     */
    public InvertedIndex() {
        index = new TreeMap<>();
        countMap = new TreeMap<>();
    }

    /**
     * Either creates a new location, makes a new treeSet with the given position Or
     * works on an old location, also an old treeSet with a new position Or adds a
     * new word to the index treeMap with a new location, and new treeSet position
     *
     * @param word     the first argument of the data structure
     * @param location the pathfile of the word / the second argument of the data
     *                 structure
     * @param position the position of the word / the last argument of the data
     *                 structure
     */
    public void add(String word, String location, int position) {
        index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
        index.get(word).putIfAbsent(location, new TreeSet<Integer>());
        boolean modified = index.get(word).get(location).add(position);

        if (modified) {
            int count = countMap.getOrDefault(location, 0) + 1;
            countMap.put(location, count);
        }

    }

    /**
     * Adds all words as a list with the same location and position as -1
     *
     * @param words    first argument
     * @param location second arguemnt/ location of the word
     * @param position third argument/ position of word
     */
    public void addAll(List<String> words, String location, int position) {
        int start = position;
        for (int i = 0; i < words.size(); i++, start++) {
            add(words.get(i), location, start);
        }
    }

    /**
     * Adds all words with their locations and positions from another inverted index
     *
     * @param secondIndex the other inverted index
     */
    public void addAll(InvertedIndex secondIndex) {
        for (String word : secondIndex.index.keySet()) {
            if (!this.index.containsKey(word)) {
                this.index.put(word, secondIndex.index.get(word));
            } else {
                for (String location : secondIndex.index.get(word).keySet()) {
                    if (this.index.get(word).containsKey(location)) {
                        this.index.get(word).get(location).addAll(secondIndex.index.get(word).get(location));
                    } else {
                        this.index.get(word).put(location, secondIndex.index.get(word).get(location));
                    }
                }
            }
        }
        for (String location : secondIndex.countMap.keySet()) {
            if (this.countMap.containsKey(location)) {
                int count = this.countMap.get(location) + secondIndex.countMap.get(location);
                this.countMap.put(location, count);
            } else {
                this.countMap.put(location, secondIndex.countMap.get(location));
            }
        }
    }

    /**
     * Converts data in a specified file into a Json format
     *
     * @param path   output file path
     * @param writer buffered writer
     * @throws IOException throws an exception if invertedIndexJson throws an
     *                     exception
     */
    public void toJson(Path path, BufferedWriter writer) throws IOException {
        SimpleJsonWriter.invertedIndexJson(index, writer, 0);
    }

    /**
     * Converts data in a specified file into a Json format
     *
     * @param path   output file path
     * @param writer buffered writer
     * @throws IOException throws an exception if invertedIndexJson throws an
     *                     exception
     */
    public void countToJson(Path path, BufferedWriter writer) throws IOException {
        SimpleJsonWriter.asObject(countMap, writer, 0);
    }

    /**
     * Does an exact Search on inverted index
     *
     * @param queries paramater passed for queries
     * @return results
     */
    public ArrayList<SearchResult> exactSearch(Set<String> queries) {
        ArrayList<SearchResult> results = new ArrayList<SearchResult>();
        HashMap<String, SearchResult> lookupMap = new HashMap<>();
        for (String word : queries) {
            if (index.containsKey(word)) {
                performSearch(word, lookupMap, results);
            }
        }
        Collections.sort(results);
        return results;
    }

    /**
     * Does a partial search on an inverted index
     *
     * @param queries paramater passed for queries
     * @return results
     */
    public ArrayList<SearchResult> partialSearch(Set<String> queries) {
        ArrayList<SearchResult> results = new ArrayList<SearchResult>();
        HashMap<String, SearchResult> rMap = new HashMap<>();
        for (String word : queries) {
            Set<String> wordSet = index.tailMap(word).keySet();
            for (String key : wordSet) {
                if (key.startsWith(word)) {
                    performSearch(key, rMap, results);
                } else {
                    break;
                }
            }
        }
        Collections.sort(results);
        return results;
    }

    /**
     * Searches in inverted index for specified query
     *
     * @param match     word looking for
     * @param resultMap input a result map
     * @param results   input an ArrayList of result
     */
    private void performSearch(String match, HashMap<String, SearchResult> resultMap, ArrayList<SearchResult> results) {
        Set<String> locationSet = index.get(match).keySet();
        for (String location : locationSet) {
            if (resultMap.containsKey(location)) {
                resultMap.get(location).update(match);
            } else {
                SearchResult sr = new SearchResult(location, match);
                resultMap.put(location, sr);
                results.add(sr);
            }
        }
    }

    @Override
    public String toString() {
        return index.toString();
    }

    /**
     * Gives the set of all the words in the inverted index
     *
     * @return a set of words
     */
    public Set<String> getWordSet() {
        return Collections.unmodifiableSet(index.keySet());
    }

    /**
     * Returns a set of locations of a given word
     *
     * @param word given word
     * @return map of location
     */
    public Set<String> getLocationSet(String word) {
        if (index.containsKey(word)) {
            return Collections.unmodifiableSet(index.get(word).keySet());
        }

        return Collections.emptySet();
    }

    /**
     * Returns a set of positions for a given location of a word
     *
     * @param word     given word
     * @param location given location
     * @return TreeSet of postitions
     */
    public Set<Integer> getPositionSet(String word, String location) {
        if (index.containsKey(word) && index.get(word).containsKey(location)) {
            return Collections.unmodifiableSet(index.get(word).get(location));
        }
        return Collections.emptySet();
    }

    /**
     * Returns the number of words in inverted index
     *
     * @return number of words in inverted index
     */
    public int size() {
        return index.size();
    }

    /**
     * Returns number of locations of that word in inverted index
     *
     * @param word given word
     * @return number of locations of that word
     */
    public int size(String word) {
        if (contains(word)) {
            return index.get(word).size();
        }

        return 0;
    }

    /**
     * Returns number of positions of that word in the inverted index
     *
     * @param word     given word
     * @param location given location
     * @return number of positions of that word
     */
    public int size(String word, String location) {
        if (contains(word) && contains(word, location)) {
            return index.get(word).size();
        }

        return 0;
    }

    /**
     * Check to see if word is contained in the inverted index
     *
     * @param word given word
     * @return true or false if contains or not
     */
    public boolean contains(String word) {
        return index.containsKey(word);
    }

    /**
     * Check to see if location of given word exists in inverted index
     *
     * @param word     given word
     * @param location location of word
     * @return true or false if contains or not
     */
    public boolean contains(String word, String location) {
        return index.containsKey(word) && index.get(word).containsKey(location);
    }

    /**
     * Check to see if position of the location of the given word exists in inverted
     * index
     *
     * @param word     given word
     * @param location given location of word
     * @param position position of the word in the given location of the given word
     * @return true or false if contains or not
     */
    public boolean contains(String word, String location, int position) {
        return index.containsKey(word) && index.get(word).containsKey(location)
                && index.get(word).get(location).contains(position);
    }

    /**
     * Result of searching a specific query
     *
     * @author dudesqueak
     */
    public class SearchResult implements Comparable<SearchResult> {

        /**
         * Number of matches in a given text file
         */
        private int numMatches;

        /**
         * Computed score for the given text file
         */
        private double score;

        /**
         * Location of the given text file
         */
        private final String where;

        /**
         * Constructor of SearchResult with location and word
         *
         * @param where location of text file
         * @param match word to look for
         */
        public SearchResult(String where, String match) {
            this.where = where;
            this.numMatches = 0;
            this.score = 0;
            update(match);
        }

        /**
         * Update the score and number of matches
         *
         * @param match the given word
         */
        private void update(String match) {
            this.numMatches += index.get(match).get(where).size();
            this.score = (double) this.numMatches / countMap.get(where);
        }

        /**
         * Getter method for numMatches
         *
         * @return numMatches
         */
        public int getNumMatches() {
            return numMatches;
        }

        /**
         * Getter method for score
         *
         * @return score
         */
        public double getScore() {
            return score;
        }

        /**
         * Getter method for location
         *
         * @return where
         */
        public String getWhere() {
            return where;
        }

        /**
         * Check if two search results are the same
         *
         * @param sr search result
         * @return properties of search result
         */
        public boolean equals(SearchResult sr) {
            return sr.getNumMatches() == numMatches && sr.getWhere() == where && sr.getScore() == score;
        }

        @Override
        public int compareTo(InvertedIndex.SearchResult other) {
            int result = Double.compare(other.getScore(), this.getScore());
            int otherWordCount = countMap.get(other.getWhere());
            int thisWordCount = countMap.get(this.getWhere());
            if (result == 0) {
                result = Integer.compare(otherWordCount, thisWordCount);

                if (result == 0) {
                    result = this.getWhere().compareToIgnoreCase(other.getWhere());
                }
            }

            return result;
        }

    }

}