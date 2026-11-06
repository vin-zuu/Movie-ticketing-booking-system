package ticketbooking;

// Parent class of all the ticket types (Regular, Student, Senior, PWD).
// Each child class only says its own name and discount.
public class Ticket {

    // Price of ONE ticket (before any discount). Each movie has its own
    // price (250 to 350); the booking window sets it with setPrice().
    public static final double DEFAULT_PRICE = 250;
    protected double price = DEFAULT_PRICE;

    // protected: the child classes can use these
    protected String typeName;
    protected int discountPercent;   // 10 = 10% off the TOTAL
    // Overloaded constructors
    public Ticket() {
        this("Regular", 0);
    }
    public Ticket(String typeName, int discountPercent) {
        this.typeName = typeName;
        this.discountPercent = discountPercent;
    }
    public String getTypeName() {
        return typeName;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    // Total price = (price x quantity) minus the discount on the TOTAL
    // Example: 4 Senior tickets = 1000 - 20% = 800
    public double calculateTotal(int quantity) {
        double subtotal = price * quantity;
        double discount = subtotal * discountPercent / 100;
        return subtotal - discount;
    }
    // Does the customer need to type an ID number? (children override this)
    public boolean needsId() {
        return false;
    }
}
