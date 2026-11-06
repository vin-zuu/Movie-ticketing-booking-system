package ticketbooking;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JLabel;

// Booking Records in simple JOptionPane boxes.
//   View All : every booking in the order they were made
//   Sort     : BUBBLE SORT by ID, customer, movie, quantity or price
//   Search   : LINEAR SEARCH by booking ID, customer name or movie title
//   Cancel   : Admin only (needs the Admin password)
public class BookingRecords {

    private static final String[] SORT_FIELDS = {
        "Booking ID", "Customer Name", "Movie Title", "Quantity", "Total Price"
    };
    private static final String[] SEARCH_FIELDS = {
        "Booking ID", "Customer Name", "Movie Title"
    };

    // Columns of the booking table (JTable) and how wide each one is
    private static final String[] COLUMNS = {
        "ID", "Movie", "Customer", "Movie Title", "Showtime", "Seats",
        "Type", "Qty", "Payment", "Total"
    };
    private static final int[] COLUMN_WIDTHS = {55, 55, 160, 200, 85, 120, 70, 40, 80, 90};

    // Heading of the list on the screen (changes after a sort or a search)
    private static String heading = "All Bookings";

    public static void show(Component parent, BookingManager bookingManager,
                            AccountManager accountManager, UserAccount user) {
        String[] buttons;
        String access;

        // Staff can only view. Only the Admin can cancel bookings.
        if (user.isAdmin()) {
            buttons = new String[]{"View All", "Sort", "Search", "Cancel Booking", "Close"};
            access = "Admin: can view and cancel bookings";
        } else {
            buttons = new String[]{"View All", "Sort", "Search", "Close"};
            access = "Staff: view only";
        }

        // The booking list is the main screen. All the buttons are under the
        // list, so the user can sort, search or cancel right away.
        ArrayList<Booking> shown = bookingManager.getBookingList();
        heading = "All Bookings";

        while (true) {
            JLabel accessLabel = Theme.makeLabel(access, 14, false, Theme.SOFT_TEXT);
            String footer = "Total bookings shown: " + shown.size();
            if (shown.isEmpty()) {
                footer = "(No bookings yet)   " + footer;
            }
            int choice = Popup.showTableWithButtons(parent,
                    "Booking Records - " + heading, accessLabel,
                    COLUMNS, tableRows(shown), COLUMN_WIDTHS, footer, buttons);

            if (choice == -1 || choice == buttons.length - 1) {   // Close is the last button
                return;
            }

            // After every action the screen goes back to the full list,
            // except when a sort or a search gave a result.
            shown = bookingManager.getBookingList();
            heading = "All Bookings";

            // The number of the button: 0 View All, 1 Sort, 2 Search,
            // 3 Cancel Booking (Admin only)
            if (choice == 1) {
                ArrayList<Booking> sorted = sortBookings(parent, bookingManager);
                if (sorted != null) {
                    shown = sorted;
                }
            } else if (choice == 2) {
                ArrayList<Booking> results = searchBookings(parent, bookingManager);
                if (results != null) {
                    shown = results;
                }
            } else if (choice == 3) {
                cancelBooking(parent, bookingManager, accountManager, user);
                shown = bookingManager.getBookingList();
            }
        }
    }
    // Bubble sort: choose what to sort by, then ascending or descending
    private static ArrayList<Booking> sortBookings(Component parent, BookingManager bookingManager) {
        if (bookingManager.getBookingList().isEmpty()) {
            Popup.info(parent, "There are no bookings to sort.");
            return null;
        }

        int field = Popup.pick(parent, "Sort Bookings",
                "Sort the bookings by:", SORT_FIELDS);
        if (field == -1) {
            return null;
        }

        String[] orders = {"Ascending (A-Z / lowest first)",
                           "Descending (Z-A / highest first)"};
        int order = Popup.choose(parent, "Sort Bookings",
                "Sort by " + SORT_FIELDS[field] + ":", orders);
        if (order == -1) {
            return null;
        }

        ArrayList<Booking> sorted = bookingManager.bubbleSort(
                SORT_FIELDS[field], order == 0);

        String way = "ascending";
        if (order == 1) {
            way = "descending";
        }
        heading = "Sorted by " + SORT_FIELDS[field] + " (" + way + ", Bubble Sort)";
        return sorted;
    }
    // Linear search: choose where to look, then type the exact words
    private static ArrayList<Booking> searchBookings(Component parent, BookingManager bookingManager) {
        int field = Popup.pick(parent, "Search Bookings",
                "Search by:", SEARCH_FIELDS);
        if (field == -1) {
            return null;
        }

        String key = Popup.ask(parent, "Search Bookings",
                "Type the exact " + SEARCH_FIELDS[field] + ":");
        if (key == null) {
            return null;
        }
        if (key.isEmpty()) {
            Popup.info(parent, "Please type something to search.");
            return null;
        }

        ArrayList<Booking> results = bookingManager.linearSearch(
                SEARCH_FIELDS[field], key);

        if (results.isEmpty()) {
            Popup.info(parent, "Booking not found.");
            return null;
        }
        heading = "Found " + results.size() + " booking(s)";
        return results;
    }
    private static void cancelBooking(Component parent, BookingManager bookingManager,
                                      AccountManager accountManager, UserAccount user) {
        // Extra safety: only the Admin can cancel
        if (!user.isAdmin()) {
            Popup.info(parent, "Only the Admin can cancel bookings.");
            return;
        }

        ArrayList<Booking> bookings = bookingManager.getBookingList();
        if (bookings.isEmpty()) {
            Popup.info(parent, "There are no bookings to cancel.");
            return;
        }

        String[] items = new String[bookings.size()];
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            items[i] = b.getBookingId() + " - " + b.getCustomerName()
                    + " - " + b.getMovieTitle() + " (" + b.getShowTime() + ")";
        }

        int index = Popup.pick(parent, "Cancel Booking",
                "Select the booking to cancel:", items);
        if (index == -1) {
            return;
        }

        String bookingId = bookings.get(index).getBookingId();

        // Deleting needs the Admin password
        if (Popup.yes(parent, "Confirm Cancellation", "Cancel booking " + bookingId + "?")
                && AdminAuth.confirm(parent, accountManager, user,
                        "Cancel booking " + bookingId)) {
            if (bookingManager.cancelBooking(bookingId)) {
                Popup.info(parent, "Booking cancelled successfully.");
            }
        }
    }
    // The booking rows for the JTable: one row for every booking
    private static Object[][] tableRows(ArrayList<Booking> bookings) {
        Object[][] rows = new Object[bookings.size()][COLUMNS.length];

        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            String seats = "-";
            if (!b.getSeats().isEmpty()) {
                seats = b.getSeats().replace(",", ", ");
            }
            rows[i][0] = b.getBookingId();
            rows[i][1] = b.getMovieId();
            rows[i][2] = b.getCustomerName();
            rows[i][3] = b.getMovieTitle();
            rows[i][4] = b.getShowTime();
            rows[i][5] = seats;
            rows[i][6] = b.getTicketType();
            rows[i][7] = b.getQuantity();
            rows[i][8] = b.getPaymentMode();
            rows[i][9] = Theme.peso(b.getTotalPrice());
        }
        return rows;
    }
}
