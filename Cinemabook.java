import java.util.Scanner;
public class Cinemabook {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] movies = {"Akanda", "Salaar", "Pushpa"};
        String[] locations = {"Lingampally", "Kukatpally", "Miyapur"};
        String[][] theatres = {
            {"GSM", "Miraj Cinemas"},
            {"Arjun", "Malikarjuna Theaters"},
            {"Sai Ranga Theater", "JPG Cinemas"}
        };
        boolean[] seats = new boolean[400];
        System.out.println("Movie List:");
        System.out.println("1. " + movies[0]);
        System.out.println("2. " + movies[1]);
        System.out.println("3. " + movies[2]);
        System.out.print("Enter movie number: ");
        int movieNo = sc.nextInt();
        if (movieNo >= 1 && movieNo <= 3) {
            System.out.println("You selected " + movies[movieNo - 1]);
        } else {
            System.out.println("Invalid movie number");
            sc.close();
            return;
        }
        System.out.println("\nLocation List:");
        System.out.println("1. " + locations[0]);
        System.out.println("2. " + locations[1]);
        System.out.println("3. " + locations[2]);
        System.out.print("Enter location number: ");
        int locationNo = sc.nextInt();
        if (locationNo >= 1 && locationNo <= 3) {
            System.out.println("\nTheatres in " + locations[locationNo - 1] + ":");
            System.out.println("1. " + theatres[locationNo - 1][0]);
            System.out.println("2. " + theatres[locationNo - 1][1]);
        } else {
            System.out.println("Invalid location number");
            sc.close();
            return;
        }
        System.out.print("Enter theatre number: ");
        int theatreNo = sc.nextInt();
        if (theatreNo >= 1 && theatreNo <= 2) {
            System.out.println("You selected "
                    + theatres[locationNo - 1][theatreNo - 1]);
        } else {
            System.out.println("Invalid theatre number");
            sc.close();
            return;
        }
        System.out.println("\nThere are 400 seats available in the theatre.");
        System.out.print("Enter your seat number: ");
        int seatNo = sc.nextInt();
        if (seatNo >= 1 && seatNo <= 400) {
            if (seats[seatNo - 1] == false) {
                int ticketPrice = 350;
                System.out.println("Seat number: " + seatNo);
                System.out.println("Ticket price: Rs. " + ticketPrice);
                System.out.print("Do you want to confirm your seat? (yes/no): ");
                String confirm = sc.next();
                if (confirm.equalsIgnoreCase("yes")) {
                    seats[seatNo - 1] = true;
                    System.out.println("Successfully confirmed your seat!");
                } else {
                    System.out.println("Seat booking cancelled.");
                }
            } else {
                System.out.println("Sorry, this seat is already booked.");
            }
        } else {
            System.out.println(
                "Invalid seat number. Please choose a seat between 1 and 400."
            );
        }
        sc.close();
    }
}