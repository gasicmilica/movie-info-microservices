package se.magnus.microservices.core.rating.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.rating.RatingService;
import se.magnus.microservices.core.rating.persistence.RatingEntity;
import se.magnus.microservices.core.rating.persistence.RatingRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;


@RestController
public class RatingServiceImpl implements RatingService {
    private final ServiceUtil serviceUtil;
    private static final Logger LOG = LoggerFactory.getLogger(RatingServiceImpl.class);
    private final RatingRepository repository;
    private final RatingMapper mapper;

    @Autowired
    public RatingServiceImpl(ServiceUtil serviceUtil, RatingRepository repository, RatingMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Flux<Rating> getRatings(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        return repository.findByMovieId(movieId)
                .log()
                .map(mapper::entityToApi)
                .map(e -> { e.setServiceAddress(serviceUtil.getServiceAddress()); return e; });
    }

    @Override
    public Rating createRating(Rating body) {
        RatingEntity entity = mapper.apiToEntity(body);
        Mono<Rating> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Movie Id: " + body.getMovieId() + ", Rating Id:" + body.getRatingId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public void deleteRatings(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);
        LOG.debug("deleteRatings: tries to delete ratings for the movie with movieId: {}", movieId);
        repository.deleteAll(repository.findByMovieId(movieId)).block();
    }
}
