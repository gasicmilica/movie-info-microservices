package se.magnus.api.core.comment;

import se.magnus.api.composite.movie.ServiceAddresses;

import java.util.Date;

public class Comment {
    private final int movieId;
    private final int commentId;
    private final String author;
    private final Date commentDate;
    private final String commentText;
    private final String serviceAddress;


    public Comment() {
        this.movieId = 1;
        this.commentId = 1;
        this.author = "Author";
        this.commentDate = new Date();
        this.commentText = "commentText";
        this.serviceAddress = null;
    }

    public Comment(int movieId, int commentId, String author, Date commentDate, String commentText, String serviceAddress) {
        this.movieId = movieId;
        this.commentId = commentId;
        this.author = author;
        this.commentDate = commentDate;
        this.commentText = commentText;
        this.serviceAddress = serviceAddress;
    }

    public int getMovieId() {
        return movieId;
    }

    public int getCommentId() {
        return commentId;
    }

    public String getAuthor() {
        return author;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }
}
