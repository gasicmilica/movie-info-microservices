package se.magnus.microservices.core.movie.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import se.magnus.api.core.movie.Movie;
import se.magnus.api.core.movie.MovieService;
import se.magnus.microservices.core.movie.persistence.MovieEntity;
import se.magnus.microservices.core.movie.persistence.MovieRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;


@RestController
public class MovieServiceImpl implements MovieService {
    private static final Logger LOG = LoggerFactory.getLogger(MovieServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final MovieMapper mapper;
    private final MovieRepository repository;

    @Autowired
    public MovieServiceImpl(ServiceUtil serviceUtil, MovieMapper mapper, MovieRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public Movie getMovie(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        MovieEntity entity = repository.findByMovieId(movieId)
                .orElseThrow(() -> new NotFoundException("No movie found for movieId: nije uspeo repo" + movieId));

        Movie response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getProduct: found movieId: {}", response.getMovieId());

        return response;
    }

    @Override
    public Movie createMovie(Movie body) {
        try {
            MovieEntity entity = mapper.apiToEntity(body);
            MovieEntity newEntity = repository.save(entity);

            LOG.debug("createMovie: entity created for movieId: {}", body.getMovieId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Movie Id: " + body.getMovieId());
        }
    }

    @Override
    public void deleteMovie(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);
        repository.findByMovieId(movieId).ifPresent(e -> repository.delete(e));
    }
}
