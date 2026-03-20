import java.util.*;

/**
 * Use Case 7: Add-On Service Selection
 * Goal: Support optional services and calculate costs without modifying core logic.
 * @version 7.0
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

    // UC7: Map Reservation ID to a List of Add-On Services
    private static Map<String, List<Service>> addOnServices = new HashMap<>();

    public static void main(String[] args) {
        // Step 1: Initialize Inventory (UC6)
        inventory.put("Single Room", 10);
        inventory.put("Double Room", 0);
        inventory.put("Suite Room", 3);

        allocatedRooms.put("Single Room", new HashSet<>());
        allocatedRooms.put("Double Room", new HashSet<>());
        allocatedRooms.put("Suite Room", new HashSet<>());

        System.out.println("--- Welcome to Book My Stay App v7.0 ---");

        // Step 2: Queueing Requests
        Reservation res1 = new Reservation("Alice", "Single Room");
        Reservation res2 = new Reservation("Bob", "Suite Room");

        bookingQueue.add(res1);
        bookingQueue.add(res2);
        bookingQueue.add(new Reservation("Charlie", "Single Room"));

        // Step 3: UC7 - Guest selects Add-On Services
        addServiceToReservation(res1.getReservationID(), new Service("Breakfast", 15.0));
        addServiceToReservation(res1.getReservationID(), new Service("Late Checkout", 20.0));
        addServiceToReservation(res2.getReservationID(), new Service("Spa Treatment", 50.0));

        System.out.println("Processing " + bookingQueue.size() + " queued requests...\n");

        // Step 4: Process Queue and Allocate Rooms
        System.out.println("====================================================================");
        System.out.printf("%-12s | %-8s | %-10s | %-10s | %-10s%n",
                "RES ID", "STATUS", "GUEST", "ROOM ID", "ADD-ONS COST");
        System.out.println("--------------------------------------------------------------------");

        while (!bookingQueue.isEmpty()) {
            Reservation request = bookingQueue.poll();
            String type = request.getRoomType();
            String resID = request.getReservationID();
            int currentStock = inventory.getOrDefault(type, 0);

            if (currentStock > 0) {
                String roomID = generateRoomID(type);
                allocatedRooms.get(type).add(roomID);
                inventory.put(type, currentStock - 1);

                // UC7: Calculate total cost for services
                double serviceTotal = calculateServiceCost(resID);

                System.out.printf("%-12s | %-8s | %-10s | %-10s | $%-10.2f%n",
                        resID, "SUCCESS", request.getGuestName(), roomID, serviceTotal);

                // Print specific services if any exist
                if (addOnServices.containsKey(resID)) {
                    System.out.println("   ㄴ Services: " + addOnServices.get(resID));
                }
            } else {
                System.out.printf("%-12s | %-8s | %-10s | %-10s | %-10s%n",
                        resID, "REJECTED", request.getGuestName(), "N/A", "N/A");
            }
        }
        System.out.println("====================================================================\n");
    }

    /**
     * UC7: Adds a service to the reservation mapping.
     */
    private static void addServiceToReservation(String resID, Service service) {
        addOnServices.computeIfAbsent(resID, k -> new ArrayList<>()).add(service);
    }

    /**
     * UC7: Iterates through the list of services for a reservation to sum the price.
     */
    private static double calculateServiceCost(String resID) {
        List<Service> services = addOnServices.get(resID);
        if (services == null) return 0.0;

        double total = 0;
        for (Service s : services) {
            total += s.getPrice();
        }
        return total;
    }

    private static String generateRoomID(String type) {
        String prefix = type.substring(0, 2).toUpperCase();
        int roomNumber = 101 + allocatedRooms.get(type).size();
        return prefix + "-" + roomNumber;
    }
}