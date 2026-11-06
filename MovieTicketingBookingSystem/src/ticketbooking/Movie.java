package ticketbooking;

public class Movie {

    public static final double DEFAULT_PRICE = 250;   // only used for old files with no price

    private String movieId;
    private String title;
    private String genre;
    private int duration;
    private String rating;
    private String status;
    private String posterFile;   // picture file, example: posters/galaxy_rangers.png
    private double price;        // ticket price of this movie (no limit)
    public Movie(String movieId, String title, String genre,
                 int duration, String rating, String status,
                 String posterFile, double price) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.status = status;
        this.posterFile = posterFile;
        this.price = price;
    }
    public String getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    public String getRating() {
        return rating;
    }

    public String getStatus() {
        return status;
    }

    public String getPosterFile() {
        return posterFile;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPosterFile(String posterFile) {
        this.posterFile = posterFile;
    }
}
