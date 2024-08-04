package se.magnus.microservices.core.comment.persistence;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

import static java.lang.String.format;

@Document(collection="comments")
@CompoundIndex(name = "movie-com-id", unique = true, def = "{'movieId': 1, 'commentId' : 1}")
public class CommentEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int movieId;
    private int commentId;
    private String author;
    private String commentText;
    private Date commentDate;

    public CommentEntity() { }

    public CommentEntity(int movieId, int commentId, String author, String commentText, Date commentDate) {
        this.movieId = movieId;
        this.commentId = commentId;
        this.author = author;
        this.commentText = commentText;
        this.commentDate = commentDate;
    }

    @Override
    public String toString() {
        return format("CommentEntity: %s/%d", movieId, commentId);
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

    public int getCommentId() {
        return commentId;
    }

    public String getAuthor() {
        return author;
    }

    public String getCommentText() {
        return commentText;
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

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCommentText(String content) {
        this.commentText = content;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }
}

