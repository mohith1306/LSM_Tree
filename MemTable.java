import java.util.HashMap;
import java.util.Map;

public class MemTable {

    private final int MAX_SIZE;
    private final HashMap<String, String> map;
    public static final String TOMBSTONE = "__DELETED__";

    public MemTable(int maxSize) {
        this.MAX_SIZE = maxSize;
        this.map = new HashMap<>();
    }

    // Insert or update a key
    public void put(String key, String value) {
        map.put(key, value);
    }

    // Read a key
    public String get(String key) {
        return map.get(key);
    }

    // Mark a key as deleted (do NOT remove immediately)
    public void delete(String key) {
        map.put(key, TOMBSTONE);
    }

    // Check if memtable reached capacity
    public boolean isFull() {
        return map.size() >= MAX_SIZE;
    }

    // Used during flush
    public Map<String, String> getAllEntries() {
        return map;
    }

    // Clear after flushing to SSTable
    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }
}
