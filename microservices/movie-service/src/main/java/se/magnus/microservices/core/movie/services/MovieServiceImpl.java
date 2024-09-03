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

import static reactor.core.publisher.Mono.error;


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
    public Mono<Movie> getMovie(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        return repository.findByMovieId(movieId)
                .switchIfEmpty(error(new NotFoundException("No movie found for movieId: " + movieId)))
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public Movie createMovie(Movie body) {

        if (body.getMovieId() < 1) throw new InvalidInputException("Invalid movieId: " + body.getMovieId());

        MovieEntity entity = mapper.apiToEntity(body);
        Mono<Movie> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Movie Id: " + body.getMovieId()))
                .map(mapper::entityToApi);

        LOG.debug("createMovie: entity created for movieId: {}", body.getMovieId());

        return newEntity.block();
    }

    @Override
    public void deleteMovie(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        LOG.debug("deleteMovie: tries to delete an entity with movieId: {}", movieId);
        repository.findByMovieId(movieId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();
    }
}
