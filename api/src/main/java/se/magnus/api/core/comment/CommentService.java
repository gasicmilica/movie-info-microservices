package se.magnus.api.core.comment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CommentService {

    @GetMapping(
            value = "/comment",
            produces = "application/json")
    List<Comment> getComments(@RequestParam(value = "movieId") int movieId);
}
