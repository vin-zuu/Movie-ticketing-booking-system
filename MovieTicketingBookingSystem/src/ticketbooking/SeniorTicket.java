package ticketbooking;

public class SeniorTicket extends Ticket {
    public SeniorTicket() {
        super("Senior", 20);   // 20% off the total
    }
    @Override
    public boolean needsId() {
        return true;   // valid ID required
    }
}
