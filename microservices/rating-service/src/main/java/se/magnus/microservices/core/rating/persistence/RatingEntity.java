package se.magnus.microservices.core.rating.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static java.lang.String.format;

@Document(collection="ratings")
@CompoundIndex(name = "movie-rat-id", unique = true, def = "{'movieId': 1, 'ratingId' : 1}")
public class RatingEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int movieId;
    private int ratingId;
    private String author;
    private Date ratingDate;
    private int ratingNumber;

    public RatingEntity() {}

    public RatingEntity(int movieId, int ratingId, String author, Date ratingDate, int ratingNumber) {
        this.id = id;
        this.movieId = movieId;
        this.ratingId = ratingId;
        this.author = author;
        this.ratingDate = ratingDate;
        this.ratingNumber = ratingNumber;
    }

    @Override
    public String toString() {
        return format("RatingEntity: %s/%d", movieId, ratingId);
    }

    public String getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
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

    public void setAuthor(String newAuthor) {
        this.author = newAuthor;
    }

    public Date getRatingDate() {
        return ratingDate;
    }

    public int getRatingNumber() {
        return ratingNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public void setRatingDate(Date ratingDate) {
        this.ratingDate = ratingDate;
    }

    public void setRatingNumber(int ratingNumber) {
        this.ratingNumber = ratingNumber;
    }
}
