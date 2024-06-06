package se.magnus.api.composite.movie;

import java.util.Date;

public class RatingSummary {
    private final int ratingId;
    private final String author;
    private final Date ratingDate;
    private final int ratingNumber;


    public RatingSummary(int ratingId, String author, Date ratingDate, int ratingNumber) {
        this.ratingId = ratingId;
        this.author = author;
        this.ratingDate = ratingDate;
        this.ratingNumber = ratingNumber;
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
}
