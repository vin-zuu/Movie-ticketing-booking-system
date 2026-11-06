package ticketbooking;

// One system user (Admin or Staff).
// Data hiding: every field is private.
public class UserAccount {

    public static final int MAX_ATTEMPTS = 3;

    private String username;
    private String role;
    private String password;
    // One-time code of an Admin (null = no code). It works only ONE time.
    private String oneTimeCode;

    // Lock information (3 wrong passwords = locked)
    private int failedAttempts;
    private boolean locked;
    private long lockedTime;   // when the account was locked (milliseconds)

    // Creates a brand new account
    public UserAccount(String username, String password, String role) {
        this.username = username;
        this.role = role;
        this.password = password;
        this.failedAttempts = 0;
        this.locked = false;
        this.lockedTime = 0;
    }
    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    // True if the typed password is the right one
    public boolean checkPassword(String typedPassword) {
        if (typedPassword == null) {
            return false;
        }
        return password.equals(typedPassword);
    }
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
    // ---------------- one-time code (Admin) ----------------
    public boolean hasOneTimeCode() {
        return oneTimeCode != null;
    }
    public String getOneTimeCode() {
        return oneTimeCode;
    }
    public void setOneTimeCode(String code) {
        this.oneTimeCode = code;
    }
    // True if the typed text is the one-time code of this account
    public boolean checkOneTimeCode(String typedCode) {
        if (oneTimeCode == null || typedCode == null) {
            return false;
        }
        return oneTimeCode.equals(typedCode.trim());
    }
    // True if this account is the Admin
    public boolean isAdmin() {
        return role.equalsIgnoreCase("Admin");
    }

    // ---------------- lock ----------------
    public int getFailedAttempts() {
        return failedAttempts;
    }
    public int getRemainingAttempts() {
        int remaining = MAX_ATTEMPTS - failedAttempts;
        if (remaining < 0) {
            remaining = 0;
        }
        return remaining;
    }
    public boolean isLocked() {
        return locked;
    }

    public long getLockedTime() {
        return lockedTime;
    }
    // Adds one wrong attempt. Returns true if this attempt locked the account.
    public boolean addFailedAttempt() {
        failedAttempts++;
        if (failedAttempts >= MAX_ATTEMPTS && !locked) {
            locked = true;
            lockedTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    public void resetAttempts() {
        failedAttempts = 0;
    }
    // Unlocks the account and gives back all 3 attempts
    public void unlock() {
        locked = false;
        failedAttempts = 0;
        lockedTime = 0;
    }
}
