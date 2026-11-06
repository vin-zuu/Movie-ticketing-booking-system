package ticketbooking;

import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

// Asks for a password before something private or dangerous is done.
//   confirm()     : Admin password (delete a movie, cancel a booking,
//                   delete or reset an account)
//   confirmUser() : the password of the logged-in user (Staff opening
//                   Booking Records)
public class AdminAuth {

    private static final int MAX_TRIES = 3;
    // Returns true only if the Admin typed the right password
    public static boolean confirm(Component parent, AccountManager accountManager,
                                  UserAccount admin, String action) {
        if (admin == null || !admin.isAdmin()) {
            JOptionPane.showMessageDialog(parent,
                    "Only the Admin can do this.", "Not Allowed",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return askPassword(parent, accountManager, admin, action,
                "Admin Password Required", "the Admin password");
    }
    // Returns true only if the logged-in user typed his or her own password
    public static boolean confirmUser(Component parent, AccountManager accountManager,
                                      UserAccount user, String action) {
        if (user == null) {
            return false;
        }
        return askPassword(parent, accountManager, user, action,
                "Password Required", "your password");
    }
    // Shared by confirm() and confirmUser(): 3 tries
    private static boolean askPassword(Component parent,
                                       AccountManager accountManager,
                                       UserAccount user, String action,
                                       String windowTitle, String what) {
        for (int tries = 1; tries <= MAX_TRIES; tries++) {
            JPasswordField passwordField = new JPasswordField(18);

            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            panel.add(Theme.makeBoldLabel(action));
            panel.add(new JLabel("Enter " + what + " to continue:"));
            panel.add(passwordField);

            int choice = JOptionPane.showConfirmDialog(parent, panel,
                    windowTitle,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (choice != JOptionPane.OK_OPTION) {
                return false;   // cancelled
            }

            String typed = new String(passwordField.getPassword());

            if (accountManager.verifyPassword(user, typed)) {
                return true;
            }

            if (tries < MAX_TRIES) {
                JOptionPane.showMessageDialog(parent,
                        "Wrong password. Tries left: " + (MAX_TRIES - tries),
                        "Wrong Password", JOptionPane.WARNING_MESSAGE);
            }
        }

        JOptionPane.showMessageDialog(parent,
                "Too many wrong passwords. The action was cancelled.",
                "Cancelled", JOptionPane.ERROR_MESSAGE);
        return false;
    }
}
