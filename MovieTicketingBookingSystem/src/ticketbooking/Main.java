package ticketbooking;

public class Main {
    public static void main(String[] args) {

        // These are created once, so the data stays even after logout.
        // Nothing is saved to a file: all data is gone when the program closes.
        AccountManager accountManager = new AccountManager();
        MovieManager movieManager = new MovieManager();
        BookingManager bookingManager = new BookingManager();

        LoginFrame loginFrame = new LoginFrame(
                accountManager, movieManager, bookingManager);
        loginFrame.setVisible(true);
    }
}
