/**
 * Use Case 2: Basic Room Types & Static Availability
 * * This version introduces:
 * - Abstraction & Inheritance (Room hierarchy)
 * - Encapsulation (Private attributes)
 * - Static Availability (Simple variable tracking)
 * * @author Developer
 * @version 2.0
 */

// Abstract base class for domain modeling
abstract class Room {
    private String type;
    private int beds;
    private double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Beds     : " + beds);
        System.out.println("Price    : $" + price);
    }
}

// Concrete implementations demonstrating Inheritance
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 100.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 180.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 4, 350.0);
    }
}

public class BookMyStayApp {
    // Static Availability Representation (Requirement for UC2)
    private static int singleRoomAvailability = 10;
    private static int doubleRoomAvailability = 7;
    private static int suiteRoomAvailability = 3;

    public static void main(String[] args) {
        System.out.println("--- Welcome to Book My Stay App v2.0 ---");
        System.out.println("Initializing Room Inventory...\n");

        // Polymorphism: Using the Room base type to reference different objects
        Room single = new SingleRoom();
        Room dual = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Display Room 1
        single.displayDetails();
        System.out.println("Current Availability: " + singleRoomAvailability + "\n");

        // Display Room 2
        dual.displayDetails();
        System.out.println("Current Availability: " + doubleRoomAvailability + "\n");

        // Display Room 3
        suite.displayDetails();
        System.out.println("Current Availability: " + suiteRoomAvailability + "\n");

        System.out.println("System initialized successfully.");
    }
}