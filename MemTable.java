import java.util.TreeMap;

public class MemTable {

    private static final int MAX_SIZE = 5;
    private static final String TOMBSTONE = "__DELETED__";

    private final TreeMap<String, String> map = new TreeMap<>();

    public MemTable() {}

    public void put(String key, String value) {
        map.put(key, value);
    }

    public String get(String key) {
        String val = map.get(key);
        if (TOMBSTONE.equals(val)) return null;
        return val;
    }

    public void delete(String key) {
        map.put(key, TOMBSTONE);
    }

    public boolean isFull() {
        return map.size() >= MAX_SIZE;
    }

    public TreeMap<String, String> getAll() {
        return map;
    }

    public void clear() {
        map.clear();
    }
}
