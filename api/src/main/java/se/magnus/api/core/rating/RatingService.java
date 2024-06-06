package se.magnus.api.core.rating;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RatingService {
    @GetMapping(
            value = "/rating",
            produces = "application/json")
    List<Rating> getRatings(@RequestParam(value = "movieId") int movieId);
}
