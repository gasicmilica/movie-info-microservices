package se.magnus.microservices.core.movie.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.movie.MovieService;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;


@RestController
public class MovieServiceImpl implements MovieService {
    private final ServiceUtil serviceUtil;

    @Autowired
    public MovieServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Movie getMovie(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        if (movieId == 13) throw new NotFoundException("No product found for movieId: " + movieId);

        return new Movie(movieId, "Mi nismo andjeli", "Emili Blant", 2004, 1,"Komedija", serviceUtil.getServiceAddress());
    }
}
