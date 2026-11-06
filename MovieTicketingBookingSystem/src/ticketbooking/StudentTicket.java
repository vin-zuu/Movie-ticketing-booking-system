package ticketbooking;

public class StudentTicket extends Ticket {
    public StudentTicket() {
        super("Student", 10);   // 10% off the total
    }
    @Override
    public boolean needsId() {
        return true;   // valid ID required
    }
}
