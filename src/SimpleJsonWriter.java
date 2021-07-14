import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class SimpleJsonWriter {

    /**
     * Writes the elements as a pretty JSON array.
     *
     * @param iterator the elements to iterate
     * @param writer   the writer to use
     * @param level    the initial indent level
     * @throws IOException if an IO error occurs
     */
    public static void asArray(Iterator<Integer> iterator, Writer writer, int level) throws IOException {
        writer.write("[\n");

        if (iterator.hasNext()) {
            indent(Integer.toString(iterator.next()), writer, level + 1);
        }

        while (iterator.hasNext()) {
            writer.write(",");
            writer.write("\n");
            indent(Integer.toString(iterator.next()), writer, level + 1);
        }

        writer.write("\n");
        indent("]", writer, level);
    }

    /**
     * Writes the elements as a pretty JSON object.
     *
     * @param elements the elements to write
     * @param writer   the writer to use
     * @param level    the initial indent level
     * @throws IOException if an IO error occurs
     */
    public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
        Iterator<String> iterator = elements.keySet().iterator();
        writer.write("{\n");

        while (iterator.hasNext()) {
            writer.write("\t");
            String key = iterator.next();
            quote(key, writer, level);
            writer.write(": ");
            writer.write(Integer.toString(elements.get(key)));
            if (iterator.hasNext()) {
                writer.write(",");
            }
            writer.write("\n");
        }
        writer.write("}\n");
    }

    /**
     * Addresses the queries in a pretty JSON object.
     *
     * @param query  queries to look for
     * @param writer the writer to use
     * @param level  initial indent level
     * @throws IOException if an IO error occurs
     */
    public static void asObject(TreeMap<String, ArrayList<InvertedIndex.SearchResult>> query, Writer writer, int level)
            throws IOException {

        Iterator<String> iterator = query.keySet().iterator();
        writer.write("{");
        writer.write(System.lineSeparator());
        while (iterator.hasNext()) {
            writer.write(indent(level));
            String input = iterator.next();

            String key = "\"" + input + "\"";
            writer.write(key);
            writer.write(": ");
            writer.write("[");

            if (query.get(input) != null) {
                writeSearchResponse(query.get(input), (BufferedWriter) writer, level + 1);
            }
            writer.write(System.lineSeparator());
            writer.write(indent(level));
            writer.write("]");
            if (iterator.hasNext()) {
                writer.write(",");
            }
            writer.write(System.lineSeparator());
        }
        writer.write("}");
    }

    /**
     * This function is in charge of the number of needed indents
     *
     * @param times number of indents
     * @return string value of tabs
     */
    public static String indent(int times) {
        char[] tabs = new char[times];
        Arrays.fill(tabs, '\t');
        return String.valueOf(tabs);
    }

    /**
     * @param sr     ArrayList SearchResults
     * @param writer BufferedWriter writer
     * @param indent number of indents
     * @throws IOException throws IO exception
     */
    private static void writeSearchResponse(ArrayList<InvertedIndex.SearchResult> sr, BufferedWriter writer, int indent)
            throws IOException {

        Iterator<InvertedIndex.SearchResult> iterator = sr.iterator();
        writer.write(indent(indent));
        while (iterator.hasNext()) {
            writer.write(System.lineSeparator());
            writer.write(indent(indent));
            writer.write("{");
            writer.write(System.lineSeparator());
            writeResultValues(iterator.next(), writer, indent);
            writer.write(indent(indent));
            writer.write("}");
            if (iterator.hasNext()) {
                writer.write(",");
            }
        }
    }

    /**
     * @param found  SearchResult found
     * @param writer BufferedWriter writer
     * @param level  int level
     * @throws IOException throws IO exception
     */
    private static void writeResultValues(InvertedIndex.SearchResult found, BufferedWriter writer, int level)
            throws IOException {

        writer.write(indent(level + 1));
        writer.write("\"where\": \"" + found.getWhere() + "\",");
        writer.write(System.lineSeparator());
        writer.write(indent(level + 1));
        writer.write("\"count\": " + found.getNumMatches() + ",");
        writer.write(System.lineSeparator());
        writer.write(indent(level + 1));
        String strDouble = String.format("%.8f", found.getScore());
        writer.write("\"score\": " + strDouble);
        writer.write(System.lineSeparator());
    }

    /**
     * create a method to write a map inside of a map. (for project 1)
     *
     * @param index  of map
     * @param writer writer class
     * @param level  parm
     * @throws IOException throw error
     */
    public static void invertedIndexJson(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, Writer writer,
                                         int level) throws IOException {
        Iterator<String> iterator = index.keySet().iterator();
        writer.write("{");

        if (iterator.hasNext()) {
            writer.write("\n");
            String key = iterator.next();
            quote(key, writer, level + 1);
            writer.write(": ");
            asNestedArray(index.get(key), writer, level + 1);
        }

        while (iterator.hasNext()) {
            writer.write(",");
            writer.write("\n");
            String key = iterator.next();
            quote(key, writer, level + 1);
            writer.write(": ");
            asNestedArray(index.get(key), writer, level + 1);
        }
        writer.write("\n");
        writer.write("}\n");
    }

    /**
     * Writes the elements as a pretty JSON object with a nested array. The generic
     * notation used allows this method to be used for any type of map with any type
     * of nested collection of integer objects.
     *
     * @param elements the elements to write
     * @param writer   the writer to use
     * @param level    the initial indent level
     * @throws IOException if an IO error occurs
     */
    public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
            throws IOException {

        Iterator<String> iterator = elements.keySet().iterator();
        writer.write("{\n");

        if (iterator.hasNext()) {
            String key = iterator.next();
            quote(key, writer, level + 1);
            writer.write(": ");
            Iterator<Integer> values = elements.get(key).iterator();
            asArray(values, writer, level + 1);
        }

        while (iterator.hasNext()) {
            writer.write(",");
            writer.write("\n");
            String key = iterator.next();
            quote(key, writer, level + 1);
            writer.write(": ");
            Iterator<Integer> values = elements.get(key).iterator();
            asArray(values, writer, level + 1);
        }

        writer.write("\n");
        indent("}", writer, level);

    }

    /**
     * Writes the elements as a nested pretty JSON object to file.
     *
     * @param elements the elements to write
     * @param path     the file path to use
     * @throws IOException if an IO error occurs
     *
     * @see #asNestedArray(Map, Writer, int)
     */
    public static void asNestedArray(Map<String, ? extends Collection<Integer>> elements, Path path)
            throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            asNestedArray(elements, writer, 0);
        }
    }

    /**
     * Returns the elements as a nested pretty JSON object.
     *
     * @param elements the elements to use
     * @return a {@link String} containing the elements in pretty JSON format
     *
     * @see #asNestedArray(Map, Writer, int)
     */
    public static String asNestedArray(Map<String, ? extends Collection<Integer>> elements) {
        try {
            StringWriter writer = new StringWriter();
            asNestedArray(elements, writer, 0);
            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes the elements as a pretty JSON object to file.
     *
     * @param elements the elements to write
     * @param path     the file path to use
     * @throws IOException if an IO error occurs
     *
     * @see #asObject(Map, Writer, int)
     */
    public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            asObject(elements, writer, 0);
        }
    }

    /**
     * Returns the elements as a pretty JSON object.
     *
     * @param elements the elements to use
     * @return a {@link String} containing the elements in pretty JSON format
     *
     * @see #asObject(Map, Writer, int)
     */
    public static String asObject(Map<String, Integer> elements) {
        try {
            StringWriter writer = new StringWriter();
            asObject(elements, writer, 0);
            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes the elements as a pretty JSON array to file.
     *
     * @param elements the elements to write
     * @param path     the file path to use
     * @throws IOException if an IO error occurs
     *
     * @see #asArray(Iterator, Writer, int)
     */
    public static void asArray(Iterator<Integer> elements, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            asArray(elements, writer, 0);
        }
    }

    /**
     * Returns the elements as a pretty JSON array.
     *
     * @param elements the elements to use
     * @return a {@link String} containing the elements in pretty JSON format
     *
     * @see #asArray(Iterator, Writer, int)
     */
    public static String asArray(Iterator<Integer> elements) {
        try {
            StringWriter writer = new StringWriter();
            asArray(elements, writer, 0);
            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Indents and then writes the String element.
     *
     * @param element the element to write
     * @param writer  the writer to use
     * @param level   the number of times to indent
     * @throws IOException if an IO error occurs
     */
    public static void indent(String element, Writer writer, int level) throws IOException {
        writer.write("\t".repeat(level));

        if (!element.isBlank()) {
            writer.write(element);
        }
    }

    /**
     * Indents and then writes the text element surrounded by {@code " "} quotation
     * marks.
     *
     * @param element the element to write
     * @param writer  the writer to use
     * @param level   the number of times to indent
     * @throws IOException if an IO error occurs
     */
    public static void quote(String element, Writer writer, int level) throws IOException {
        writer.write("\t".repeat(level));
        writer.write('"');
        writer.write(element);
        writer.write('"');
    }
}
