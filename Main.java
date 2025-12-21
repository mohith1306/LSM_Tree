public class Main {
    public static void main(String[] args) {
        StorageEngine db = new StorageEngine("data", 5, 3);
        System.out.println("=== STARTING DATABASE ===");
        // Write operations
        db.set("name", "mohith");
        db.set("age", "22");
        db.set("city", "Bangalore");
        db.set("lang", "Java");
        db.set("role", "Student"); // should trigger a flush
        System.out.println("\n=== READ AFTER FLUSH ===");
        System.out.println("name  : " + db.get("name"));
        System.out.println("age   : " + db.get("age"));
        System.out.println("city  : " + db.get("city"));
        db.set("name", "mohith");

        System.out.println("\n=== AFTER UPDATE ===");
        System.out.println("name  : " + db.get("name"));

        // Delete
        db.delete("age");

        System.out.println("\n=== AFTER DELETE ===");
        System.out.println("age   : " + db.get("age")); // should be null

        // More writes to create more SSTables
        db.set("k1", "v1");
        db.set("k2", "v2");
        db.set("k3", "v3");
        db.set("k4", "v4");
        db.set("k5", "v5"); // another flush

        System.out.println("\n=== FINAL READS ===");
        System.out.println("k3    : " + db.get("k3"));
        System.out.println("name  : " + db.get("name"));

        System.out.println("\n=== DATABASE END ===");
    }
}
