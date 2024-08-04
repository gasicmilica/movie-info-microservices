package se.magnus.api.composite.movie;

import java.util.Date;

public class ScreeningSummary {
    private final int screeningId;
    private final String cinemaName;
    private final Date screeningDate;
    private final double price;
    private final String location;


    public ScreeningSummary() {
        this.screeningId = 0;
        this.cinemaName = null;
        this.screeningDate = new Date();
        this.price = 0;
        this.location = null;
    }

    public ScreeningSummary(int screeningId, String cinemaName, Date screeningDate, double price, String location) {
        this.screeningId = screeningId;
        this.cinemaName = cinemaName;
        this.screeningDate = screeningDate;
        this.price = price;
        this.location = location;
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
}
