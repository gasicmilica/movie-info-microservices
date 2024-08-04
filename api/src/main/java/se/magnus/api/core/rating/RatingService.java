package se.magnus.api.core.rating;

import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface RatingService {
    @GetMapping(
            value = "/rating",
            produces = "application/json")
    List<Rating> getRatings(@RequestParam(value = "movieId") int movieId);

    @PostMapping(
            value    = "/rating",
            consumes = "application/json",
            produces = "application/json")
    Rating createRating(@RequestBody Rating body);

    @DeleteMapping(value = "/rating")
    void deleteRatings(@RequestParam(value = "movieId", required = true)  int movieId);
}
