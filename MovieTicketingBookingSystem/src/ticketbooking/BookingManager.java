package ticketbooking;

import java.util.ArrayList;

public class BookingManager {

    // Bookings are kept IN MEMORY only (nothing is saved to a file).
    // They stay while the program is running, even after logout.
    private ArrayList<Booking> bookingList;
    public BookingManager() {
        bookingList = new ArrayList<Booking>();
    }
    // Make the right Ticket object for a ticket type (null if unknown).
    // The result can be a RegularTicket, StudentTicket, SeniorTicket or
    // PWDTicket, but we hold it in a Ticket variable (polymorphism).
    public Ticket getTicket(String ticketType) {
        if (ticketType.equalsIgnoreCase("Regular")) {
            return new RegularTicket();
        } else if (ticketType.equalsIgnoreCase("Student")) {
            return new StudentTicket();
        } else if (ticketType.equalsIgnoreCase("Senior")) {
            return new SeniorTicket();
        } else if (ticketType.equalsIgnoreCase("PWD")) {
            return new PWDTicket();
        }
        return null;
    }
    // Generate a booking ID (uses the smallest ID that is free,
    // so IDs of cancelled bookings are used again)
    public String generateBookingId() {
        int nextNumber = 1;
        String bookingId = makeId(nextNumber);

        while (searchBooking(bookingId) != null) {
            nextNumber++;
            bookingId = makeId(nextNumber);
        }

        return bookingId;
    }
    // Turns 1 into B001, 25 into B025, 120 into B120
    private String makeId(int number) {
        if (number < 10) {
            return "B00" + number;
        } else if (number < 100) {
            return "B0" + number;
        } else {
            return "B" + number;
        }
    }
    // Add a booking
    public void addBooking(Booking booking) {
        bookingList.add(booking);
    }
    // Get all bookings
    public ArrayList<Booking> getBookingList() {
        return bookingList;
    }

    // ---------------- LINEAR SEARCH (DSA) ----------------
    // Linear search looks at the bookings one by one, from the first to the
    // last, and compares each one with what we are looking for.

    // Finds ONE booking by its ID. Returns the position (index) in the list,
    // or -1 if it is not there.
    public int linearSearchById(String bookingId) {
        if (bookingId == null) {
            return -1;
        }
        for (int i = 0; i < bookingList.size(); i++) {
            if (bookingList.get(i).getBookingId().equalsIgnoreCase(bookingId.trim())) {
                return i;   // found
            }
        }
        return -1;   // not found
    }
    public Booking searchBooking(String bookingId) {
        int index = linearSearchById(bookingId);
        if (index == -1) {
            return null;
        }
        return bookingList.get(index);
    }
    // Finds ALL bookings that match a field exactly (small or capital letters
    // are okay). searchBy is "Booking ID", "Customer Name" or "Movie Title".
    public ArrayList<Booking> linearSearch(String searchBy, String key) {
        ArrayList<Booking> results = new ArrayList<Booking>();
        String target = key.trim();

        // No early stop here: customers and movies can appear many times,
        // so the loop checks every booking to the end of the list.
        for (int i = 0; i < bookingList.size(); i++) {
            Booking booking = bookingList.get(i);
            String value;

            if (searchBy.equals("Booking ID")) {
                value = booking.getBookingId();
            } else if (searchBy.equals("Customer Name")) {
                value = booking.getCustomerName();
            } else {
                value = booking.getMovieTitle();
            }

            if (value.equalsIgnoreCase(target)) {
                results.add(booking);
            }
        }
        return results;
    }
    // Cancel a booking
    public boolean cancelBooking(String bookingId) {
        int index = linearSearchById(bookingId);
        if (index == -1) {
            return false;
        }
        bookingList.remove(index);
        return true;
    }

    // ---------------- BUBBLE SORT (DSA) ----------------
    // Bubble sort compares two bookings that sit next to each other and swaps
    // them if they are in the wrong order. After every pass, the biggest
    // (or smallest) booking "bubbles" to the end, so the list gets shorter.
    // sortBy: "Booking ID", "Customer Name", "Movie Title", "Quantity"
    //         or "Total Price"
    // The real list is NOT changed: a sorted copy is returned.
    public ArrayList<Booking> bubbleSort(String sortBy, boolean ascending) {
        ArrayList<Booking> sorted = new ArrayList<Booking>(bookingList);
        int n = sorted.size();

        for (int pass = 0; pass < n - 1; pass++) {
            boolean swapped = false;

            for (int j = 0; j < n - 1 - pass; j++) {
                int result = compareBookings(sorted.get(j), sorted.get(j + 1), sortBy);

                if (!ascending) {
                    result = -result;   // descending: flip the order
                }

                if (result > 0) {
                    // wrong order: swap the two neighbours
                    Booking temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                    swapped = true;
                }
            }

            if (!swapped) {
                break;   // no swap in this pass: the list is already sorted
            }
        }
        return sorted;
    }
    // Negative = first comes first, 0 = same, positive = second comes first
    private int compareBookings(Booking a, Booking b, String sortBy) {
        if (sortBy.equals("Customer Name")) {
            return a.getCustomerName().compareToIgnoreCase(b.getCustomerName());
        } else if (sortBy.equals("Movie Title")) {
            return a.getMovieTitle().compareToIgnoreCase(b.getMovieTitle());
        } else if (sortBy.equals("Quantity")) {
            return Integer.compare(a.getQuantity(), b.getQuantity());
        } else if (sortBy.equals("Total Price")) {
            return Double.compare(a.getTotalPrice(), b.getTotalPrice());
        }
        // Booking ID (B2 comes before B10, so the shorter ID is first)
        if (a.getBookingId().length() != b.getBookingId().length()) {
            return a.getBookingId().length() - b.getBookingId().length();
        }
        return a.getBookingId().compareToIgnoreCase(b.getBookingId());
    }
    // True if this seat (example: "A1") is already bought for one movie
    // at one showtime
    public boolean isSeatTaken(String movieId, String showTime, String seatName) {
        for (Booking booking : bookingList) {
            if (booking.getMovieId().equalsIgnoreCase(movieId)
                    && booking.getShowTime().equalsIgnoreCase(showTime)) {
                String[] seats = booking.getSeatArray();
                for (int i = 0; i < seats.length; i++) {
                    if (seats[i].trim().equalsIgnoreCase(seatName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    // Total tickets sold in all bookings
    public int getTotalTickets() {
        int total = 0;
        for (Booking booking : bookingList) {
            total = total + booking.getQuantity();
        }
        return total;
    }
    // Total sales of all bookings
    public double getTotalSales() {
        double total = 0;
        for (Booking booking : bookingList) {
            total = total + booking.getTotalPrice();
        }
        return total;
    }
}
