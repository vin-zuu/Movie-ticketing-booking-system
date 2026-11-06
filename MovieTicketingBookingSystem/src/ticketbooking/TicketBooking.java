package ticketbooking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

// Ticket Booking in ONE window. Everything is on the same screen:
// customer name, movie, showtime, ticket type, ID number, payment mode and
// the seat map. Press "Book Now" to save the booking, or "Cancel" to close
// the window (nothing is added when cancelled).
public class TicketBooking implements ActionListener {

    private static final int SEAT_ROWS = 8;
    private static final int SEAT_COLS = 10;
    private static final int MAX_SEATS = 20;
    private static final String ROW_NAMES = "ABCDEFGH";

    private static final String[] SHOW_TIMES = {"10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM"};
    private static final String[] TICKET_TYPES = {"Regular", "Student", "Senior", "PWD"};
    private static final String[] PAYMENTS = {"Cash", "Cashless"};

    private BookingManager bookingManager;
    private ArrayList<Movie> showing;   // the movies that can be booked

    private JDialog dialog;

    // Parts of the form
    private JTextField customerField;
    private JComboBox<String> movieBox;
    private JComboBox<String> timeBox;
    private JComboBox<String> typeBox;
    private JTextField idField;
    private JComboBox<String> paymentBox;

    // Seat map
    private JPanel seatGrid;
    private JButton[][] seatButtons;
    private boolean[][] chosen;

    // Labels that change when the choices change
    private JLabel movieTitleLabel;
    private JLabel priceLabel;
    private JLabel infoLabel;

    private JButton bookButton;
    private JButton cancelButton;

    // The ticket of the chosen type, with the price of the chosen movie
    private Ticket ticket;

    // The Dashboard calls this one method to open the booking window
    public static void show(Component parent, MovieManager movieManager,
                            BookingManager bookingManager) {
        // ---------- the movies that can be booked ----------
        ArrayList<Movie> showing = new ArrayList<Movie>();
        for (Movie movie : movieManager.getMovieList()) {
            if ("Now Showing".equalsIgnoreCase(movie.getStatus())) {
                showing.add(movie);
            }
        }
        if (showing.isEmpty()) {
            Popup.info(parent, "No movies available. Add a Now Showing movie first.");
            return;
        }

        TicketBooking booking = new TicketBooking(parent, bookingManager, showing);
        booking.dialog.setVisible(true);   // waits here until the window is closed
    }
    // Builds the whole window
    private TicketBooking(Component parent, BookingManager bookingManager,
                          ArrayList<Movie> showing) {
        this.bookingManager = bookingManager;
        this.showing = showing;

        Window owner = SwingUtilities.getWindowAncestor(parent);
        dialog = new JDialog(owner, "Ticket Booking", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(Theme.BACKGROUND);
        dialog.setLayout(new BorderLayout(0, 10));

        // ---------- LEFT: the form ----------
        customerField = new JTextField(18);
        customerField.setToolTipText("Name of the customer who is buying the tickets");

        String[] movieItems = new String[showing.size()];
        for (int i = 0; i < showing.size(); i++) {
            Movie m = showing.get(i);
            movieItems[i] = m.getMovieId() + " - " + m.getTitle()
                    + "  (" + Theme.peso(m.getPrice()) + ")";
        }
        movieBox = new JComboBox<String>(movieItems);
        movieBox.setToolTipText("Only the Now Showing movies can be booked");

        timeBox = new JComboBox<String>(SHOW_TIMES);
        timeBox.setToolTipText("The seats on the right change for each showtime");

        String[] typeItems = new String[TICKET_TYPES.length];
        for (int i = 0; i < TICKET_TYPES.length; i++) {
            Ticket t = bookingManager.getTicket(TICKET_TYPES[i]);
            typeItems[i] = TICKET_TYPES[i];
            if (t.getDiscountPercent() > 0) {
                typeItems[i] = typeItems[i] + " (" + t.getDiscountPercent()
                        + "% off, ID required)";
            }
        }
        typeBox = new JComboBox<String>(typeItems);
        typeBox.setToolTipText("Student, Senior and PWD get a discount on the total");

        idField = new JTextField();
        idField.setToolTipText("Only needed for Student, Senior and PWD tickets");

        paymentBox = new JComboBox<String>(PAYMENTS);
        paymentBox.setToolTipText("Cash, or Cashless (GCash / Card / Maya)");

        Theme.styleField(customerField);
        Theme.styleField(movieBox);
        Theme.styleField(timeBox);
        Theme.styleField(typeBox);
        Theme.styleField(idField);
        Theme.styleField(paymentBox);

        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.setOpaque(false);
        form.add(Theme.makeBoldLabel("Customer name:"));
        form.add(customerField);
        form.add(Theme.makeBoldLabel("Movie:"));
        form.add(movieBox);
        form.add(Theme.makeBoldLabel("Showtime:"));
        form.add(timeBox);
        form.add(Theme.makeBoldLabel("Ticket type:"));
        form.add(typeBox);
        form.add(Theme.makeBoldLabel("ID number (Student, Senior, PWD):"));
        form.add(idField);
        form.add(Theme.makeBoldLabel("Mode of payment:"));
        form.add(paymentBox);

        JPanel formSide = new JPanel(new BorderLayout());
        formSide.setOpaque(false);
        formSide.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 20));
        formSide.setPreferredSize(new Dimension(420, 0));
        formSide.add(form, BorderLayout.NORTH);

        // ---------- RIGHT: the seat map ----------
        movieTitleLabel = Theme.makeLabel("", 16, true, Theme.TITLE);

        JLabel screen = Theme.makeLabel("S C R E E N", 14, true, Theme.TEXT);
        screen.setHorizontalAlignment(SwingConstants.CENTER);
        screen.setOpaque(true);
        screen.setBackground(Theme.LINE);
        screen.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JPanel seatTop = new JPanel(new BorderLayout(0, 8));
        seatTop.setOpaque(false);
        seatTop.add(movieTitleLabel, BorderLayout.NORTH);
        seatTop.add(screen, BorderLayout.CENTER);

        seatGrid = new JPanel(new GridLayout(SEAT_ROWS, SEAT_COLS + 1, 4, 4));
        seatGrid.setOpaque(false);

        priceLabel = new JLabel();
        infoLabel = Theme.makeLabel("", 14, true, Theme.PRIMARY);

        JPanel seatBottom = new JPanel(new GridLayout(0, 1, 0, 3));
        seatBottom.setOpaque(false);
        seatBottom.add(priceLabel);
        seatBottom.add(new JLabel("Gray = free     Teal = selected     XX = already taken"));
        seatBottom.add(infoLabel);

        JPanel seatSide = new JPanel(new BorderLayout(0, 10));
        seatSide.setOpaque(false);
        seatSide.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        seatSide.add(seatTop, BorderLayout.NORTH);
        seatSide.add(seatGrid, BorderLayout.CENTER);
        seatSide.add(seatBottom, BorderLayout.SOUTH);

        // ---------- BOTTOM: the buttons ----------
        bookButton = new JButton("Book Now");
        Theme.styleButton(bookButton, Theme.PRIMARY, Color.WHITE);
        bookButton.setMnemonic('B');
        bookButton.setToolTipText("Save the booking and show the receipt (Alt+B)");
        bookButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        Theme.styleButton(cancelButton, Theme.NEUTRAL, Theme.TEXT);
        cancelButton.setMnemonic('C');
        cancelButton.setToolTipText("Close this window without booking (Alt+C)");
        cancelButton.addActionListener(this);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 15, 12, 15));
        buttons.add(bookButton);
        buttons.add(cancelButton);

        // ---------- put everything in the window ----------
        dialog.add(formSide, BorderLayout.WEST);
        dialog.add(seatSide, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);

        // Fill the seat map and the price. The listeners are added AFTER this,
        // so they do not run while the window is still being built.
        buildSeats();
        movieBox.addActionListener(this);
        timeBox.addActionListener(this);
        typeBox.addActionListener(this);

        dialog.getRootPane().setDefaultButton(bookButton);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
    }
    // Runs when something in the window is clicked or changed
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == bookButton) {
            book();

        } else if (source == cancelButton) {
            dialog.dispose();

        } else if (source == movieBox || source == timeBox) {
            // other movie or showtime = other seats, so the map starts again
            buildSeats();

        } else if (source == typeBox) {
            updateTicket();

        } else {
            // one of the seat buttons: select it / unselect it
            for (int r = 0; r < SEAT_ROWS; r++) {
                for (int c = 0; c < SEAT_COLS; c++) {
                    if (seatButtons[r][c] == source) {
                        chosen[r][c] = !chosen[r][c];
                    }
                }
            }
            showColors();
        }
    }
    // Checks the form, then saves the booking and shows the receipt
    private void book() {
        String customer = customerField.getText().trim();
        if (customer.isEmpty()) {
            Popup.warn(dialog, "Check the Booking", "Please enter the customer's name.");
            customerField.requestFocus();
            return;
        }

        ArrayList<String> seats = getChosenSeats();
        if (seats.isEmpty()) {
            Popup.warn(dialog, "Check the Seats", "Please select at least one seat.");
            return;
        }
        if (seats.size() > MAX_SEATS) {
            Popup.warn(dialog, "Check the Seats",
                    "You can select up to " + MAX_SEATS + " seats per booking.");
            return;
        }

        String ticketType = TICKET_TYPES[typeBox.getSelectedIndex()];
        if (ticket.needsId() && idField.getText().trim().isEmpty()) {
            Popup.warn(dialog, "Check the Booking",
                    "Please enter the customer's ID number (" + ticketType + ").");
            idField.requestFocus();
            return;
        }

        Movie movie = showing.get(movieBox.getSelectedIndex());
        String showTime = SHOW_TIMES[timeBox.getSelectedIndex()];
        String paymentMode = PAYMENTS[paymentBox.getSelectedIndex()];
        int quantity = seats.size();
        double total = ticket.calculateTotal(quantity);

        String discountText = "";
        if (ticket.getDiscountPercent() > 0) {
            discountText = "Discount: " + ticket.getDiscountPercent()
                    + "% off the total\n";
        }

        // ---------- ask YES or NO before the booking is added ----------
        boolean confirmed = Popup.yes(dialog, "Confirm Booking",
                "Please check the booking:\n\n"
                + "Customer: " + customer + "\n"
                + "Movie: " + movie.getTitle() + "\n"
                + "Showtime: " + showTime + "\n"
                + "Seats: " + joinSeats(seats, ", ") + "\n"
                + "Ticket Type: " + ticketType + "\n"
                + "Quantity: " + quantity + "\n"
                + discountText
                + "Mode of Payment: " + paymentMode + "\n"
                + "Total: " + Theme.peso(total) + "\n\n"
                + "Confirm this booking?");
        if (!confirmed) {
            return;   // NO: nothing is added, the window stays open
        }

        // ---------- YES: add the booking and show the receipt ----------
        String bookingId = bookingManager.generateBookingId();

        Booking booking = new Booking(bookingId, movie.getMovieId(), customer,
                movie.getTitle(), showTime, ticketType, quantity, total,
                joinSeats(seats, ","), paymentMode);
        bookingManager.addBooking(booking);

        Popup.info(dialog, "BOOKING CONFIRMED!\n\n"
                + "Booking ID: " + bookingId + "\n"
                + "Customer: " + customer + "\n"
                + "Movie: " + movie.getTitle() + "\n"
                + "Showtime: " + showTime + "\n"
                + "Seats: " + joinSeats(seats, ", ") + "\n"
                + "Ticket Type: " + ticketType + "\n"
                + "Quantity: " + quantity + "\n"
                + discountText
                + "Mode of Payment: " + paymentMode + "\n"
                + "Total: " + Theme.peso(total));

        dialog.dispose();
    }
    // Draws one button for every seat, with a gap (aisle) in the middle.
    // Called again when the movie or the showtime is changed, because the
    // seats that are already taken are different for each one.
    private void buildSeats() {
        Movie movie = showing.get(movieBox.getSelectedIndex());
        String showTime = SHOW_TIMES[timeBox.getSelectedIndex()];

        seatButtons = new JButton[SEAT_ROWS][SEAT_COLS];
        chosen = new boolean[SEAT_ROWS][SEAT_COLS];
        seatGrid.removeAll();

        for (int r = 0; r < SEAT_ROWS; r++) {
            for (int c = 0; c < SEAT_COLS; c++) {
                if (c == SEAT_COLS / 2) {
                    seatGrid.add(new JLabel(""));   // aisle
                }
                String name = ROW_NAMES.charAt(r) + "" + (c + 1);

                JButton seat = new JButton(name);
                seat.setFont(new Font("SansSerif", Font.BOLD, 12));
                seat.setMargin(new Insets(6, 2, 6, 2));
                seat.setOpaque(true);
                seat.setBorderPainted(false);
                seat.setFocusPainted(false);

                if (bookingManager.isSeatTaken(movie.getMovieId(), showTime, name)) {
                    seat.setText("XX");
                    seat.setEnabled(false);
                    seat.setToolTipText("Seat " + name + " is already taken");
                } else {
                    seat.setToolTipText("Seat " + name + " - click to select");
                    seat.addActionListener(this);
                }
                seatButtons[r][c] = seat;
                seatGrid.add(seat);
            }
        }
        seatGrid.revalidate();
        seatGrid.repaint();

        movieTitleLabel.setText(movie.getTitle() + " - " + showTime);
        updateTicket();
    }
    // Makes the Ticket of the chosen type (with the price of the chosen movie)
    // and updates the price text, the ID box and the total
    private void updateTicket() {
        Movie movie = showing.get(movieBox.getSelectedIndex());
        ticket = bookingManager.getTicket(TICKET_TYPES[typeBox.getSelectedIndex()]);
        ticket.setPrice(movie.getPrice());   // each movie has its own price

        String priceText = "Price per ticket: " + Theme.peso(ticket.getPrice());
        if (ticket.getDiscountPercent() > 0) {
            priceText = priceText + "  (" + ticket.getDiscountPercent() + "% off the total)";
        }
        priceLabel.setText(priceText);

        // The ID box can only be typed in when the ticket type needs an ID
        idField.setEnabled(ticket.needsId());
        if (!ticket.needsId()) {
            idField.setText("");
        }
        showColors();
    }
    // Colors: taken = dark gray, selected = teal, free = light gray.
    // Also shows how many seats are selected and the total.
    private void showColors() {
        int count = 0;
        for (int r = 0; r < SEAT_ROWS; r++) {
            for (int c = 0; c < SEAT_COLS; c++) {
                JButton b = seatButtons[r][c];
                if (!b.isEnabled()) {
                    b.setBackground(new Color(170, 170, 170));
                } else if (chosen[r][c]) {
                    b.setBackground(Theme.ACCENT);
                    b.setForeground(Color.WHITE);
                    count++;
                } else {
                    b.setBackground(Theme.NEUTRAL);
                    b.setForeground(Theme.TEXT);
                }
            }
        }
        if (count == 0) {
            infoLabel.setText("No seats selected yet");
        } else {
            infoLabel.setText("Selected seats: " + count + "     Total: "
                    + Theme.peso(ticket.calculateTotal(count)));
        }
    }
    // The names of the selected seats, example: A1, A2
    private ArrayList<String> getChosenSeats() {
        ArrayList<String> seats = new ArrayList<String>();
        for (int r = 0; r < SEAT_ROWS; r++) {
            for (int c = 0; c < SEAT_COLS; c++) {
                if (chosen[r][c]) {
                    seats.add(ROW_NAMES.charAt(r) + "" + (c + 1));
                }
            }
        }
        return seats;
    }
    private static String joinSeats(ArrayList<String> seats, String separator) {
        String text = "";
        for (int i = 0; i < seats.size(); i++) {
            if (i > 0) {
                text = text + separator;
            }
            text = text + seats.get(i);
        }
        return text;
    }
}
