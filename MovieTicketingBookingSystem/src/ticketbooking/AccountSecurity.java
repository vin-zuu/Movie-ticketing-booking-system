package ticketbooking;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// Account Security (Admin only) in simple JOptionPane boxes:
//  - see the accounts and which ones are locked (3 wrong passwords)
//  - unlock an account / reset the password of a Staff
//  - add a user (Admin or Staff) / delete a user (needs the Admin password)
//  - read the security log
public class AccountSecurity {

    // Columns of the two tables (JTable) and how wide each column is
    private static final String[] ACCOUNT_COLUMNS = {
        "Username", "Role", "Status", "Wrong Attempts", "Locked Since"
    };
    private static final int[] ACCOUNT_WIDTHS = {150, 80, 80, 120, 200};

    private static final String[] LOG_COLUMNS = {
        "Date / Time", "Account", "Event", "Details"
    };
    private static final int[] LOG_WIDTHS = {170, 120, 150, 400};

    public static void show(Component parent, AccountManager accountManager,
                            UserAccount user) {
        // The account list is the main screen. All the buttons are under the
        // list, so the Admin can unlock, reset, add or delete right away.
        String[] buttons = {"View Accounts", "Unlock Account", "Reset Password",
                            "Add User", "Delete User", "Security Log",
                            "New One-Time Code", "Close"};

        boolean showLog = false;

        while (true) {
            int choice;
            if (showLog) {
                choice = Popup.showTableWithButtons(parent,
                        "Account Security - Security Log (newest first)",
                        LOG_COLUMNS, logRows(accountManager), LOG_WIDTHS,
                        logFooter(accountManager), buttons);
            } else {
                choice = Popup.showTableWithButtons(parent,
                        "Account Security - Accounts",
                        ACCOUNT_COLUMNS, accountRows(accountManager), ACCOUNT_WIDTHS,
                        "Users: " + accountManager.getUserList().size()
                        + "   |   Locked accounts: " + accountManager.countLockedAccounts(),
                        buttons);
            }

            if (choice == -1 || choice == buttons.length - 1) {   // Close is the last button
                return;
            }

            // After every action the screen goes back to the account list,
            // except when the Security Log button was clicked.
            showLog = false;

            // The number of the button: 0 View Accounts, 1 Unlock Account,
            // 2 Reset Password, 3 Add User, 4 Delete User, 5 Security Log,
            // 6 New One-Time Code
            if (choice == 1) {
                unlock(parent, accountManager, user);
            } else if (choice == 2) {
                resetPassword(parent, accountManager, user);
            } else if (choice == 3) {
                addUser(parent, accountManager, user);
            } else if (choice == 4) {
                deleteUser(parent, accountManager, user);
            } else if (choice == 5) {
                showLog = true;
            } else if (choice == 6) {
                newOneTimeCode(parent, accountManager, user);
            }
        }
    }
    // The account rows for the JTable: one row for every account
    private static Object[][] accountRows(AccountManager accountManager) {
        ArrayList<UserAccount> users = accountManager.getUserList();
        Object[][] rows = new Object[users.size()][ACCOUNT_COLUMNS.length];

        for (int i = 0; i < users.size(); i++) {
            UserAccount u = users.get(i);
            String status = "Active";
            String since = "-";
            if (u.isLocked()) {
                status = "LOCKED";
                since = Theme.dateTime(u.getLockedTime());
            }
            rows[i][0] = u.getUsername();
            rows[i][1] = u.getRole();
            rows[i][2] = status;
            rows[i][3] = u.getFailedAttempts() + " / " + UserAccount.MAX_ATTEMPTS;
            rows[i][4] = since;
        }
        return rows;
    }
    // Drop-down list of all accounts. Returns the username (or null).
    private static String pickUsername(Component parent, AccountManager accountManager,
                                       String title, String message) {
        ArrayList<UserAccount> users = accountManager.getUserList();
        String[] items = new String[users.size()];

        for (int i = 0; i < users.size(); i++) {
            UserAccount u = users.get(i);
            items[i] = u.getUsername() + " (" + u.getRole() + ")";
            if (u.isLocked()) {
                items[i] = items[i] + " - LOCKED";
            }
        }

        int index = Popup.pick(parent, title, message, items);
        if (index == -1) {
            return null;
        }
        return users.get(index).getUsername();
    }
    // The Admin makes a new one-time code (the old code stops working)
    private static void newOneTimeCode(Component parent, AccountManager accountManager,
                                       UserAccount admin) {
        if (!AdminAuth.confirm(parent, accountManager, admin,
                "Make a new one-time code")) {
            return;
        }

        String code = accountManager.makeNewOneTimeCode(admin);
        Popup.info(parent, "Your new one-time code is:\n\n" + code
                + "\n\nWrite it down and keep it safe.\n"
                + "It works only ONE time. The old code does not work anymore.");
    }
    private static void unlock(Component parent, AccountManager accountManager,
                               UserAccount admin) {
        String username = pickUsername(parent, accountManager,
                "Unlock Account", "Select the account to unlock:");
        if (username == null) {
            return;
        }

        if (!accountManager.isLocked(username)) {
            Popup.info(parent, username + " is not locked.");
            return;
        }

        if (Popup.yes(parent, "Confirm Unlock",
                "Unlock the account \"" + username + "\"?")) {
            accountManager.unlockAccount(username, admin.getUsername());
            Popup.info(parent, username + " is unlocked. They have 3 new attempts.");
        }
    }
    // The Admin sets a new password for a Staff who forgot it
    private static void resetPassword(Component parent, AccountManager accountManager,
                                      UserAccount admin) {
        String username = pickUsername(parent, accountManager,
                "Reset Password", "Select the Staff account:");
        if (username == null) {
            return;
        }

        JPasswordField newField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(Theme.makeBoldLabel("Reset password of " + username));
        form.add(new JLabel("New password (6+ characters, letter + number):"));
        form.add(newField);
        form.add(new JLabel("Confirm new password:"));
        form.add(confirmField);

        int choice = JOptionPane.showConfirmDialog(parent, form, "Reset Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        if (!AdminAuth.confirm(parent, accountManager, admin,
                "Reset the password of " + username)) {
            return;
        }

        String error = accountManager.adminResetPassword(username,
                new String(newField.getPassword()),
                new String(confirmField.getPassword()),
                admin.getUsername());

        if (error != null) {
            Popup.warn(parent, "Reset Failed", error);
        } else {
            Popup.info(parent, "The password of " + username + " was changed.\n"
                    + "The account is unlocked.");
        }
    }
    // Adds a new system user and chooses the level of access
    private static void addUser(Component parent, AccountManager accountManager,
                                UserAccount admin) {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<String>(new String[]{"Staff", "Admin"});
        userField.setToolTipText("4-15 letters, numbers or _");
        passField.setToolTipText("At least 6 characters with a letter and a number");
        roleBox.setToolTipText("Staff sells tickets, Admin manages the system");

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Username (4-15 letters, numbers or _):"));
        form.add(userField);
        form.add(new JLabel("Password (6+ characters, letter + number):"));
        form.add(passField);
        form.add(new JLabel("Confirm password:"));
        form.add(confirmField);
        form.add(new JLabel("Level of access:"));
        form.add(roleBox);

        int choice = JOptionPane.showConfirmDialog(parent, form, "Add User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String error = accountManager.addUser(
                userField.getText().trim(),
                new String(passField.getPassword()),
                new String(confirmField.getPassword()),
                (String) roleBox.getSelectedItem(),
                admin.getUsername());

        if (error != null) {
            Popup.warn(parent, "Add User Failed", error);
        } else {
            Popup.info(parent, "User added successfully!");
        }
    }
    // Deleting a user needs the Admin password
    private static void deleteUser(Component parent, AccountManager accountManager,
                                   UserAccount admin) {
        String username = pickUsername(parent, accountManager,
                "Delete User", "Select the account to delete:");
        if (username == null) {
            return;
        }

        if (!Popup.yes(parent, "Confirm Delete",
                "Delete the account \"" + username + "\"?")) {
            return;
        }

        if (!AdminAuth.confirm(parent, accountManager, admin, "Delete user " + username)) {
            return;
        }

        String error = accountManager.deleteUser(username, admin.getUsername());

        if (error != null) {
            Popup.warn(parent, "Delete Failed", error);
        } else {
            Popup.info(parent, "User deleted.");
        }
    }
    // The security log rows for the JTable, newest first
    private static Object[][] logRows(AccountManager accountManager) {
        ArrayList<AccountLogEntry> log = accountManager.getSecurityLog();
        Object[][] rows = new Object[log.size()][LOG_COLUMNS.length];

        int row = 0;
        for (int i = log.size() - 1; i >= 0; i--) {
            AccountLogEntry entry = log.get(i);
            rows[row][0] = Theme.dateTime(entry.getTime());
            rows[row][1] = entry.getUsername();
            rows[row][2] = entry.getEvent();
            rows[row][3] = entry.getDetails();
            row++;
        }
        return rows;
    }
    // The note under the security log
    private static String logFooter(AccountManager accountManager) {
        int count = accountManager.getSecurityLog().size();
        if (count == 0) {
            return "(No security events yet)";
        }
        return "Security events: " + count;
    }
}
