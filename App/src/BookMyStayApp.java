import java.util.*;

/**
 * Use Case 11: Concurrent Booking Simulation (Thread Safety)
 * Goal: Demonstrate how synchronization ensures correctness under multi-user conditions.
 * @version 11.0
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
    // Shared Resources
    private static Map<String, Integer> inventory = new HashMap<>();
    private static Queue<Reservation> bookingQueue = new LinkedList<>();
    private static Map<String, Set<String>> allocatedRooms = new HashMap<>();
    private static List<Reservation> bookingHistory = new ArrayList<>();
    private static Stack<String> rollbackStack = new Stack<>();

    // Lock object for synchronization
    private static final Object lock = new Object();

    public static void main(String[] args) {
        // Step 1: Initialize Inventory
        inventory.put("Single Room", 2); // Limited stock to test concurrency
        inventory.put("Double Room", 2);

        allocatedRooms.put("Single Room", new HashSet<>());
        allocatedRooms.put("Double Room", new HashSet<>());

        System.out.println("--- Welcome to Book My Stay App v11.0 (Concurrent Version) ---");

        // Step 2: Queueing Multiple Requests
        bookingQueue.add(new Reservation("Abhi", "Single Room"));
        bookingQueue.add(new Reservation("Subha", "Single Room"));
        bookingQueue.add(new Reservation("Vanmathi", "Single Room")); // This should fail if stock is 2
        bookingQueue.add(new Reservation("John", "Double Room"));

        System.out.println("Simulating concurrent processing for " + bookingQueue.size() + " requests...\n");

        // Step 3: Create Threads to simulate concurrent users
        List<Thread> threads = new ArrayList<>();

        // We create 4 threads to process the 4 requests in the queue
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(new BookingProcessor(), "Thread-" + (i + 1));
            threads.add(t);
            t.start();
        }

        // Wait for all threads to complete
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        generateBookingHistoryReport();
    }

    /**
     * Runnable task to process bookings concurrently
     */
    static class BookingProcessor implements Runnable {
        @Override
        public void run() {
            Reservation request;

            // Critical Section: Accessing the shared queue
            synchronized (bookingQueue) {
                request = bookingQueue.poll();
            }

            if (request != null) {
                processBooking(request);
            }
        }
    }

    /**
     * UC11: Synchronized method to ensure Thread Safety
     */
    private static void processBooking(Reservation request) {
        // The entire validation and update logic must be atomic
        synchronized (lock) {
            try {
                System.out.println(Thread.currentThread().getName() + " is processing " + request.getGuestName() + "...");

                validateBooking(request);

                String type = request.getRoomType();
                String roomID = generateRoomID(type);

                // Update State
                allocatedRooms.get(type).add(roomID);
                inventory.put(type, inventory.get(type) - 1);
                bookingHistory.add(request);
                rollbackStack.push(roomID);

                System.out.println("SUCCESS: " + request.getGuestName() + " got " + roomID + " [" + Thread.currentThread().getName() + "]");

            } catch (InvalidBookingException e) {
                System.out.println("REJECTED: " + request.getGuestName() + " - " + e.getMessage() + " [" + Thread.currentThread().getName() + "]");
            }
        }
    }

    private static void validateBooking(Reservation res) throws InvalidBookingException {
        String type = res.getRoomType();
        if (inventory.get(type) <= 0) {
            throw new InvalidBookingException("No rooms available.");
        }
    }

    private static String generateRoomID(String type) {
        String prefix = type.substring(0, 2).toUpperCase();
        int roomNumber = 101 + allocatedRooms.get(type).size();
        return prefix + "-" + roomNumber;
    }

    private static void generateBookingHistoryReport() {
        System.out.println("\n--- Final Concurrent Booking Audit Report ---");
        bookingHistory.forEach(res -> System.out.println("Confirmed -> " + res.getGuestName()));
        System.out.println("----------------------------------------------");
    }
}