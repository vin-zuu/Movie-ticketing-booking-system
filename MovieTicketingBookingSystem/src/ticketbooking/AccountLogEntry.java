package ticketbooking;

// One line of the security log (example: "staff - ACCOUNT LOCKED")
public class AccountLogEntry {

    private long time;
    private String username;
    private String event;
    private String details;
    public AccountLogEntry(String username, String event, String details) {
        this(System.currentTimeMillis(), username, event, details);
    }
    // Used when the time is already known
    public AccountLogEntry(long time, String username,
                           String event, String details) {
        this.time = time;
        this.username = username;
        this.event = event;
        this.details = details;
    }
    public long getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }

    public String getEvent() {
        return event;
    }

    public String getDetails() {
        return details;
    }
}
