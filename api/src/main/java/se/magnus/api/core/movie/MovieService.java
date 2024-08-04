package se.magnus.api.core.movie;

import org.springframework.web.bind.annotation.*;

public interface MovieService {

    @GetMapping(
            value = "/movie/{movieId}",
            produces = "application/json")
    Movie getMovie(@PathVariable int movieId);

    @PostMapping(
            value    = "/movie",
            consumes = "application/json",
            produces = "application/json")
    Movie createMovie(@RequestBody Movie body);

    @DeleteMapping(value = "/movie/{movieId}")
    void deleteMovie(@PathVariable int movieId);
}
