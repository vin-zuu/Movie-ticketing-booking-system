package ticketbooking;

import java.util.ArrayList;

// Keeps the movie list IN MEMORY only. Nothing is saved to a file: changes
// made in Movie Management stay while the program is running (even after
// logout) and the sample movies come back when the program is started again.
public class MovieManager {

    private ArrayList<Movie> movieList;
    public MovieManager() {
        movieList = new ArrayList<Movie>();
        addSampleMovies();
    }
    // The movies that are now showing (SM Cinema list)
    private void addSampleMovies() {
        movieList.add(new Movie("M001", "The Super Mario Galaxy Movie", "Animation",
                98, "PG", "Now Showing", "posters/mario_galaxy.png", 250.0));
        movieList.add(new Movie("M002", "Ready or Not 2: Here I Come", "Horror",
                108, "R-16", "Now Showing", "posters/ready_or_not_2.png", 300.0));
        movieList.add(new Movie("M003", "Project Hail Mary", "Sci-Fi",
                156, "PG", "Now Showing", "posters/project_hail_mary.png", 350.0));
        movieList.add(new Movie("M004", "The Drama", "Romance",
                106, "R-13", "Now Showing", "posters/the_drama.png", 300.0));
        movieList.add(new Movie("M005", "5 Centimeters per Second", "Romance",
                121, "PG", "Now Showing", "posters/five_cm_per_second.png", 280.0));
        movieList.add(new Movie("M006", "Unexpected Family", "Drama",
                121, "PG", "Now Showing", "posters/unexpected_family.png", 260.0));
        movieList.add(new Movie("M007", "Our House", "Horror",
                100, "R-13", "Now Showing", "posters/our_house.png", 320.0));
        movieList.add(new Movie("M008", "Wonderful Nightmare", "Comedy",
                105, "PG", "Now Showing", "posters/wonderful_nightmare.png", 270.0));
        movieList.add(new Movie("M009", "I Fell, It's Fine", "Romance",
                105, "R-16", "Now Showing", "posters/i_fell_its_fine.png", 290.0));
        movieList.add(new Movie("M010", "How to Make a Killing", "Thriller",
                105, "R-16", "Now Showing", "posters/how_to_make_a_killing.png", 340.0));
    }
    public void addMovie(Movie movie) {
        movieList.add(movie);
    }
    public ArrayList<Movie> getMovieList() {
        return movieList;
    }

    // ---------------- LINEAR SEARCH (DSA) ----------------
    // Linear search looks at the movies one by one, from the first to the
    // last, and stops when the movie is found. Each method returns the
    // position (index) in the list, or -1 if the movie is not there.

    // Search by movie ID (example: M003)
    public int linearSearchById(String movieId) {
        if (movieId == null) {
            return -1;
        }
        for (int i = 0; i < movieList.size(); i++) {
            if (movieList.get(i).getMovieId().equalsIgnoreCase(movieId.trim())) {
                return i;   // found
            }
        }
        return -1;   // not found
    }
    // Search by exact title (small or capital letters are okay)
    public int linearSearchByTitle(String title) {
        if (title == null) {
            return -1;
        }
        for (int i = 0; i < movieList.size(); i++) {
            if (movieList.get(i).getTitle().equalsIgnoreCase(title.trim())) {
                return i;   // found
            }
        }
        return -1;   // not found
    }
    public Movie searchById(String movieId) {
        int index = linearSearchById(movieId);
        if (index == -1) {
            return null;
        }
        return movieList.get(index);
    }
    public Movie searchMovie(String title) {
        int index = linearSearchByTitle(title);
        if (index == -1) {
            return null;
        }
        return movieList.get(index);
    }
    // Search by ID OR title: the result is a list (empty if nothing matches)
    public ArrayList<Movie> searchMovies(String key) {
        ArrayList<Movie> results = new ArrayList<Movie>();

        int index = linearSearchById(key);
        if (index != -1) {
            results.add(movieList.get(index));
        }
        for (int i = 0; i < movieList.size(); i++) {
            Movie movie = movieList.get(i);
            if (movie.getTitle().equalsIgnoreCase(key.trim())
                    && !results.contains(movie)) {
                results.add(movie);
            }
        }
        return results;
    }
    public boolean deleteMovie(String movieId) {
        int index = linearSearchById(movieId);
        if (index == -1) {
            return false;
        }
        movieList.remove(index);
        return true;
    }
    // Count movies by status ("Now Showing" or "Coming Soon")
    public int countMovies(String status) {
        int count = 0;
        for (Movie movie : movieList) {
            if (movie.getStatus().equalsIgnoreCase(status)) {
                count++;
            }
        }
        return count;
    }
}
