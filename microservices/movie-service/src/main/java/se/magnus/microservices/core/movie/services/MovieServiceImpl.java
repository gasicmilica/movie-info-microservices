package se.magnus.microservices.core.movie.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.movie.MovieService;
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
        return new Movie(movieId, "Mi nismo andjeli", "Emili Blant", 2004, 1,"Komedija", serviceUtil.getServiceAddress());
    }
}
