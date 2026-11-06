package ticketbooking;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginFrame extends JFrame implements ActionListener {

    private AccountManager accountManager;
    private MovieManager movieManager;
    private BookingManager bookingManager;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    public LoginFrame(AccountManager accountManager,
                      MovieManager movieManager,
                      BookingManager bookingManager) {
        this.accountManager = accountManager;
        this.movieManager = movieManager;
        this.bookingManager = bookingManager;

        setTitle("Movie Theater Ticket Booking System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // full screen
        setLayout(new GridLayout(1, 2));

        // ---------- LEFT SIDE: red curtain with movie posters ----------
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 120));
        leftPanel.setBackground(Theme.HEADER);

        JPanel leftContent = new JPanel(new BorderLayout(0, 25));
        leftContent.setOpaque(false);

        JLabel nowShowing = Theme.makeLabel("NOW SHOWING", 30, true, Color.WHITE);
        nowShowing.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel posterRow = new JPanel(new GridLayout(1, 3, 15, 0));
        posterRow.setOpaque(false);

        int posterCount = 0;
        for (Movie movie : movieManager.getMovieList()) {
            if (movie.getStatus().equalsIgnoreCase("Now Showing")
                    && posterCount < 3) {
                JLabel poster = new JLabel();
                Theme.showPoster(poster, movie.getPosterFile(), 190, 285);
                poster.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                posterRow.add(poster);
                posterCount++;
            }
        }

        // Title (from the first login code)
        JLabel movieTitle = Theme.makeLabel(
                "MOVIE THEATER", 32, true, Color.WHITE);
        movieTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel bookingTitle = Theme.makeLabel(
                "TICKET BOOKING SYSTEM", 24, true, Theme.NEUTRAL);
        bookingTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel tagline = new JPanel(new GridLayout(0, 1, 0, 3));
        tagline.setOpaque(false);
        tagline.add(movieTitle);
        tagline.add(bookingTitle);

        leftContent.add(nowShowing, BorderLayout.NORTH);
        leftContent.add(posterRow, BorderLayout.CENTER);
        leftContent.add(tagline, BorderLayout.SOUTH);
        leftPanel.add(leftContent);

        // ---------- RIGHT SIDE: login form ----------
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 120));
        rightPanel.setBackground(Theme.BACKGROUND);

        JPanel card = new JPanel(new GridLayout(0, 1, 8, 8));
        card.setBackground(Theme.PANEL);
        card.setPreferredSize(new Dimension(380, 460));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.LINE, 1),
                BorderFactory.createEmptyBorder(25, 35, 25, 35)));

        JLabel title = Theme.makeLabel("LOGIN", 26, true, Theme.TITLE);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        Theme.styleField(usernameField);
        Theme.styleField(passwordField);
        usernameField.setToolTipText("Type your username");
        passwordField.setToolTipText("Type your password");

        loginButton = new JButton("LOGIN");
        Theme.styleButton(loginButton, Theme.PRIMARY, Color.WHITE);
        loginButton.setMnemonic('L');
        loginButton.setToolTipText("Log in (Alt+L or the Enter key)");

        card.add(title);
        card.add(Theme.makeLabel("Username", 14, true, Theme.TEXT));
        card.add(usernameField);
        card.add(Theme.makeLabel("Password", 14, true, Theme.TEXT));
        card.add(passwordField);
        signUpButton = new JButton("SIGN UP (NEW STAFF)");
        Theme.styleButton(signUpButton, Theme.NEUTRAL, Theme.TEXT);
        signUpButton.setMnemonic('S');
        signUpButton.setToolTipText("Create a new Staff account (Alt+S)");


        card.add(loginButton);
        card.add(signUpButton);

        rightPanel.add(card);

        add(leftPanel);
        add(rightPanel);

        loginButton.addActionListener(this);
        signUpButton.addActionListener(this);
        getRootPane().setDefaultButton(loginButton);   // Enter key = login
    }
    // Runs when a button is clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            login();
        } else if (e.getSource() == signUpButton) {
            signUp();
        }
    }
    // Sign up form: creates a new STAFF account
    private void signUp() {
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();
        userField.setToolTipText("4-15 letters, numbers or _");
        passField.setToolTipText("At least 6 characters with a letter and a number");
        confirmField.setToolTipText("Type the same password again");

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(Theme.makeBoldLabel("Create a Staff account"));
        form.add(new JLabel("Username (4-15 letters, numbers or _):"));
        form.add(userField);
        form.add(new JLabel("Password (6+ characters, letter + number):"));
        form.add(passField);
        form.add(new JLabel("Confirm password:"));
        form.add(confirmField);

        int choice = JOptionPane.showConfirmDialog(this, form,
                "Sign Up", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String error = accountManager.signUp(
                userField.getText().trim(),
                new String(passField.getPassword()),
                new String(confirmField.getPassword()));

        if (error != null) {
            JOptionPane.showMessageDialog(this, error,
                    "Sign Up Failed", JOptionPane.WARNING_MESSAGE);
        } else {
            usernameField.setText(userField.getText().trim());
            passwordField.setText("");
            JOptionPane.showMessageDialog(this,
                    "Account created! You can now log in as Staff.");
        }
    }
    // Asks the locked Admin for the one-time code
    private void askOneTimeCode(UserAccount account) {
        if (!account.hasOneTimeCode()) {
            JOptionPane.showMessageDialog(this,
                    "Account is blocked.\nThere is no one-time code for this account.\n"
                    + "Ask another Admin to unlock it (Account Security).",
                    "Account Locked", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // The one-time code is already typed in the box
        Object answer = JOptionPane.showInputDialog(this,
                "Account is blocked.\nEnter a one-time code to unlock it.",
                "Account Blocked", JOptionPane.ERROR_MESSAGE,
                null, null, account.getOneTimeCode());

        if (answer == null) {
            return;   // cancelled
        }
        String code = answer.toString();

        if (accountManager.unlockWithOneTimeCode(account.getUsername(), code)) {
            JOptionPane.showMessageDialog(this,
                    "The account is unlocked. You have 3 new attempts.\n"
                    + "The one-time code is now used. You can make a new one\n"
                    + "in Account Security (New One-Time Code).",
                    "Account Unlocked", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Wrong one-time code. The account is still locked.",
                    "Wrong Code", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter your username and password.");
            return;
        }

        // Remember if the account was already locked BEFORE this try
        UserAccount account = accountManager.findUser(username);
        boolean wasLocked = false;
        if (account != null) {
            wasLocked = account.isLocked();
        }

        UserAccount user = accountManager.login(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(
                    this, "Welcome, " + user.getUsername()
                    + " (" + user.getRole() + ")!");

            Dashboard dashboard = new Dashboard(
                    user, accountManager, movieManager, bookingManager);
            dashboard.setVisible(true);
            dispose();

        } else if (account == null) {
            JOptionPane.showMessageDialog(
                    this, "Invalid username or password.");

        } else if (account.isLocked() && account.isAdmin()) {
            // An Admin unlocks his or her own account with the one-time code
            askOneTimeCode(account);

        } else if (account.isLocked()) {
            // What the user must do to get the account back
            String help = "Ask an Admin to unlock it (Account Security).";

            if (wasLocked) {
                // It was locked before this try
                JOptionPane.showMessageDialog(this,
                        "This account is locked after 3 wrong passwords.\n" + help,
                        "Account Locked", JOptionPane.ERROR_MESSAGE);
            } else {
                // This was the 3rd wrong password: the account is blocked,
                // but the program stays open
                JOptionPane.showMessageDialog(this,
                        "3 wrong passwords. The account \"" + account.getUsername()
                        + "\" is now BLOCKED.\n" + help,
                        "Account Blocked", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this,
                    "Wrong password. Attempts left: "
                    + account.getRemainingAttempts() + " of "
                    + UserAccount.MAX_ATTEMPTS + ".",
                    "Login Failed", JOptionPane.WARNING_MESSAGE);
        }
    }
}
