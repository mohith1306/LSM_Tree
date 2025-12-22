public class Main {

    public static void main(String[] args) {

        StorageEngine db = new StorageEngine("data", 5, 3);

        System.out.println("=== STARTING DATABASE ===");

        db.set("name", "mohith");
        db.set("age", "22");
        db.set("city", "Bangalore");
        db.set("lang", "Java");
        db.set("role", "Student"); // triggers flush

        System.out.println("\n=== READ AFTER FLUSH ===");
        System.out.println("name  : " + db.getWithTiming("name"));
        System.out.println("city  : " + db.getWithTiming("city"));

        db.set("name", "mohith");

        System.out.println("\n=== AFTER UPDATE ===");
        System.out.println("name  : " + db.getWithTiming("name"));

        db.delete("age");

        System.out.println("\n=== AFTER DELETE ===");
        System.out.println("age   : " + db.getWithTiming("age"));

        db.set("k1", "v1");
        db.set("k2", "v2");
        db.set("k3", "v3");
        db.set("k4", "v4");
        db.set("k5", "v5"); // triggers flush

        System.out.println("\n=== FINAL READS ===");
        System.out.println("k3    : " + db.getWithTiming("k3"));
        System.out.println("name  : " + db.getWithTiming("name"));

        System.out.println("\n=== DATABASE END ===");
    }
}
