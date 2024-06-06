package se.magnus.api.composite.movie;

import java.util.List;

public class MovieAggregate {
    private final int movieId;
    private final String title;
    private final String director;
    private final int releaseYear;
    private final int duration;
    private final String genre;
    private final ServiceAddresses serviceAddresses;
    private final List<CommentSummary> comments;
    private final List<RatingSummary> ratings;
    private final List<ScreeningSummary> screenings;


    public MovieAggregate(int movieId, String title, String director, int releaseYear, int duration, String genre, ServiceAddresses serviceAddresses, List<CommentSummary> comments, List<RatingSummary> ratings, List<ScreeningSummary> screenings) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
        this.duration = duration;
        this.genre = genre;
        this.serviceAddresses = serviceAddresses;
        this.comments = comments;
        this.ratings = ratings;
        this.screenings = screenings;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getDuration() {
        return duration;
    }

    public String getGenre() {
        return genre;
    }

    public List<CommentSummary> getComments() {
        return comments;
    }

    public List<RatingSummary> getRatings() {
        return ratings;
    }

    public List<ScreeningSummary> getScreenings() {
        return screenings;
    }

    public ServiceAddresses getServiceAddresses() {
        return serviceAddresses;
    }
}
