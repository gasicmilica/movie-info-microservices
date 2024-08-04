package se.magnus.api.core.rating;

import java.util.Date;

public class Rating {
    private int movieId;
    private int ratingId;
    private String author;
    private Date ratingDate;
    private int ratingNumber;
    private String serviceAddress;


    public Rating() {
        this.movieId = 0;
        this.ratingId = 0;
        this.author = "";
        this.ratingDate = null;
        this.ratingNumber = 0;
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

    public void setServiceAddress(String address) {
        this.serviceAddress = address;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setRatingDate(Date ratingDate) {
        this.ratingDate = ratingDate;
    }

    public void setRatingNumber(int ratingNumber) {
        this.ratingNumber = ratingNumber;
    }
}
