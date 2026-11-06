package ticketbooking;

public class PWDTicket extends Ticket {
    public PWDTicket() {
        super("PWD", 20);   // 20% off the total
    }
    @Override
    public boolean needsId() {
        return true;   // valid ID required
    }
}
