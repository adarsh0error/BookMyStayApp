import java.util.HashMap;
import java.util.Map;

/**
 * Use Case 4: Room Search & Availability Check
 * Goal: Filter results to show only available rooms (Validation Logic)
 * and ensure the search process doesn't modify the system state.
 * @version 4.0
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

    public String getType() { return type; }

    public void displayDetails() {
        System.out.printf("%-15s | Beds: %d | Price: $%.2f", type, beds, price);
    }
}

class SingleRoom extends Room { public SingleRoom() { super("Single Room", 1, 100.0); } }
class DoubleRoom extends Room { public DoubleRoom() { super("Double Room", 2, 180.0); } }
class SuiteRoom extends Room { public SuiteRoom() { super("Suite Room", 4, 350.0); } }

public class BookMyStayApp {
    // UC4: Inventory as the "State Holder"
    private static Map<String, Integer> inventory = new HashMap<>();

    public static void main(String[] args) {
        // Step 1: Initialize Inventory (Simulating current hotel state)
        inventory.put("Single Room", 10);
        inventory.put("Double Room", 0); // UC4: This should be hidden from the guest
        inventory.put("Suite Room", 3);

        System.out.println("--- Welcome to Book My Stay App v4.0 ---");
        System.out.println("Guest is searching for available rooms...\n");

        // Step 2: Read-Only Access
        // We use the inventory map to check availability without modifying it.
        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };

        System.out.println("========================================");
        System.out.println("        AVAILABLE ROOM SEARCH          ");
        System.out.println("========================================");

        for (Room r : rooms) {
            int availableCount = inventory.getOrDefault(r.getType(), 0);

            // Step 3: Validation Logic (The core change for UC4)
            // Only display the room if it is actually available (count > 0)
            if (availableCount > 0) {
                r.displayDetails();
                System.out.println(" | Available: " + availableCount);
            }
        }
        System.out.println("========================================\n");

        System.out.println("Search completed successfully.");
        System.out.println("Note: Unavailable rooms were filtered out automatically.");
    }
}