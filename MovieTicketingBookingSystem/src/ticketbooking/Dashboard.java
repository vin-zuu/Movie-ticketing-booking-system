package ticketbooking;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Dashboard extends JFrame implements ActionListener {

    private UserAccount loggedInUser;
    private AccountManager accountManager;
    private MovieManager movieManager;
    private BookingManager bookingManager;

    // Menu buttons (staff does not get the Movie Management and Sales Report buttons)
    private JButton movieButton;
    private JButton bookingButton;
    private JButton recordsButton;
    private JButton salesButton;
    private JButton securityButton;
    private JButton changePasswordButton;
    private JButton logoutButton;
    private JButton refreshButton;

    // Parts of the screen that change when the data changes
    private JPanel statsPanel;
    private JPanel nowShowingPanel;
    private JPanel comingSoonPanel;
    public Dashboard(UserAccount user, AccountManager accountManager,
                     MovieManager movieManager, BookingManager bookingManager) {
        this.loggedInUser = user;
        this.accountManager = accountManager;
        this.movieManager = movieManager;
        this.bookingManager = bookingManager;

        setTitle("Movie Theater Ticket Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);   // full screen
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BACKGROUND);

        // ---------- TOP BAR ----------
        JPanel header = Theme.createHeader(
                "MOVIE THEATER TICKET BOOKING SYSTEM",
                "Logged in as: " + user.getUsername()
                + "  |  Role: " + user.getRole().toUpperCase());

        // ---------- LEFT MENU ----------
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Theme.PANEL);
        sidebar.setPreferredSize(new Dimension(270, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.LINE),
                BorderFactory.createEmptyBorder(25, 20, 25, 20)));

        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 0, 12));
        menuPanel.setOpaque(false);
        menuPanel.add(Theme.makeLabel("MENU", 14, true, Theme.TITLE));

        // Admin and Staff see different menu buttons
        if (loggedInUser.isAdmin()) {
            movieButton = addMenuButton(menuPanel, "Movie Management", 'M',
                    "Add, update, delete and search movies (Alt+M)");
        } else {
            bookingButton = addMenuButton(menuPanel, "Ticket Booking", 'T',
                    "Book tickets for a customer (Alt+T)");
        }

        recordsButton = addMenuButton(menuPanel, "Booking Records", 'B',
                "See and search all bookings (Alt+B)");

        if (loggedInUser.isAdmin()) {
            salesButton = addMenuButton(menuPanel, "Sales Report", 'R',
                    "Total bookings, tickets and sales (Alt+R)");
            securityButton = addMenuButton(menuPanel, "Account Security", 'A',
                    "Unlock accounts, reset passwords, add or delete users (Alt+A)");
        }

        // Bottom of the menu: access note + change password + logout
        String accessNote;
        if (loggedInUser.isAdmin()) {
            accessNote = "Admin access: Movie Management, "
                    + "Booking Records, Sales Report and Account Security.";
        } else {
            accessNote = "Staff access: Ticket Booking and "
                    + "Booking Records (view only).";
        }

        logoutButton = new JButton("Logout");
        Theme.styleButton(logoutButton, Theme.ACCENT, Color.WHITE);
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        logoutButton.addActionListener(this);
        logoutButton.setMnemonic('L');
        logoutButton.setToolTipText("Log out of the system (Alt+L)");

        refreshButton = new JButton("Refresh");
        Theme.styleButton(refreshButton, Theme.NEUTRAL, Theme.TEXT);
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        refreshButton.addActionListener(this);
        refreshButton.setMnemonic('F');
        refreshButton.setToolTipText("Update the numbers and posters (Alt+F)");

        changePasswordButton = new JButton("Change Password");
        Theme.styleButton(changePasswordButton, Theme.NEUTRAL, Theme.TEXT);
        changePasswordButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        changePasswordButton.addActionListener(this);
        changePasswordButton.setMnemonic('P');
        changePasswordButton.setToolTipText("Change your own password (Alt+P)");

        JPanel bottomButtons = new JPanel(new GridLayout(3, 1, 0, 10));
        bottomButtons.setOpaque(false);
        bottomButtons.add(refreshButton);
        bottomButtons.add(changePasswordButton);
        bottomButtons.add(logoutButton);

        JPanel bottomMenu = new JPanel(new BorderLayout(0, 15));
        bottomMenu.setOpaque(false);
        bottomMenu.add(Theme.makeNote(accessNote, 13, Theme.TEXT),
                BorderLayout.NORTH);
        bottomMenu.add(bottomButtons, BorderLayout.SOUTH);

        sidebar.add(menuPanel, BorderLayout.NORTH);
        sidebar.add(bottomMenu, BorderLayout.SOUTH);

        // ---------- MAIN AREA: information cards + movie posters ----------
        int columns = 5;   // movie cards in one row

        statsPanel = new JPanel(new GridLayout(1, 0, 15, 0));
        statsPanel.setOpaque(false);

        nowShowingPanel = new JPanel(new GridLayout(0, columns, 15, 15));
        nowShowingPanel.setOpaque(false);

        comingSoonPanel = new JPanel(new GridLayout(0, columns, 15, 15));
        comingSoonPanel.setOpaque(false);

        JPanel nowShowingSection = new JPanel(new BorderLayout(0, 10));
        nowShowingSection.setOpaque(false);
        nowShowingSection.add(Theme.makeLabel("NOW SHOWING", 20, true, Theme.TITLE),
                BorderLayout.NORTH);
        nowShowingSection.add(nowShowingPanel, BorderLayout.CENTER);

        JPanel comingSoonSection = new JPanel(new BorderLayout(0, 10));
        comingSoonSection.setOpaque(false);
        comingSoonSection.add(Theme.makeLabel("COMING SOON", 20, true, Theme.TITLE),
                BorderLayout.NORTH);
        comingSoonSection.add(comingSoonPanel, BorderLayout.CENTER);

        JPanel lowerPart = new JPanel(new BorderLayout(0, 25));
        lowerPart.setOpaque(false);
        lowerPart.add(comingSoonSection, BorderLayout.NORTH);

        JPanel middlePart = new JPanel(new BorderLayout(0, 25));
        middlePart.setOpaque(false);
        middlePart.add(nowShowingSection, BorderLayout.NORTH);
        middlePart.add(lowerPart, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 25));
        content.setBackground(Theme.BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        content.add(statsPanel, BorderLayout.NORTH);
        content.add(middlePart, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND);

        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);

        refreshDashboard();
    }
    // Makes one menu button: styled, with an Alt+key shortcut, added to the menu
    private JButton addMenuButton(JPanel menuPanel, String text, char key, String tip) {
        JButton button = new JButton(text);
        button.setMnemonic(key);
        button.setToolTipText(tip);
        Theme.styleButton(button, Theme.PRIMARY, Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(this);
        menuPanel.add(button);
        return button;
    }
    // Fills the information cards and the movie posters with the latest data
    private void refreshDashboard() {
        statsPanel.removeAll();
        statsPanel.add(Theme.createStatCard("NOW SHOWING",
                "" + movieManager.countMovies("Now Showing")));
        statsPanel.add(Theme.createStatCard("COMING SOON",
                "" + movieManager.countMovies("Coming Soon")));
        statsPanel.add(Theme.createStatCard("TOTAL BOOKINGS",
                "" + bookingManager.getBookingList().size()));
        statsPanel.add(Theme.createStatCard("TICKETS SOLD",
                "" + bookingManager.getTotalTickets()));

        // Only the admin can see the sales
        if (loggedInUser.isAdmin()) {
            statsPanel.add(Theme.createStatCard("TOTAL SALES",
                    Theme.peso(bookingManager.getTotalSales())));

            int lockedCount = accountManager.countLockedAccounts();
            statsPanel.add(Theme.createStatCard("LOCKED ACCOUNTS",
                    "" + lockedCount));
            if (lockedCount > 0) {
                securityButton.setText("Account Security (" + lockedCount + ")");
            } else {
                securityButton.setText("Account Security");
            }
        }

        nowShowingPanel.removeAll();
        comingSoonPanel.removeAll();

        for (Movie movie : movieManager.getMovieList()) {
            JPanel card = createMovieCard(movie);

            if (movie.getStatus().equalsIgnoreCase("Now Showing")) {
                nowShowingPanel.add(card);
            } else {
                comingSoonPanel.add(card);
            }
        }

        if (nowShowingPanel.getComponentCount() == 0) {
            nowShowingPanel.add(Theme.makeLabel(
                    "No movies are showing right now.", 14, false, Theme.SOFT_TEXT));
        }
        if (comingSoonPanel.getComponentCount() == 0) {
            comingSoonPanel.add(Theme.makeLabel(
                    "No upcoming movies yet.", 14, false, Theme.SOFT_TEXT));
        }

        statsPanel.revalidate();
        nowShowingPanel.revalidate();
        comingSoonPanel.revalidate();
        statsPanel.repaint();
        nowShowingPanel.repaint();
        comingSoonPanel.repaint();
    }
    // One movie card: poster, title, movie information and ticket price
    private JPanel createMovieCard(Movie movie) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Theme.PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.LINE, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setPreferredSize(new Dimension(185, 340));

        JLabel poster = new JLabel();
        Theme.showPoster(poster, movie.getPosterFile(), 150, 225);

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 3));
        textPanel.setOpaque(false);

        JLabel titleLabel = Theme.makeLabel(movie.getTitle(), 14, true, Theme.TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel infoLabel = Theme.makeLabel(
                movie.getGenre() + "  |  " + movie.getDuration() + " min  |  "
                + movie.getRating(), 11, false, Theme.SOFT_TEXT);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Ticket price of the movie
        JLabel priceLabel = Theme.makeLabel(
                Theme.peso(movie.getPrice()), 13, true, Theme.PRIMARY);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        textPanel.add(titleLabel);
        textPanel.add(infoLabel);
        textPanel.add(priceLabel);

        card.add(poster, BorderLayout.CENTER);
        card.add(textPanel, BorderLayout.SOUTH);
        return card;
    }
    // Runs when a button is clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == movieButton) {
            MovieManagement.show(this, movieManager, accountManager, loggedInUser);
            refreshDashboard();

        } else if (e.getSource() == bookingButton) {
            TicketBooking.show(this, movieManager, bookingManager);
            refreshDashboard();

        } else if (e.getSource() == recordsButton) {
            // Staff must type their own password before opening the records
            if (!loggedInUser.isAdmin()
                    && !AdminAuth.confirmUser(this, accountManager,
                            loggedInUser, "Booking Records")) {
                return;
            }
            BookingRecords.show(this, bookingManager, accountManager, loggedInUser);
            refreshDashboard();

        } else if (e.getSource() == salesButton) {
            SalesReport.show(this, bookingManager);

        } else if (e.getSource() == securityButton) {
            AccountSecurity.show(this, accountManager, loggedInUser);
            refreshDashboard();

        } else if (e.getSource() == refreshButton) {
            refreshDashboard();

        } else if (e.getSource() == changePasswordButton) {
            changePassword();

        } else if (e.getSource() == logoutButton) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                LoginFrame loginFrame = new LoginFrame(
                        accountManager, movieManager, bookingManager);
                loginFrame.setVisible(true);
                dispose();
            }
        }
    }
    // Change the password of the logged-in user (Admin or Staff)
    private void changePassword() {
        JPasswordField oldField = new JPasswordField();
        JPasswordField newField = new JPasswordField();
        JPasswordField confirmField = new JPasswordField();

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Current password:"));
        form.add(oldField);
        form.add(new JLabel("New password (6+ characters, letter + number):"));
        form.add(newField);
        form.add(new JLabel("Confirm new password:"));
        form.add(confirmField);

        int choice = JOptionPane.showConfirmDialog(this, form,
                "Change Password - " + loggedInUser.getUsername(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String error = accountManager.changePassword(
                loggedInUser.getUsername(),
                new String(oldField.getPassword()),
                new String(newField.getPassword()),
                new String(confirmField.getPassword()));

        if (error != null) {
            JOptionPane.showMessageDialog(this, error,
                    "Change Password Failed", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Your password was changed successfully.");
        }
    }
}
