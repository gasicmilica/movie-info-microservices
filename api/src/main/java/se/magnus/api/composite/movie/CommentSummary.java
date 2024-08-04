package se.magnus.api.composite.movie;

import java.util.Date;

public class CommentSummary {
    private final int commentId;
    private final String author;
    private final Date commentDate;
    private final String commentText;

    public CommentSummary() {
        this.commentId = 0;
        this.author = null;
        this.commentDate = new Date();
        this.commentText = null;
    }

    public CommentSummary(int commentId, String author, Date commentDate, String commentText) {
        this.commentId = commentId;
        this.author = author;
        this.commentDate = commentDate;
        this.commentText = commentText;
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
}
