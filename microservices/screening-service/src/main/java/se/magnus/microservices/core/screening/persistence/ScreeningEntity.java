package se.magnus.microservices.core.screening.persistence;

import javax.persistence.*;
import java.util.Date;

import static java.lang.String.format;

@Entity
@Table(name = "screenings", indexes = { @Index(name = "screenings_unique_idx", unique = true, columnList = "movieId,screeningId") })
public class ScreeningEntity {

    @Id
    @GeneratedValue
    private int id;

    @Version
    private int version;

    private int movieId;
    private int screeningId;
    private String cinemaName;
    private Date screeningDate;
    private double price;
    private String location;

    public ScreeningEntity() {}

    public ScreeningEntity(int movieId, int screeningId, String cinemaName, Date screeningDate, double price, String location) {
        this.movieId = movieId;
        this.screeningId = screeningId;
        this.cinemaName = cinemaName;
        this.screeningDate = screeningDate;
        this.price = price;
        this.location = location;
    }

    @Override
    public String toString() {
        return format("ScreeningEntity: %s/%d", movieId, screeningId);
    }

    public int getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getScreeningId() {
        return screeningId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public Date getScreeningDate() {
        return screeningDate;
    }

    public double getPrice() {
        return price;
    }

    public void setLocation(String newLocation) {
        this.location = newLocation;
    }

    public String getLocation() {
        return location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public void setScreeningDate(Date screeningDate) {
        this.screeningDate = screeningDate;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
