import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Cinema booking program expanded from the original array-based version. */
public class Cinemabook {
    private static final String[] MOVIES = {"Akanda", "Salaar", "Pushpa"};
    private static final String[] LOCATIONS = {
        "Lingampally", "Kukatpally", "Miyapur"
    };
    private static final String[][] THEATRE_NAMES = {
        {"GSM", "Miraj Cinemas"},
        {"Arjun", "Malikarjuna Theaters"},
        {"Sai Ranga Theater", "JPG Cinemas"}
    };

    private static final int SEAT_COUNT = 400;
    private static final double TICKET_PRICE = 350.00;
    private static final double TAX_RATE = 0.05;
    private static final Path BOOKINGS_FILE = Path.of("bookings.txt");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Theatre[][] theatres = createTheatres();
        List<Booking> bookings = loadBookings(theatres);

        System.out.println("=== CINEMA SEAT BOOKING SYSTEM ===");
        printMovies();

        int movieNo = readChoice(
            sc, "Enter movie number: ", 1, MOVIES.length
        );
        System.out.println("You selected " + MOVIES[movieNo - 1]);

        printLocations();
        int locationNo = readChoice(
            sc, "Enter location number: ", 1, LOCATIONS.length
        );

        System.out.println("\nTheatres in " + LOCATIONS[locationNo - 1] + ":");
        for (int i = 0; i < THEATRE_NAMES[locationNo - 1].length; i++) {
            System.out.printf(
                "%d. %s%n", i + 1, THEATRE_NAMES[locationNo - 1][i]
            );
        }

        int theatreNo = readChoice(
            sc,
            "Enter theatre number: ",
            1,
            THEATRE_NAMES[locationNo - 1].length
        );

        Theatre theatre = theatres[locationNo - 1][theatreNo - 1];
        System.out.println("You selected " + theatre);
        System.out.println(
            "\nAvailable seats: " + theatre.getAvailableSeatCount()
                + " out of " + SEAT_COUNT
        );

        int seatNo = readChoice(sc, "Enter your seat number (1-400): ", 1, SEAT_COUNT);

        if (!theatre.isSeatAvailable(seatNo)) {
            System.out.println("Sorry, this seat is already booked.");
            sc.close();
            return;
        }

        Booking booking = new Booking(
            MOVIES[movieNo - 1], theatre, seatNo, TICKET_PRICE, TAX_RATE
        );

        printBill(booking);

        if (readConfirmation(sc)) {
            theatre.bookSeat(seatNo);
            bookings.add(booking);
            saveBooking(movieNo - 1, locationNo - 1, theatreNo - 1, booking);

            System.out.println("Successfully confirmed your seat!");
            printBookingSummary(bookings, theatre);
        } else {
            System.out.println("Seat booking cancelled.");
        }

        sc.close();
    }

    private static Theatre[][] createTheatres() {
        Theatre[][] result = new Theatre[LOCATIONS.length][2];

        for (int location = 0; location < LOCATIONS.length; location++) {
            for (int theatre = 0;
                    theatre < THEATRE_NAMES[location].length;
                    theatre++) {
                result[location][theatre] = new Theatre(
                    LOCATIONS[location],
                    THEATRE_NAMES[location][theatre],
                    SEAT_COUNT
                );
            }
        }

        return result;
    }

    private static void printMovies() {
        System.out.println("\nMovie List:");

        for (int i = 0; i < MOVIES.length; i++) {
            System.out.printf("%d. %s%n", i + 1, MOVIES[i]);
        }
    }

    private static void printLocations() {
        System.out.println("\nLocation List:");

        for (int i = 0; i < LOCATIONS.length; i++) {
            System.out.printf("%d. %s%n", i + 1, LOCATIONS[i]);
        }
    }

    private static int readChoice(
            Scanner sc, String prompt, int minimum, int maximum) {
        while (true) {
            System.out.print(prompt);
            String text = sc.nextLine().trim();

            try {
                int choice = Integer.parseInt(text);

                if (choice >= minimum && choice <= maximum) {
                    return choice;
                }

                System.out.printf(
                    "Enter a number from %d to %d.%n", minimum, maximum
                );
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static boolean readConfirmation(Scanner sc) {
        while (true) {
            System.out.print(
                "Do you want to confirm your seat? (yes/no): "
            );
            String answer = sc.nextLine().trim();

            if (answer.equalsIgnoreCase("yes")) {
                return true;
            }

            if (answer.equalsIgnoreCase("no")) {
                return false;
            }

            System.out.println("Please type yes or no.");
        }
    }

    private static void printBill(Booking booking) {
        System.out.println("\n--- BOOKING BILL ---");
        System.out.println("Movie: " + booking.getMovie());
        System.out.println("Theatre: " + booking.getTheatre());
        System.out.println("Seat number: " + booking.getSeatNumber());
        System.out.printf(
            "Ticket price: Rs. %.2f%n", booking.getTicketPrice()
        );
        System.out.printf(
            "Tax (%.0f%%): Rs. %.2f%n",
            TAX_RATE * 100,
            booking.getTax()
        );
        System.out.printf(
            "Total amount: Rs. %.2f%n", booking.calculateTotal()
        );
    }

    private static void printBookingSummary(
            List<Booking> bookings, Theatre theatre) {
        long soldAtTheatre = bookings.stream()
            .filter(booking -> booking.getTheatre() == theatre)
            .count();

        double totalSales = bookings.stream()
            .mapToDouble(Booking::calculateTotal)
            .sum();

        System.out.println(
            "\nBookings recorded for this theatre: " + soldAtTheatre
        );
        System.out.printf("Total sales recorded: Rs. %.2f%n", totalSales);
    }

    private static List<Booking> loadBookings(Theatre[][] theatres) {
        List<Booking> bookings = new ArrayList<>();

        if (!Files.exists(BOOKINGS_FILE)) {
            return bookings;
        }

        try {
            for (String line : Files.readAllLines(BOOKINGS_FILE)) {
                String[] data = line.split(",", -1);

                if (data.length != 4) {
                    continue;
                }

                try {
                    int movieIndex = Integer.parseInt(data[0]);
                    int locationIndex = Integer.parseInt(data[1]);
                    int theatreIndex = Integer.parseInt(data[2]);
                    int seatNo = Integer.parseInt(data[3]);

                    if (movieIndex >= 0 && movieIndex < MOVIES.length
                            && locationIndex >= 0
                            && locationIndex < theatres.length
                            && theatreIndex >= 0
                            && theatreIndex < theatres[locationIndex].length
                            && seatNo >= 1 && seatNo <= SEAT_COUNT) {

                        Theatre theatre = theatres[locationIndex][theatreIndex];

                        if (theatre.isSeatAvailable(seatNo)) {
                            theatre.bookSeat(seatNo);
                            bookings.add(new Booking(
                                MOVIES[movieIndex],
                                theatre,
                                seatNo,
                                TICKET_PRICE,
                                TAX_RATE
                            ));
                        }
                    }
                } catch (IllegalArgumentException exception) {
                    // Skip malformed or duplicate records.
                }
            }
        } catch (IOException exception) {
            System.out.println(
                "Could not read saved bookings: " + exception.getMessage()
            );
        }

        return bookings;
    }

    private static void saveBooking(
            int movieIndex,
            int locationIndex,
            int theatreIndex,
            Booking booking) {
        String record = movieIndex + ","
            + locationIndex + ","
            + theatreIndex + ","
            + booking.getSeatNumber()
            + System.lineSeparator();

        try {
            Files.writeString(
                BOOKINGS_FILE,
                record,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException exception) {
            System.out.println(
                "Booking succeeded, but saving failed: "
                    + exception.getMessage()
            );
        }
    }
}

class Theatre {
    private final String location;
    private final String name;
    private final boolean[] seats;

    public Theatre(String location, String name, int seatCount) {
        this.location = location;
        this.name = name;
        this.seats = new boolean[seatCount];
    }

    public boolean isSeatAvailable(int seatNumber) {
        if (seatNumber < 1 || seatNumber > seats.length) {
            throw new IllegalArgumentException(
                "Seat number is outside the valid range."
            );
        }

        return !seats[seatNumber - 1];
    }

    public void bookSeat(int seatNumber) {
        if (!isSeatAvailable(seatNumber)) {
            throw new IllegalArgumentException("That seat is already booked.");
        }

        seats[seatNumber - 1] = true;
    }

    public int getAvailableSeatCount() {
        int available = 0;

        for (boolean booked : seats) {
            if (!booked) {
                available++;
            }
        }

        return available;
    }

    @Override
    public String toString() {
        return name + " in " + location;
    }
}

interface Billable {
    double calculateTotal();
}

class Booking implements Billable {
    private final String movie;
    private final Theatre theatre;
    private final int seatNumber;
    private final double ticketPrice;
    private final double taxRate;

    public Booking(
            String movie,
            Theatre theatre,
            int seatNumber,
            double ticketPrice,
            double taxRate) {
        this.movie = movie;
        this.theatre = theatre;
        this.seatNumber = seatNumber;
        this.ticketPrice = ticketPrice;
        this.taxRate = taxRate;
    }

    public String getMovie() {
        return movie;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public double getTax() {
        return ticketPrice * taxRate;
    }

    @Override
    public double calculateTotal() {
        return ticketPrice + getTax();
    }
}