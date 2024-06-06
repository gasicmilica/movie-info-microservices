package se.magnus.api.core.movie;


public class Movie {
    private final int movieId;
    private final String title;
    private final String director;
    private final int releaseYear;
    private final int duration;
    private final String genre;
    private final String serviceAddress;

    public Movie() {
        movieId = 1;
        title = "Title";
        director = null;
        this.releaseYear = 2013;
        this.duration = 2;
        this.genre = null;
        this.serviceAddress = null;
    }

    public Movie(int movieId, String title, String director, int releaseYear, int duration, String genre, String serviceAddress) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
        this.duration = duration;
        this.genre = genre;
        this.serviceAddress = serviceAddress;
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

    public String getServiceAddress() {
        return serviceAddress;
    }
}