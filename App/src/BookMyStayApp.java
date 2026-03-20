import java.util.*;

/**
 * Use Case 10: Booking Cancellation & Inventory Rollback
 * Goal: Enable safe cancellation of confirmed bookings by correctly reversing state changes.
 * @version 10.0
 */

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

class Reservation {
    private String reservationID;
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.reservationID = "RES-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationID() { return reservationID; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

public class BookMyStayApp {
    private static Map<String, Integer> inventory = new HashMap<>();
    private static Queue<Reservation> bookingQueue = new LinkedList<>();
    private static Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private static List<Reservation> bookingHistory = new ArrayList<>();

    // UC10: Stack to track allocated Room IDs for LIFO Rollback
    private static Stack<String> rollbackStack = new Stack<>();

    public static void main(String[] args) {
        // Step 1: Initialize Inventory
        inventory.put("Single Room", 10);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 0);

        allocatedRooms.put("Single Room", new HashSet<>());
        allocatedRooms.put("Double Room", new HashSet<>());
        allocatedRooms.put("Suite Room", new HashSet<>());

        System.out.println("--- Welcome to Book My Stay App v10.0 ---");

        // Step 2: Queueing Requests
        bookingQueue.add(new Reservation("Abhi", "Single Room"));
        bookingQueue.add(new Reservation("Subha", "Single Room"));
        bookingQueue.add(new Reservation("Vanmathi", "Suite Room")); // Out of Stock

        System.out.println("Processing " + bookingQueue.size() + " queued requests...\n");

        // Step 3: Process Bookings
        while (!bookingQueue.isEmpty()) {
            Reservation request = bookingQueue.poll();
            try {
                validateBooking(request);

                String type = request.getRoomType();
                String roomID = generateRoomID(type);

                // Update State
                allocatedRooms.get(type).add(roomID);
                inventory.put(type, inventory.get(type) - 1);
                bookingHistory.add(request);

                // UC10: Record room ID in rollback structure
                rollbackStack.push(roomID);

                System.out.println("SUCCESS: Booking Confirmed for " + request.getGuestName() + " (Room: " + roomID + ")");

            } catch (InvalidBookingException e) {
                System.out.println("REJECTED: " + request.getGuestName() + " - " + e.getMessage());
            }
        }

        // UC10: Simulate Guest Cancellation
        System.out.println("\n--- Initiating Cancellation Request ---");
        // Let's assume Subha cancels her booking
        cancelBooking("Subha", "Single Room");

        generateBookingHistoryReport();
    }

    /**
     * UC10: Cancellation & Rollback Service
     */
    private static void cancelBooking(String guestName, String roomType) {
        System.out.println("Attempting to cancel booking for: " + guestName);

        // 1. Validate if booking exists in history
        Reservation bookingToCancel = null;
        for (Reservation res : bookingHistory) {
            if (res.getGuestName().equalsIgnoreCase(guestName)) {
                bookingToCancel = res;
                break;
            }
        }

        if (bookingToCancel != null && !rollbackStack.isEmpty()) {
            // 2. LIFO Rollback: Release the most recently allocated room ID
            String releasedRoomID = rollbackStack.pop();

            // 3. Inventory Restoration: Increment count
            inventory.put(roomType, inventory.get(roomType) + 1);

            // 4. Update state: Remove from history and active allocations
            allocatedRooms.get(roomType).remove(releasedRoomID);
            bookingHistory.remove(bookingToCancel);

            System.out.println("ROLLBACK SUCCESS: Room " + releasedRoomID + " is now available. Inventory updated.");
        } else {
            System.out.println("ERROR: Cancellation failed. No active booking found for " + guestName);
        }
    }

    private static void validateBooking(Reservation res) throws InvalidBookingException {
        String type = res.getRoomType();
        if (!inventory.containsKey(type)) {
            throw new InvalidBookingException("Invalid Room Type: " + type);
        }
        if (inventory.get(type) <= 0) {
            throw new InvalidBookingException("Room Type '" + type + "' is currently out of stock.");
        }
    }

    private static void generateBookingHistoryReport() {
        System.out.println("\n--- Final Booking Audit Report ---");
        System.out.println("Current Inventory (Single): " + inventory.get("Single Room"));
        if (bookingHistory.isEmpty()) {
            System.out.println("No confirmed bookings.");
        } else {
            for (Reservation res : bookingHistory) {
                System.out.println("Confirmed -> Guest: " + res.getGuestName() + " | Room: " + res.getRoomType());
            }
        }
        System.out.println("----------------------------------");
    }

    private static String generateRoomID(String type) {
        String prefix = type.substring(0, 2).toUpperCase();
        int roomNumber = 101 + allocatedRooms.get(type).size();
        return prefix + "-" + roomNumber;
    }
}