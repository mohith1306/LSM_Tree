import java.io.*;
import java.util.*;

public class StorageEngine {

    private final MemTable memTable;
    private final File walFile;
    private final File sstableDir;
    private int sstableCounter = 0;

    public StorageEngine(String dataDir, int maxMemTableSize, int maxSSTables) {

        // Create data directory
        File baseDir = new File(dataDir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        // WAL file
        this.walFile = new File(baseDir, "wal.log");

        // SSTable directory
        this.sstableDir = new File(baseDir, "sstables");
        if (!sstableDir.exists()) {
            sstableDir.mkdirs();
        }

        // Initialize MemTable
        this.memTable = new MemTable(maxMemTableSize);

        // Load existing SSTables count
        File[] existing = sstableDir.listFiles();
        if (existing != null) {
            sstableCounter = existing.length;
        }

        // Recover state from WAL
        recoverFromWAL();
    }

    /* ===================== SET ===================== */
    public void set(String key, String value) {
        appendToWAL("SET " + key + " " + value);
        memTable.put(key, value);

        if (memTable.isFull()) {
            flush();
        }
    }

    /* ===================== GET ===================== */
    public String get(String key) {

        // 1. Check MemTable
        String value = memTable.get(key);
        if (value != null) {
            if (value.equals(MemTable.TOMBSTONE)) return null;
            return value;
        }

        // 2. Check SSTables (newest to oldest)
        File[] files = sstableDir.listFiles();
        if (files == null) return null;

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

        for (File file : files) {
            value = searchSSTable(file, key);
            if (value != null) {
                if (value.equals(MemTable.TOMBSTONE)) return null;
                return value;
            }
        }

        return null;
    }

    /* ===================== DELETE ===================== */
    public void delete(String key) {
        appendToWAL("DELETE " + key);
        memTable.delete(key);

        if (memTable.isFull()) {
            flush();
        }
    }

    /* ===================== FLUSH ===================== */
    private void flush() {
        System.out.println("Flushing MemTable to SSTable...");

        Map<String, String> data = memTable.getAllEntries();
        if (data.isEmpty()) return;

        TreeMap<String, String> sorted = new TreeMap<>(data);

        File sstableFile = new File(
                sstableDir,
                "sstable_" + (++sstableCounter) + ".dat"
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sstableFile))) {
            for (Map.Entry<String, String> entry : sorted.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return; // DO NOT clear memtable if write fails
        }

        memTable.clear();
    }

    /* ===================== WAL ===================== */
    private void appendToWAL(String entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(walFile, true))) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recoverFromWAL() {
        if (!walFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(walFile))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 3);
                if (parts.length < 2) continue;

                String cmd = parts[0];
                String key = parts[1];

                if (cmd.equals("SET") && parts.length == 3) {
                    memTable.put(key, parts[2]);
                } else if (cmd.equals("DELETE")) {
                    memTable.delete(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ===================== SSTABLE SEARCH ===================== */
    private String searchSSTable(File file, String key) {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                int idx = line.indexOf('=');
                if (idx == -1) continue;

                String k = line.substring(0, idx);
                if (k.equals(key)) {
                    return line.substring(idx + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
