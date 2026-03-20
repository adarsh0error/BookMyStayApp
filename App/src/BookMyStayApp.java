import java.util.*;

/**
 * Use Case 6: Reservation Confirmation & Room Allocation
 * Goal: Confirm bookings, assign unique Room IDs, and update inventory.
 * @version 6.0
 */

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

public class BookMyStayApp {
    private static Map<String, Integer> inventory = new HashMap<>();
    private static Queue<Reservation> bookingQueue = new LinkedList<>();

    // UC6: Track allocated Room IDs to prevent double-booking
    // Map: Room Type -> Set of Assigned Room IDs
    private static Map<String, Set<String>> allocatedRooms = new HashMap<>();

    public static void main(String[] args) {
        // Step 1: Initialize Inventory
        inventory.put("Single Room", 10);
        inventory.put("Double Room", 0);
        inventory.put("Suite Room", 3);

        // UC6: Initialize the allocation map for each room type
        allocatedRooms.put("Single Room", new HashSet<>());
        allocatedRooms.put("Double Room", new HashSet<>());
        allocatedRooms.put("Suite Room", new HashSet<>());

        System.out.println("--- Welcome to Book My Stay App v6.0 ---");

        // Step 2: Queueing Requests (UC5 logic)
        bookingQueue.add(new Reservation("Alice", "Single Room"));
        bookingQueue.add(new Reservation("Bob", "Suite Room"));
        bookingQueue.add(new Reservation("Charlie", "Single Room"));
        bookingQueue.add(new Reservation("Diana", "Double Room")); // Out of stock example

        System.out.println("Processing " + bookingQueue.size() + " queued requests...\n");

        // Step 3: UC6 - Process Queue and Allocate Rooms (FIFO)
        System.out.println("========================================");
        System.out.println("       ROOM ALLOCATION REPORT           ");
        System.out.println("========================================");

        while (!bookingQueue.isEmpty()) {
            Reservation request = bookingQueue.poll(); // Dequeue
            String type = request.getRoomType();
            int currentStock = inventory.getOrDefault(type, 0);

            if (currentStock > 0) {
                // Generate a Unique Room ID (e.g., SR-101, SU-103)
                String roomID = generateRoomID(type);

                // Add to Set to ensure uniqueness and prevent double-booking
                allocatedRooms.get(type).add(roomID);

                // Update Inventory Immediately
                inventory.put(type, currentStock - 1);

                System.out.printf("CONFIRMED: %-8s | Room: %-6s | Guest: %s%n",
                        "SUCCESS", roomID, request.getGuestName());
            } else {
                System.out.printf("REJECTED:  %-8s | Room: %-6s | Guest: %s%n",
                        "NO STOCK", "N/A", request.getGuestName());
            }
        }
        System.out.println("========================================\n");

        // Step 4: Final Inventory Status
        System.out.println("Updated Inventory: " + inventory);
    }

    /**
     * Helper to generate a unique Room ID based on type and current allocation size.
     */
    private static String generateRoomID(String type) {
        String prefix = type.substring(0, 2).toUpperCase();
        int roomNumber = 101 + allocatedRooms.get(type).size();
        return prefix + "-" + roomNumber;
    }
}