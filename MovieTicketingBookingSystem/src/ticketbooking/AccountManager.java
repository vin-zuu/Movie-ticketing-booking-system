package ticketbooking;

import java.util.ArrayList;
import java.util.Random;

// Keeps all accounts and the security log IN MEMORY only.
// Nothing is saved to a file: the data stays while the program is running
// (even after logout) and is gone when the program is closed.
// All account rules are here.
public class AccountManager {

    private ArrayList<UserAccount> userList;
    private ArrayList<AccountLogEntry> securityLog;
    public AccountManager() {
        userList = new ArrayList<UserAccount>();
        securityLog = new ArrayList<AccountLogEntry>();

        // The default accounts are created every time the program starts
        UserAccount defaultAdmin = new UserAccount("vincent", "admin123", "Admin");
        defaultAdmin.setOneTimeCode("resetadminaccount");   // the Admin's first one-time code
        userList.add(defaultAdmin);
        userList.add(new UserAccount("staff", "staff123", "Staff"));
    }

    // ---------------- finding accounts ----------------
    // LINEAR SEARCH (DSA): look at the accounts one by one, from the first
    // to the last, until the username matches. Returns the position (index)
    // in the list, or -1 if there is no such account.
    public int linearSearchUser(String username) {
        if (username == null) {
            return -1;
        }
        String target = username.trim();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUsername().equalsIgnoreCase(target)) {
                return i;   // found
            }
        }
        return -1;   // not found
    }
    // Find an account by username (uses the linear search above)
    public UserAccount findUser(String username) {
        int index = linearSearchUser(username);
        if (index == -1) {
            return null;
        }
        return userList.get(index);
    }
    public ArrayList<UserAccount> getUserList() {
        return userList;
    }
    private int countAdmins() {
        int count = 0;
        for (UserAccount user : userList) {
            if (user.isAdmin()) {
                count++;
            }
        }
        return count;
    }

    // ---------------- rules for usernames and passwords ----------------
    // Returns null if OK, or the problem
    public String validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Please enter a username.";
        }
        String name = username.trim();
        if (name.length() < 4 || name.length() > 15) {
            return "Username must be 4 to 15 characters.";
        }
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            boolean allowed = false;
            if (ch >= 'a' && ch <= 'z') {
                allowed = true;
            } else if (ch >= 'A' && ch <= 'Z') {
                allowed = true;
            } else if (ch >= '0' && ch <= '9') {
                allowed = true;
            } else if (ch == '_') {
                allowed = true;
            }
            if (!allowed) {
                return "Username can only use letters, numbers and underscore (_).";
            }
        }
        if (findUser(name) != null) {
            return "That username is already taken.";
        }
        return null;
    }
    // Returns null if OK, or the problem
    public String validatePassword(String password) {
        if (password == null || password.length() < 6) {
            return "Password must be at least 6 characters.";
        }
        boolean hasLetter = false;
        boolean hasNumber = false;
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                hasLetter = true;
            }
            if (ch >= '0' && ch <= '9') {
                hasNumber = true;
            }
        }
        if (!hasLetter || !hasNumber) {
            return "Password must have at least one letter and one number.";
        }
        if (password.contains(" ")) {
            return "Password cannot have spaces.";
        }
        return null;
    }

    // ---------------- sign up / add / delete ----------------
    // Checks the username and the two passwords (used by Sign Up and Add User).
    // Returns null if OK, or the problem.
    private String checkNewAccount(String username, String password, String confirm) {
        String error = validateUsername(username);
        if (error != null) {
            return error;
        }
        error = validatePassword(password);
        if (error != null) {
            return error;
        }
        if (!password.equals(confirm)) {
            return "The two passwords do not match.";
        }
        return null;
    }
    // Sign up form: creates a STAFF account.
    // Returns null if OK, or the problem.
    public String signUp(String username, String password, String confirm) {
        String error = checkNewAccount(username, password, confirm);
        if (error != null) {
            return error;
        }

        userList.add(new UserAccount(username.trim(), password, "Staff"));
        addLog(username.trim(), "ACCOUNT CREATED", "Sign up (Staff)");
        return null;
    }
    // Admin adds a new system user and chooses the level of access.
    // Returns null if OK, or the problem.
    public String addUser(String username, String password, String confirm,
                          String role, String addedBy) {
        if (!"Admin".equalsIgnoreCase(role) && !"Staff".equalsIgnoreCase(role)) {
            return "Please choose a role (Admin or Staff).";
        }
        String error = checkNewAccount(username, password, confirm);
        if (error != null) {
            return error;
        }

        String cleanRole = "Staff";
        if ("Admin".equalsIgnoreCase(role)) {
            cleanRole = "Admin";
        }
        userList.add(new UserAccount(username.trim(), password, cleanRole));
        addLog(username.trim(), "ACCOUNT CREATED",
                cleanRole + " added by " + addedBy);
        return null;
    }
    // Admin deletes an account. Returns null if OK, or the problem.
    public String deleteUser(String username, String deletedBy) {
        UserAccount user = findUser(username);

        if (user == null) {
            return "Account not found.";
        }
        if (user.getUsername().equalsIgnoreCase(deletedBy)) {
            return "You cannot delete the account you are using.";
        }
        if (user.isAdmin() && countAdmins() <= 1) {
            return "You cannot delete the last Admin account.";
        }

        userList.remove(user);
        addLog(user.getUsername(), "ACCOUNT DELETED", "Deleted by " + deletedBy);
        return null;
    }

    // ---------------- login and lock ----------------
    // Verify login credentials.
    // Admin AND Staff are locked after 3 wrong passwords in a row.
    public UserAccount login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        UserAccount user = findUser(username);

        if (user == null) {
            return null;
        }

        // A locked account cannot log in, even with the right password
        if (user.isLocked()) {
            return null;
        }

        if (user.checkPassword(password)) {
            if (user.getFailedAttempts() > 0) {
                user.resetAttempts();
            }
            return user;
        }

        boolean justLocked = user.addFailedAttempt();
        if (justLocked) {
            addLog(user.getUsername(), "ACCOUNT LOCKED",
                    "3 wrong passwords (" + user.getRole() + ")");
        }

        return null;
    }
    // True if the password is the right password of this (logged-in) account.
    // Used before deleting something (Admin password check).
    public boolean verifyPassword(UserAccount user, String password) {
        if (user == null) {
            return false;
        }
        return user.checkPassword(password);
    }
    public boolean isLocked(String username) {
        UserAccount user = findUser(username);
        if (user == null) {
            return false;
        }
        return user.isLocked();
    }
    // How many accounts are locked right now
    public int countLockedAccounts() {
        int count = 0;
        for (UserAccount user : userList) {
            if (user.isLocked()) {
                count++;
            }
        }
        return count;
    }
    // Admin unlocks an account that was locked by 3 wrong passwords
    public boolean unlockAccount(String username, String unlockedBy) {
        UserAccount user = findUser(username);

        if (user == null || !user.isLocked()) {
            return false;
        }

        user.unlock();
        addLog(user.getUsername(), "ACCOUNT UNLOCKED",
                "Unlocked by " + unlockedBy);
        return true;
    }

    // ---------------- one-time code (Admin) ----------------
    // An Admin who is locked after 3 wrong passwords can unlock his or her
    // own account with the one-time code. The code works only ONE time:
    // after it is used it is deleted. A new one is made in Account Security.
    public boolean unlockWithOneTimeCode(String username, String code) {
        UserAccount user = findUser(username);

        if (user == null || !user.isAdmin() || !user.isLocked()) {
            return false;
        }

        if (!user.checkOneTimeCode(code)) {
            addLog(user.getUsername(), "ONE-TIME CODE FAILED",
                    "Wrong one-time code typed");
            return false;
        }

        user.setOneTimeCode(null);   // used: it cannot be used again
        user.unlock();
        addLog(user.getUsername(), "ACCOUNT UNLOCKED",
                "Unlocked with the one-time code");
        return true;
    }
    // Makes a new one-time code for this Admin (the old code stops working).
    // Returns the new code so it can be shown once.
    public String makeNewOneTimeCode(UserAccount admin) {
        String letters = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        String code = "reset-";
        for (int i = 0; i < 8; i++) {
            code = code + letters.charAt(random.nextInt(letters.length()));
        }

        admin.setOneTimeCode(code);
        addLog(admin.getUsername(), "NEW ONE-TIME CODE", "A new code was made");
        return code;
    }

    // ---------------- change password (user is logged in) ----------------
    // Returns null if OK, or the problem.
    public String changePassword(String username, String oldPassword,
                                 String newPassword, String confirm) {
        UserAccount user = findUser(username);

        if (user == null) {
            return "Account not found.";
        }
        if (!user.checkPassword(oldPassword)) {
            return "Your current password is wrong.";
        }
        String error = validatePassword(newPassword);
        if (error != null) {
            return error;
        }
        if (!newPassword.equals(confirm)) {
            return "The two new passwords do not match.";
        }
        if (newPassword.equals(oldPassword)) {
            return "The new password must be different from the old one.";
        }

        user.setPassword(newPassword);
        addLog(user.getUsername(), "PASSWORD CHANGED", "Changed by the user");
        return null;
    }

    // ---------------- reset passwords ----------------
    // An Admin sets a new password for a STAFF account.
    // The account is unlocked too. Returns null if OK, or the problem.
    public String adminResetPassword(String username, String newPassword,
                                     String confirm, String resetBy) {
        UserAccount user = findUser(username);

        if (user == null) {
            return "Account not found.";
        }
        if (user.isAdmin()) {
            return "Only Staff passwords can be reset here.";
        }

        String error = validatePassword(newPassword);
        if (error != null) {
            return error;
        }
        if (!newPassword.equals(confirm)) {
            return "The two passwords do not match.";
        }

        user.setPassword(newPassword);
        user.unlock();
        addLog(user.getUsername(), "PASSWORD RESET", "Reset by " + resetBy);
        return null;
    }

    // ---------------- security log ----------------
    public ArrayList<AccountLogEntry> getSecurityLog() {
        return securityLog;
    }
    private void addLog(String username, String event, String details) {
        securityLog.add(new AccountLogEntry(username, event, details));
    }
}
