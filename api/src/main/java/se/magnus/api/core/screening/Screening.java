package se.magnus.api.core.screening;

import se.magnus.api.composite.movie.ServiceAddresses;

import java.util.Date;

public class Screening {
    private int movieId;
    private int screeningId;
    private String cinemaName;
    private Date screeningDate;
    private double price;
    private String location;
    private String serviceAddress;


    public Screening() {
        this.movieId = 0;
        this.screeningId = 0;
        this.cinemaName = null;
        this.screeningDate = null;
        this.price =0;
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

    public void setServiceAddress(String address) {
        this.serviceAddress = address;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setScreeningId(int screeningId) {
        this.screeningId = screeningId;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setScreeningDate(Date screeningDate) {
        this.screeningDate = screeningDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
