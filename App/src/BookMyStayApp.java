import java.util.HashMap;
import java.util.Map;

/**
 * Use Case 3: Centralized Room Inventory Management
 * This version refactors static variables into a HashMap collection.
 * * @author Developer
 * @version 3.0
 */

abstract class Room {
    private String type;
    private int beds;
    private double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void displayDetails() {
        System.out.printf("%-15s | Beds: %d | Price: $%.2f%n", type, beds, price);
    }
}

class SingleRoom extends Room { public SingleRoom() { super("Single Room", 1, 100.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super("Double Room", 2, 180.0); } }
class SuiteRoom extends Room { public SuiteRoom() { super("Suite Room", 4, 350.0); } }

public class BookMyStayApp {
    // UC3: Centralized Inventory using HashMap
    private static Map<String, Integer> inventory = new HashMap<>();

    public static void main(String[] args) {
        // Step 1: Initialize Centralized Inventory
        inventory.put("Single Room", 10);
        inventory.put("Double Room", 7);
        inventory.put("Suite Room", 3);

        System.out.println("--- Welcome to Book My Stay App v3.0 ---");

        // Step 2: Display Centralized Inventory Status (UC3 Specific Format)
        System.out.println("\n========================================");
        System.out.println("      CENTRALIZED INVENTORY STATUS      ");
        System.out.println("========================================");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Count: " + entry.getValue());
        }
        System.out.println("========================================\n");

        // Step 3: Display Detailed Room Information
        System.out.println("Detailed Room Specifications:");
        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };

        for (Room r : rooms) {
            r.displayDetails();
            // Fetch availability from the centralized map
            int available = inventory.getOrDefault(r.getType(), 0);
            System.out.println("Current Availability: " + available + "\n");
        }

        System.out.println("Inventory synchronized successfully.");
    }
}