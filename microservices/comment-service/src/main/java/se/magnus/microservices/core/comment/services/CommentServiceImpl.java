package se.magnus.microservices.core.comment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.comment.CommentService;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.movie.MovieService;
import se.magnus.util.http.ServiceUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
public class CommentServiceImpl implements CommentService {
    private final ServiceUtil serviceUtil;

    @Autowired
    public CommentServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Comment> getComments(int movieId) {
        List<Comment> list = new ArrayList<>();
        list.add(new Comment(movieId, 1, "Author 1", new Date(), "Komentar 1", serviceUtil.getServiceAddress()));
        list.add(new Comment(movieId, 2, "Author 2", new Date(), "Komentar 2", serviceUtil.getServiceAddress()));
        list.add(new Comment(movieId, 3, "Author 3", new Date(), "Komentar 3", serviceUtil.getServiceAddress()));

        return list;
    }
}
