package ticketbooking;

public class Booking {

    private String bookingId;
    private String movieId;
    private String customerName;
    private String movieTitle;
    private String showTime;
    private String ticketType;
    private int quantity;
    private double totalPrice;
    private String seats;   // example: A1,A2,A3 (empty for old bookings)
    private String paymentMode;   // "Cash" or "Cashless" ("-" for old bookings)
    public Booking(String bookingId, String movieId, String customerName,
                   String movieTitle, String showTime,
                   String ticketType, int quantity,
                   double totalPrice, String seats,
                   String paymentMode) {
        this.bookingId = bookingId;
        this.movieId = movieId;
        this.customerName = customerName;
        this.movieTitle = movieTitle;
        this.showTime = showTime;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        if (seats == null) {
            this.seats = "";
        } else {
            this.seats = seats;
        }
        if (paymentMode == null || paymentMode.trim().isEmpty()) {
            this.paymentMode = "-";
        } else {
            this.paymentMode = paymentMode;
        }
    }
    public String getBookingId() {
        return bookingId;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getShowTime() {
        return showTime;
    }

    public String getTicketType() {
        return ticketType;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getSeats() {
        return seats;
    }

    public String getPaymentMode() {
        return paymentMode;
    }
    // Seats as a list, example: ["A1", "A2"]
    public String[] getSeatArray() {
        if (seats.trim().isEmpty()) {
            return new String[0];
        }
        return seats.split(",");
    }
}
