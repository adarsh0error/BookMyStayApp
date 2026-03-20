import java.util.*;

/**
 * Use Case 8: Booking History & Reporting
 * Goal: Maintain a historical record of confirmed bookings and generate reports.
 * @version 8.0
 */

class Service {
    private String name;
    private double price;

    public Service(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " ($" + price + ")";
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
    private static Map<String, List<Service>> addOnServices = new HashMap<>();

    // UC8: List to maintain a historical record of confirmed bookings
    private static List<Reservation> bookingHistory = new ArrayList<>();

    public static void main(String[] args) {
        // Step 1: Initialize Inventory
        inventory.put("Single Room", 10);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 3);

        allocatedRooms.put("Single Room", new HashSet<>());
        allocatedRooms.put("Double Room", new HashSet<>());
        allocatedRooms.put("Suite Room", new HashSet<>());

        System.out.println("--- Welcome to Book My Stay App v8.0 ---");

        // Step 2: Queueing Requests (Using names from UC8 documentation)
        bookingQueue.add(new Reservation("Abhi", "Single Room"));
        bookingQueue.add(new Reservation("Subha", "Double Room"));
        bookingQueue.add(new Reservation("Vanmathi", "Suite Room"));

        System.out.println("Processing " + bookingQueue.size() + " queued requests...\n");

        // Step 3: Process Queue and Update History
        while (!bookingQueue.isEmpty()) {
            Reservation request = bookingQueue.poll();
            String type = request.getRoomType();
            int currentStock = inventory.getOrDefault(type, 0);

            if (currentStock > 0) {
                String roomID = generateRoomID(type);
                allocatedRooms.get(type).add(roomID);
                inventory.put(type, currentStock - 1);

                // UC8: Add confirmed reservation to history
                bookingHistory.add(request);

                System.out.println("Booking Confirmed for: " + request.getGuestName());
            } else {
                System.out.println("Booking Rejected for: " + request.getGuestName());
            }
        }

        // Step 4: UC8 - Generate Reporting
        generateBookingHistoryReport();
    }

    /**
     * UC8: Reporting Service - Displays the audit trail of confirmed bookings.
     */
    private static void generateBookingHistoryReport() {
        System.out.println("\n--- Booking History and Reporting ---");
        System.out.println("\nBooking History Report");

        if (bookingHistory.isEmpty()) {
            System.out.println("No confirmed bookings to show.");
            return;
        }

        for (Reservation res : bookingHistory) {
            System.out.println("Guest: " + res.getGuestName() + ", Room Type: " + res.getRoomType());
        }
        System.out.println("--------------------------------------");
    }

    private static String generateRoomID(String type) {
        String prefix = type.substring(0, 2).toUpperCase();
        int roomNumber = 101 + allocatedRooms.get(type).size();
        return prefix + "-" + roomNumber;
    }
}