package ticketbooking;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JPanel;

// Admin only: the sales report with three information cards and a table
public class SalesReport {

    // Columns of the sales table (JTable) and how wide each one is
    private static final String[] COLUMNS = {
        "ID", "Movie", "Customer", "Movie Title", "Showtime",
        "Type", "Tickets", "Amount"
    };
    private static final int[] COLUMN_WIDTHS = {55, 55, 160, 220, 85, 75, 60, 100};

    public static void show(Component parent, BookingManager bookingManager) {
        JPanel cards = new JPanel(new GridLayout(1, 3, 10, 0));
        cards.setOpaque(false);
        cards.add(Theme.createStatCard("TOTAL BOOKINGS",
                "" + bookingManager.getBookingList().size()));
        cards.add(Theme.createStatCard("TOTAL TICKETS SOLD",
                "" + bookingManager.getTotalTickets()));
        cards.add(Theme.createStatCard("TOTAL SALES",
                Theme.peso(bookingManager.getTotalSales())));

        // the cards, then the heading of the table
        JPanel top = new JPanel(new GridLayout(0, 1, 0, 10));
        top.setOpaque(false);
        top.add(cards);
        top.add(Theme.makeLabel("ALL BOOKING DETAILS", 14, true, Theme.TITLE));

        ArrayList<Booking> bookings = bookingManager.getBookingList();
        Object[][] rows = new Object[bookings.size()][COLUMNS.length];

        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            rows[i][0] = b.getBookingId();
            rows[i][1] = b.getMovieId();
            rows[i][2] = b.getCustomerName();
            rows[i][3] = b.getMovieTitle();
            rows[i][4] = b.getShowTime();
            rows[i][5] = b.getTicketType();
            rows[i][6] = b.getQuantity();
            rows[i][7] = Theme.peso(b.getTotalPrice());
        }

        String footer = "Total bookings shown: " + bookings.size();
        if (bookings.isEmpty()) {
            footer = "(No bookings yet)";
        }
        Popup.showTable(parent, "Sales Report", top, COLUMNS, rows,
                COLUMN_WIDTHS, footer);
    }
}
