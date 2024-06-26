package se.magnus.api.core.movie;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface MovieService {

    @GetMapping(
            value = "/movie/{movieId}",
            produces = "application/json")
    Movie getMovie(@PathVariable int movieId);
}
