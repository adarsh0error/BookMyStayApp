import java.util.*;

/**
 * Use Case 9: Error Handling & Validation
 * Goal: Strengthen system reliability by introducing structured validation and custom exceptions.
 * @version 9.0
 */

// UC9: Custom Exception for Invalid Booking Scenarios
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

    public static void main(String[] args) {
        // Step 1: Initialize Inventory
        inventory.put("Single Room", 10);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 0); // Setting to 0 to test Out of Stock validation

        allocatedRooms.put("Single Room", new HashSet<>());
        allocatedRooms.put("Double Room", new HashSet<>());
        allocatedRooms.put("Suite Room", new HashSet<>());

        System.out.println("--- Welcome to Book My Stay App v9.0 ---");

        // Step 2: Queueing Requests
        bookingQueue.add(new Reservation("Abhi", "Single Room"));
        bookingQueue.add(new Reservation("Subha", "Penthouse")); // UC9: Invalid Room Type
        bookingQueue.add(new Reservation("Vanmathi", "Suite Room")); // UC9: Out of Stock

        System.out.println("Processing " + bookingQueue.size() + " queued requests...\n");

        // Step 3: Process Queue with UC9 Validation & Error Handling
        while (!bookingQueue.isEmpty()) {
            Reservation request = bookingQueue.poll();

            try {
                // UC9: Fail-Fast Validation
                validateBooking(request);

                // If validation passes, proceed with state changes
                String type = request.getRoomType();
                String roomID = generateRoomID(type);

                allocatedRooms.get(type).add(roomID);
                inventory.put(type, inventory.get(type) - 1);
                bookingHistory.add(request);

                System.out.println("SUCCESS: Booking Confirmed for " + request.getGuestName());

            } catch (InvalidBookingException e) {
                // UC9: Graceful Failure Handling
                System.out.println("REJECTED: " + request.getGuestName() + " - " + e.getMessage());
            }
        }

        generateBookingHistoryReport();
    }

    /**
     * UC9: Validator Method - Checks constraints before system state is modified.
     */
    private static void validateBooking(Reservation res) throws InvalidBookingException {
        String type = res.getRoomType();

        // 1. Validate Room Type Existence
        if (!inventory.containsKey(type)) {
            throw new InvalidBookingException("Invalid Room Type: " + type);
        }

        // 2. Prevent Negative Inventory (Guard System State)
        if (inventory.get(type) <= 0) {
            throw new InvalidBookingException("Room Type '" + type + "' is currently out of stock.");
        }
    }

    private static void generateBookingHistoryReport() {
        System.out.println("\n--- Final Booking Audit Report ---");
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