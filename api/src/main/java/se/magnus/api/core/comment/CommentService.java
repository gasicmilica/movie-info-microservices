package se.magnus.api.core.comment;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface CommentService {

    @GetMapping(
            value = "/comment",
            produces = "application/json")
    List<Comment> getComments(@RequestParam(value = "movieId") int movieId);

    @PostMapping(
            value    = "/comment",
            consumes = "application/json",
            produces = "application/json")
    Comment createComment(@RequestBody Comment body);

    @DeleteMapping(value = "/comment")
    void deleteComments(@RequestParam(value = "movieId", required = true)  int movieId);

}
