import java.io.*;
import java.util.*;

/**
 * Use Case 12: Data Persistence & System Recovery
 * Goal: Ensure system state (Inventory & Bookings) survives application restarts.
 * @version 12.0
 */

// Step 1: Make Reservation Serializable for file storage
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
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

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

public class BookMyStayApp {
    private static final String DATA_FILE = "system_state.dat";

    // Shared Resources
    private static Map<String, Integer> inventory = new HashMap<>();
    private static List<Reservation> bookingHistory = new ArrayList<>();
    private static Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private static Queue<Reservation> bookingQueue = new LinkedList<>();

    private static final Object lock = new Object();

    public static void main(String[] args) {
        System.out.println("--- Welcome to Book My Stay App v12.0 (Persistence Enabled) ---");

        // Step 2: System Recovery - Load state from file on startup
        loadSystemState();

        // Initialize default inventory ONLY if file was empty/missing
        if (inventory.isEmpty()) {
            System.out.println("Initializing fresh inventory...");
            inventory.put("Single Room", 5);
            inventory.put("Double Room", 5);
            allocatedRooms.put("Single Room", new HashSet<>());
            allocatedRooms.put("Double Room", new HashSet<>());
        }

        // Add Shutdown Hook to save state automatically on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[System] Shutdown detected. Saving state...");
            saveSystemState();
        }));

        // Step 3: Simulate new bookings
        processNewRequests();

        generateBookingHistoryReport();
        System.out.println("\nProgram Finished. Restart to see persisted data.");
    }

    /**
     * UC12: Save data to persistent storage
     */
    private static void saveSystemState() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(inventory);
            oos.writeObject(bookingHistory);
            oos.writeObject(allocatedRooms);
            System.out.println("SUCCESS: System state persisted to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("ERROR: Could not save state - " + e.getMessage());
        }
    }

    /**
     * UC12: Restore data from persistent storage
     */
    @SuppressWarnings("unchecked")
    private static void loadSystemState() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No recovery file found. Starting fresh.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            inventory = (Map<String, Integer>) ois.readObject();
            bookingHistory = (List<Reservation>) ois.readObject();
            allocatedRooms = (Map<String, Set<String>>) ois.readObject();
            System.out.println("RECOVERY: Previous state restored. Confirmed Bookings: " + bookingHistory.size());
        } catch (Exception e) {
            System.err.println("RECOVERY FAILED: Data file corrupted or incompatible. Starting fresh.");
        }
    }

    private static void processNewRequests() {
        bookingQueue.add(new Reservation("Abhi", "Single Room"));
        bookingQueue.add(new Reservation("Subha", "Double Room"));

        System.out.println("Processing " + bookingQueue.size() + " new requests...");

        while (!bookingQueue.isEmpty()) {
            processBooking(bookingQueue.poll());
        }
    }

    private static void processBooking(Reservation request) {
        synchronized (lock) {
            try {
                validateBooking(request);
                String roomID = generateRoomID(request.getRoomType());

                allocatedRooms.get(request.getRoomType()).add(roomID);
                inventory.put(request.getRoomType(), inventory.get(request.getRoomType()) - 1);
                bookingHistory.add(request);

                System.out.println("CONFIRMED: " + request.getGuestName() + " -> " + roomID);
            } catch (InvalidBookingException e) {
                System.out.println("REJECTED: " + request.getGuestName() + " - " + e.getMessage());
            }
        }
    }

    private static void validateBooking(Reservation res) throws InvalidBookingException {
        String type = res.getRoomType();
        if (!inventory.containsKey(type) || inventory.get(type) <= 0) {
            throw new InvalidBookingException("No rooms available for " + type);
        }
    }

    private static String generateRoomID(String type) {
        String prefix = type.substring(0, 2).toUpperCase();
        int roomNumber = 101 + (allocatedRooms.get(type) != null ? allocatedRooms.get(type).size() : 0);
        return prefix + "-" + roomNumber;
    }

    private static void generateBookingHistoryReport() {
        System.out.println("\n--- Current Booking Audit Report ---");
        if (bookingHistory.isEmpty()) System.out.println("No bookings found.");
        bookingHistory.forEach(res -> System.out.println("ID: " + res.getReservationID() + " | Guest: " + res.getGuestName()));
        System.out.println("Current Inventory: " + inventory);
        System.out.println("------------------------------------");
    }
}