import java.io.*;
import java.util.Map;

public class StorageEngine {

    private final MemTable memTable = new MemTable();
    private final File dataFile;

    public StorageEngine(String dir, int memSize, int maxSSTables) {
        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        this.dataFile = new File(dir + "/sstable.data");
    }

    // PUT
    public void set(String key, String value) {
        memTable.put(key, value);

        if (memTable.isFull()) {
            flushToDisk();
        }
    }

    // DELETE
    public void delete(String key) {
        memTable.delete(key);
    }

    // NORMAL GET (NO TIMING)
    public String get(String key) {
        String val = memTable.get(key);
        if (val != null) {
            return val;
        }
        return readFromDisk(key);
    }

    // GET WITH TIMING (MemTable vs Disk)
    public String getWithTiming(String key) {

        // MemTable timing
        long memStart = System.nanoTime();
        String val = memTable.get(key);
        long memEnd = System.nanoTime();
        long memTime = memEnd - memStart;

        if (val != null) {
            System.out.println("Found in MemTable");
            System.out.println("MemTable access time (ns): " + memTime);
            return val;
        }

        // Disk timing
        long diskStart = System.nanoTime();
        val = readFromDisk(key);
        long diskEnd = System.nanoTime();
        long diskTime = diskEnd - diskStart;

        if (val != null) {
            System.out.println("Found in SSTable (disk)");
            System.out.println("MemTable access time (ns): " + memTime);
            System.out.println("Disk access time (ns): " + diskTime);
            return val;
        }

        System.out.println("Key not found");
        System.out.println("MemTable access time (ns): " + memTime);
        System.out.println("Disk access time (ns): " + diskTime);
        return null;
    }

    // Flush MemTable → disk
    private void flushToDisk() {
        System.out.println("Flushing MemTable to disk...");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile, true))) {
            for (Map.Entry<String, String> e : memTable.getAll().entrySet()) {
                bw.write(e.getKey() + "=" + e.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        memTable.clear();
    }

    // Disk read
    private String readFromDisk(String key) {
        if (!dataFile.exists()) return null;

        String latest = null;

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts[0].equals(key)) {
                    latest = parts[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ("__DELETED__".equals(latest)) return null;
        return latest;
    }
}
