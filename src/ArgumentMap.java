import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses and stores command-line arguments into simple flag/value pairs.
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class ArgumentMap {
    /**
     * Stores command-line arguments in flag/value pairs.
     */
    private final Map<String, String> map;

    /**
     * Initializes this argument map.
     *
     * When you have variable of type Map, you have to complete with calling the map
     * and setting it equal to a HashMap You do this because the Map is an Abstract
     * class that can't be made into an object.
     */
    public ArgumentMap() {
        this.map = new HashMap<String, String>();
    }

    /**
     * Initializes this argument map and then parsers the arguments into flag/value
     * pairs where possible. Some flags may not have associated values. If a flag is
     * repeated, its value is overwritten.
     *
     * @param args the command line arguments to parse
     */
    public ArgumentMap(String[] args) {
        this.map = new HashMap<String, String>();
        parse(args);
    }

    /**
     * Parses the arguments into flag/value pairs where possible. Some flags may not
     * have associated values. If a flag is repeated, its value will be overwritten.
     *
     * @param args the command line arguments to parse
     */
    public void parse(String[] args) {
        String prevFlag = null;
        for (int i = 0; i < args.length; i++) {
            if (isFlag(args[i])) {
                this.map.put(args[i], null);
                prevFlag = args[i];
            } else {
                if (this.map.get(prevFlag) == null) {
                    this.map.put(prevFlag, args[i]);
                }
            }

        }

    }

    /**
     * Determines whether the argument is a flag. The argument is considered a flag
     * if it is a dash "-" character followed by any letter character.
     *
     * @param arg the argument to test if its a flag
     * @return {@code true} if the argument is a flag
     */
    public static boolean isFlag(String arg) {
        return arg != null && arg.length() >= 2 && arg.charAt(0) == '-' && Character.isLetter(arg.charAt(1));
    }

    /**
     * Determines whether the argument is a value. Anything that is not a flag is
     * considered a value.
     *
     * *!* is used to flip the result of the method call so if the called method is
     * false the parent method is true and in reverse
     *
     * @param arg the argument to test if its a value
     * @return {@code true} if the argument is a value
     */
    public static boolean isValue(String arg) {
        return !isFlag(arg);

    }

    /**
     * Returns the number of unique flags.
     *
     * @return number of unique flags
     */
    public int numFlags() {
        return this.map.size();

    }

    /**
     * Determines whether the specified flag exists.
     *
     * @param flag the flag check
     * @return {@code true} if the flag exists
     */
    public boolean hasFlag(String flag) {
        return this.map.containsKey(flag);

    }

    /**
     * Determines whether the specified flag is mapped to a non-null value.
     *
     * @param flag the flag to find
     * @return {@code true} if the flag is mapped to a non-null value
     */
    public boolean hasValue(String flag) {
        return this.map.get(flag) != null;
    }

    /**
     * Returns the value to which the specified flag is mapped as a {@link String}
     * or null if there is no mapping.
     *
     * @param flag the flag whose associated value is to be returned
     * @return the value to which the specified flag is mapped or {@code null} if
     *         there is no mapping
     */
    public String getString(String flag) {
        return this.map.get(flag);
    }

    /**
     * Returns the value to which the specified flag is mapped as a {@link String}
     * or the default value if there is no mapping.
     *
     * @param flag         the flag whose associated value is to be returned
     * @param defaultValue the default value to return if there is no mapping
     * @return the value to which the specified flag is mapped, or the default value
     *         if there is no mapping
     */
    public String getString(String flag, String defaultValue) {
        if (this.map.get(flag) == null) {
            return defaultValue;
        }
        return this.map.get(flag);
    }

    /**
     * Returns the value to which the specified flag is mapped as a {@link Path}, or
     * {@code null} if unable to retrieve this mapping (including being unable to
     * convert the value to a {@link Path} or no value exists).
     *
     * This method should not throw any exceptions!
     *
     * @param flag the flag whose associated value is to be returned
     * @return the value to which the specified flag is mapped, or {@code null} if
     *         unable to retrieve this mapping
     */
    public Path getPath(String flag) {
        if (this.map.get(flag) != null) {
            return Path.of(this.map.get(flag));
        } else {
            return null;
        }

    }

    /**
     * Returns the value the specified flag is mapped as a {@link Path}, or the
     * default value if unable to retrieve this mapping (including being unable to
     * convert the value to a {@link Path} or if no value exists).
     *
     * This method should not throw any exceptions!
     *
     * @param flag         the flag whose associated value will be returned
     * @param defaultValue the default value to return if there is no valid mapping
     * @return the value the specified flag is mapped as a {@link Path}, or the
     *         default value if there is no valid mapping
     */
    public Path getPath(String flag, Path defaultValue) {
        if (this.map.get(flag) != null) {
            return Path.of(this.map.get(flag));
        } else {
            return defaultValue;
        }
    }

    /**
     * Returns the value the specified flag is mapped as an int value, or the
     * default value if unable to retrieve this mapping (including being unable to
     * convert the value to an int or if no value exists).
     *
     * @param flag         the flag whose associated value will be returned
     * @param defaultValue the default value to return if there is no valid mapping
     * @return the value the specified flag is mapped as a int, or the default value
     *         if there is no valid mapping
     */
    public int getInteger(String flag, int defaultValue) {
        try {
            return Integer.parseInt(this.map.get(flag));
        } catch (NumberFormatException e) {
            return defaultValue;
        }

    }
    @Override
    public String toString() {
        return this.map.toString();
    }
}

