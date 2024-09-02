package se.magnus.api.core.comment;


import java.util.Date;

public class Comment {
    private int movieId;
    private int commentId;
    private String author;
    private Date commentDate;
    private String commentText;
    private String serviceAddress;


    public Comment() {
        this.movieId = 0;
        this.commentId = 0;
        this.author = "";
        this.commentDate = new Date();
        this.commentText = "";
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

    public void setServiceAddress(String address) {
        this.serviceAddress = address;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
