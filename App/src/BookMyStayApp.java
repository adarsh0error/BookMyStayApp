import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Use Case 5: Booking Request (First-Come-First-Served)
 * Goal: Handle multiple booking requests fairly using a Queue (FIFO).
 * @version 5.0
 */

// UC5: Represents a guest's intent to book a room
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

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
    private static Map<String, Integer> inventory = new HashMap<>();

    // UC5: Booking Request Queue (FIFO)
    // We use LinkedList because it implements the Queue interface
    private static Queue<Reservation> bookingQueue = new LinkedList<>();

    public static void main(String[] args) {
        // Step 1: Initialize Inventory
        inventory.put("Single Room", 10);
        inventory.put("Double Room", 0);
        inventory.put("Suite Room", 3);

        System.out.println("--- Welcome to Book My Stay App v5.0 ---");

        // Step 2: Simulate Guests Submitting Requests (The "Arrival" order)
        System.out.println("Receiving booking requests...");

        bookingQueue.add(new Reservation("Alice", "Single Room"));
        bookingQueue.add(new Reservation("Bob", "Suite Room"));
        bookingQueue.add(new Reservation("Charlie", "Single Room"));

        System.out.println("Requests added to the queue in arrival order.\n");

        // Step 3: Display the Queue (FIFO Principle)
        System.out.println("========================================");
        System.out.println("        CURRENT BOOKING QUEUE           ");
        System.out.println("========================================");

        if (bookingQueue.isEmpty()) {
            System.out.println("No pending requests.");
        } else {
            int position = 1;
            for (Reservation res : bookingQueue) {
                System.out.println(position + ". Guest: " + res.getGuestName() +
                        " | Requested: " + res.getRoomType());
                position++;
            }
        }

        System.out.println("========================================\n");

        // UC5 Requirement: No inventory mutation occurs at this stage.
        System.out.println("Status: Requests are queued and waiting for processing.");
        System.out.println("Note: Inventory has not been updated yet.");
    }
}