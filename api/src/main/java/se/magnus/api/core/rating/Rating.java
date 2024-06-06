package se.magnus.api.core.rating;

import se.magnus.api.composite.movie.ServiceAddresses;
import se.magnus.api.core.screening.Screening;

import java.util.Date;

public class Rating {
    private final int movieId;
    private final int ratingId;
    private final String author;
    private final Date ratingDate;
    private final int ratingNumber;
    private final String serviceAddress;


    public Rating() {
        this.movieId = 1;
        this.ratingId = 1;
        this.author = "author";
        this.ratingDate = null;
        this.ratingNumber = 5;
        this.serviceAddress = null;
    }

    public Rating(int movieId, int ratingId, String author, Date ratingDate, int ratingNumber, String serviceAddress) {
        this.movieId = movieId;
        this.ratingId = ratingId;
        this.author = author;
        this.ratingDate = ratingDate;
        this.ratingNumber = ratingNumber;
        this.serviceAddress = serviceAddress;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getRatingId() {
        return ratingId;
    }

    public String getAuthor() {
        return author;
    }

    public Date getRatingDate() {
        return ratingDate;
    }

    public int getRatingNumber() {
        return ratingNumber;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }
}
