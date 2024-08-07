package se.magnus.api.core.movie;


public class Movie {
    private int movieId;
    private String title;
    private String director;
    private int releaseYear;
    private int duration;
    private String genre;
    private String serviceAddress;

    public Movie() {
        movieId = 0;
        title = "";
        director = null;
        this.releaseYear = 0;
        this.duration = 0;
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

    public void setServiceAddress(String address) {
        this.serviceAddress = address;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}