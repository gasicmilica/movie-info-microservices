package se.magnus.api.core.screening;

import se.magnus.api.composite.movie.ServiceAddresses;

import java.util.Date;

public class Screening {
    private final int movieId;
    private final int screeningId;
    private final String cinemaName;
    private final Date screeningDate;
    private final double price;
    private final String location;
    private final String serviceAddress;


    public Screening() {
        this.movieId = 1;
        this.screeningId = 1;
        this.cinemaName = null;
        this.screeningDate = null;
        this.price = 2400;
        this.location = null;
        this.serviceAddress = null;
    }

    public Screening(int movieId, int screeningId, String cinemaName, Date screeningDate, double price, String location, String serviceAddress) {
        this.movieId = movieId;
        this.screeningId = screeningId;
        this.cinemaName = cinemaName;
        this.screeningDate = screeningDate;
        this.price = price;
        this.location = location;
        this.serviceAddress = serviceAddress;
    }

    public int getMovieId() {
        return movieId;
    }

    public Date getScreeningDate() {
        return screeningDate;
    }

    public double getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public int getScreeningId() {
        return screeningId;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }
}
