import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for creating the data structure for generating a
 * JSON file type of structure through multi-threading
 *
 * @author sme777
 * @author Samson Petrosyan
 */
public class ConcurrentInvertedIndex extends InvertedIndex {

    /**
     * SimpleReadWriteLock lock object
     */
    private final SimpleReadWriteLock lock;

    /**
     * constructor is the same is its parent class
     */
    public ConcurrentInvertedIndex() {
        super();
        lock = new SimpleReadWriteLock();
    }

    @Override
    public void add(String word, String location, int position) {
        lock.writeLock().lock();
        try {
            super.add(word, location, position);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addAll(List<String> words, String location, int position) {
        lock.writeLock().lock();
        try {
            super.addAll(words, location, position);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addAll(InvertedIndex secondIndex) {
        lock.writeLock().lock();
        try {
            super.addAll(secondIndex);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void toJson(Path path, BufferedWriter writer) throws IOException {
        lock.readLock().lock();
        try {
            super.toJson(path, writer);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void countToJson(Path path, BufferedWriter writer) throws IOException {
        lock.readLock().lock();
        try {
            super.countToJson(path, writer);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ArrayList<SearchResult> exactSearch(Set<String> queries) {
        lock.readLock().lock();
        try {
            return super.exactSearch(queries);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ArrayList<SearchResult> partialSearch(Set<String> queries) {
        lock.readLock().lock();
        try {
            return super.partialSearch(queries);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return super.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<String> getWordSet() {
        lock.readLock().lock();
        try {
            return super.getWordSet();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<String> getLocationSet(String word) {
        lock.readLock().lock();
        try {
            return super.getLocationSet(word);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<Integer> getPositionSet(String word, String location) {
        lock.readLock().lock();
        try {
            return super.getPositionSet(word, location);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return super.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size(String word) {
        lock.readLock().lock();
        try {
            return super.size(word);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size(String word, String location) {
        lock.readLock().lock();
        try {
            return super.size(word, location);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(String word) {
        lock.readLock().lock();
        try {
            return super.contains(word);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(String word, String location) {
        lock.readLock().lock();
        try {
            return super.contains(word, location);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean contains(String word, String location, int position) {
        lock.readLock().lock();
        try {
            return super.contains(word, location, position);
        } finally {
            lock.readLock().unlock();
        }
    }

}
