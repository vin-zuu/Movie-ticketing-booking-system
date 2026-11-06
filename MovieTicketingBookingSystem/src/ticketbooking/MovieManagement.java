package ticketbooking;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Movie Management (Admin only) in simple JOptionPane boxes.
// View / Add / Update / Delete / Search (LINEAR SEARCH).
public class MovieManagement {

    // Columns of the movie table (JTable) and how wide each one is
    private static final String[] COLUMNS = {
        "ID", "Title", "Genre", "Duration", "Price", "Rating", "Status"
    };
    private static final int[] COLUMN_WIDTHS = {60, 280, 100, 90, 90, 70, 110};

    public static void show(Component parent, MovieManager movieManager,
                            AccountManager accountManager, UserAccount user) {
        // The movie list is the main screen. All the buttons are under the
        // list, so the Admin can add, update, delete or search right away.
        String[] buttons = {"Show All", "Add Movie", "Update Movie",
                            "Delete Movie", "Search Movie", "Close"};

        ArrayList<Movie> shown = movieManager.getMovieList();
        String heading = "All Movies";

        while (true) {
            int choice = Popup.showTableWithButtons(parent,
                    "Movie Management - " + heading,
                    COLUMNS, tableRows(shown), COLUMN_WIDTHS,
                    "Total movies shown: " + shown.size(), buttons);

            if (choice == -1 || choice == buttons.length - 1) {   // Close is the last button
                return;
            }

            // After every action the screen goes back to the full list,
            // except when a search found something.
            shown = movieManager.getMovieList();
            heading = "All Movies";

            // The number of the button: 0 Show All, 1 Add Movie, 2 Update Movie,
            // 3 Delete Movie, 4 Search Movie
            if (choice == 1) {
                addMovie(parent, movieManager);
            } else if (choice == 2) {
                updateMovie(parent, movieManager);
            } else if (choice == 3) {
                deleteMovie(parent, movieManager, accountManager, user);
            } else if (choice == 4) {
                ArrayList<Movie> results = searchMovie(parent, movieManager);
                if (results != null) {
                    shown = results;
                    heading = "Search Result";
                }
            }
        }
    }
    private static void addMovie(Component parent, MovieManager movieManager) {
        Movie movie = askMovie(parent, movieManager, null);
        if (movie == null) {
            return;   // cancelled
        }
        movieManager.addMovie(movie);
        Popup.info(parent, "Movie added successfully!\nMovie ID: " + movie.getMovieId());
    }
    private static void updateMovie(Component parent, MovieManager movieManager) {
        Movie movie = pickMovie(parent, movieManager, "Update Movie",
                "Select the movie to update:");
        if (movie == null) {
            return;
        }

        Movie changed = askMovie(parent, movieManager, movie);
        if (changed == null) {
            return;   // cancelled
        }

        movie.setTitle(changed.getTitle());
        movie.setGenre(changed.getGenre());
        movie.setDuration(changed.getDuration());
        movie.setRating(changed.getRating());
        movie.setStatus(changed.getStatus());
        movie.setPosterFile(changed.getPosterFile());
        movie.setPrice(changed.getPrice());
        Popup.info(parent, "Movie updated successfully!");
    }
    private static void deleteMovie(Component parent, MovieManager movieManager,
                                    AccountManager accountManager, UserAccount user) {
        Movie movie = pickMovie(parent, movieManager, "Delete Movie",
                "Select the movie to delete:");
        if (movie == null) {
            return;
        }

        // Deleting needs the Admin password
        if (Popup.yes(parent, "Confirm Deletion",
                "Are you sure you want to delete \"" + movie.getTitle() + "\"?")
                && AdminAuth.confirm(parent, accountManager, user,
                        "Delete movie " + movie.getMovieId())) {
            if (movieManager.deleteMovie(movie.getMovieId())) {
                Popup.info(parent, "Movie deleted successfully!");
            }
        }
    }
    // Linear search: the exact movie ID or the exact title.
    // Returns the movies found, or null if nothing was found / cancelled.
    private static ArrayList<Movie> searchMovie(Component parent, MovieManager movieManager) {
        String key = Popup.ask(parent, "Search Movie",
                "Type the exact movie ID or title:");
        if (key == null) {
            return null;
        }
        if (key.isEmpty()) {
            Popup.info(parent, "Enter a movie ID or title to search.");
            return null;
        }

        ArrayList<Movie> results = movieManager.searchMovies(key);
        if (results.isEmpty()) {
            Popup.info(parent, "Movie not found.");
            return null;
        }
        return results;
    }
    // Drop-down list of all movies. Returns the chosen movie (or null).
    private static Movie pickMovie(Component parent, MovieManager movieManager,
                                   String title, String message) {
        ArrayList<Movie> movies = movieManager.getMovieList();
        if (movies.isEmpty()) {
            Popup.info(parent, "There are no movies yet.");
            return null;
        }

        String[] items = new String[movies.size()];
        for (int i = 0; i < movies.size(); i++) {
            items[i] = movies.get(i).getMovieId() + " - " + movies.get(i).getTitle();
        }

        int index = Popup.pick(parent, title, message, items);
        if (index == -1) {
            return null;
        }
        return movies.get(index);
    }
    // Browse button: opens a file window and puts the chosen picture in the poster box
    private static class BrowseListener implements ActionListener {
        private Component parent;
        private JTextField posterField;

        public BrowseListener(Component parent, JTextField posterField) {
            this.parent = parent;
            this.posterField = posterField;
        }
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser("posters");   // starts in the posters folder
            int result = chooser.showOpenDialog(parent);
            if (result == JFileChooser.APPROVE_OPTION) {
                posterField.setText(chooser.getSelectedFile().getPath());
            }
        }
    }
    // One form for Add and Update (existing = null when adding).
    // Keeps asking until the fields are OK. Returns null if cancelled.
    private static Movie askMovie(Component parent, MovieManager movieManager,
                                  Movie existing) {
        JTextField titleField = new JTextField(22);
        JTextField genreField = new JTextField();
        JTextField durationField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField ratingField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<String>(
                new String[]{"Now Showing", "Coming Soon"});
        JTextField posterField = new JTextField();
        titleField.setToolTipText("The title must not be used by another movie");
        durationField.setToolTipText("Minutes, a whole number from 1 to 500");
        priceField.setToolTipText("Ticket price in pesos, greater than 0");
        posterField.setToolTipText("Picture file of the poster (use the Browse button)");

        if (existing != null) {
            titleField.setText(existing.getTitle());
            genreField.setText(existing.getGenre());
            durationField.setText("" + existing.getDuration());
            priceField.setText("" + existing.getPrice());
            ratingField.setText(existing.getRating());
            statusBox.setSelectedItem(existing.getStatus());
            posterField.setText(existing.getPosterFile());
        }

        // The poster box with a Browse button next to it
        JButton browseButton = new JButton("Browse...");
        browseButton.setToolTipText("Choose the poster picture from your computer");
        browseButton.addActionListener(new BrowseListener(parent, posterField));

        JPanel posterPanel = new JPanel(new BorderLayout(5, 0));
        posterPanel.add(posterField, BorderLayout.CENTER);
        posterPanel.add(browseButton, BorderLayout.EAST);

        JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
        form.add(new JLabel("Movie Title:"));
        form.add(titleField);
        form.add(new JLabel("Genre:"));
        form.add(genreField);
        form.add(new JLabel("Duration (minutes):"));
        form.add(durationField);
        form.add(new JLabel("Ticket Price:"));
        form.add(priceField);
        form.add(new JLabel("Rating (G, PG, R-13...):"));
        form.add(ratingField);
        form.add(new JLabel("Status:"));
        form.add(statusBox);
        form.add(new JLabel("Poster picture (click Browse, can be empty):"));
        form.add(posterPanel);

        String windowTitle = "Add Movie";
        if (existing != null) {
            windowTitle = "Update Movie " + existing.getMovieId();
        }

        while (true) {
            int result = JOptionPane.showConfirmDialog(parent, form, windowTitle,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result != JOptionPane.OK_OPTION) {
                return null;
            }

            String error = checkFields(titleField.getText().trim(),
                    genreField.getText().trim(), durationField.getText().trim(),
                    priceField.getText().trim(), ratingField.getText().trim());

            // The title must not be used by ANOTHER movie
            if (error == null) {
                Movie sameTitle = movieManager.searchMovie(titleField.getText().trim());
                if (sameTitle != null && sameTitle != existing) {
                    error = "A movie with this title already exists.";
                }
            }

            if (error != null) {
                Popup.warn(parent, "Check the Movie", error);
                continue;   // show the same form again
            }

            String id;
            if (existing != null) {
                id = existing.getMovieId();
            } else {
                id = generateMovieId(movieManager);
            }

            return new Movie(id, titleField.getText().trim(),
                    genreField.getText().trim(),
                    Integer.parseInt(durationField.getText().trim()),
                    ratingField.getText().trim(),
                    (String) statusBox.getSelectedItem(),
                    posterField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()));
        }
    }
    // Checks the form. Returns null if OK, or the problem.
    private static String checkFields(String title, String genre, String durationText,
                                      String priceText, String rating) {
        if (title.isEmpty() || genre.isEmpty() || priceText.isEmpty()
                || durationText.isEmpty() || rating.isEmpty()) {
            return "Please complete all movie fields.";
        }

        try {
            int duration = Integer.parseInt(durationText);
            if (duration <= 0 || duration > 500) {
                return "Duration must be between 1 and 500 minutes.";
            }
        } catch (NumberFormatException ex) {
            return "Please enter a valid number for duration.";
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                return "The ticket price must be greater than 0.";
            }
        } catch (NumberFormatException ex) {
            return "Please enter a valid number for the price.";
        }
        return null;
    }
    // Uses the smallest movie ID that is free (M001, M002, ...)
    private static String generateMovieId(MovieManager movieManager) {
        int number = 1;
        String id = makeId(number);

        while (movieManager.searchById(id) != null) {
            number++;
            id = makeId(number);
        }
        return id;
    }
    // Turns 1 into M001, 25 into M025, 120 into M120
    private static String makeId(int number) {
        if (number < 10) {
            return "M00" + number;
        } else if (number < 100) {
            return "M0" + number;
        } else {
            return "M" + number;
        }
    }
    // The movie rows for the JTable: one row for every movie
    private static Object[][] tableRows(ArrayList<Movie> movies) {
        Object[][] rows = new Object[movies.size()][COLUMNS.length];

        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            rows[i][0] = m.getMovieId();
            rows[i][1] = m.getTitle();
            rows[i][2] = m.getGenre();
            rows[i][3] = m.getDuration() + " min";
            rows[i][4] = Theme.peso(m.getPrice());
            rows[i][5] = m.getRating();
            rows[i][6] = m.getStatus();
        }
        return rows;
    }
}
