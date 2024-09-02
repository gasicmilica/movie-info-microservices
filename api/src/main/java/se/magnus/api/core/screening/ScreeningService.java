package se.magnus.api.core.screening;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


public interface ScreeningService {
    @GetMapping(
            value = "/screening",
            produces = "application/json")
    Flux<Screening> getScreenings(@RequestParam(value = "movieId") int movieId);

    @PostMapping(
            value    = "/screening",
            consumes = "application/json",
            produces = "application/json")
    Screening createScreening(@RequestBody Screening body);

    @DeleteMapping(value = "/screening")
    void deleteScreenings(@RequestParam(value = "movieId", required = true)  int movieId);
}
