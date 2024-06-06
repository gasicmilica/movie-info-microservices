package se.magnus.api.core.screening;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ScreeningService {
    @GetMapping(
            value = "/screening",
            produces = "application/json")
    List<Screening> getScreenings(@RequestParam(value = "movieId") int movieId);
}
