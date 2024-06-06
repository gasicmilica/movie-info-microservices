package se.magnus.api.composite.movie;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface MovieCompositeService {
    @GetMapping(
            value    = "/movie-composite/{movieId}",
            produces = "application/json")
    MovieAggregate getMovie(@PathVariable int movieId);
}
