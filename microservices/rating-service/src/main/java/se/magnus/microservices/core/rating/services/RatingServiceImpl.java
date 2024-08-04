package se.magnus.microservices.core.rating.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.rating.Rating;
import se.magnus.api.core.rating.RatingService;
import se.magnus.microservices.core.rating.persistence.RatingEntity;
import se.magnus.microservices.core.rating.persistence.RatingRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;


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
    public List<Rating> getRatings(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        List<RatingEntity> entityList = repository.findByMovieId(movieId);
        List<Rating> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getRatings: response size: {}", list.size());

        return list;
    }

    @Override
    public Rating createRating(Rating body) {
        try {
            RatingEntity entity = mapper.apiToEntity(body);
            RatingEntity newEntity = repository.save(entity);

            LOG.debug("createRating: created a rating entity: {}/{}", body.getMovieId(), body.getRatingId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Movie Id: " + body.getMovieId() + ", Rating Id:" + body.getRatingId());
        }
    }

    @Override
    public void deleteRatings(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);
        LOG.debug("deleteRatings: tries to delete ratings for the movie with movieId: {}", movieId);
        repository.deleteAll(repository.findByMovieId(movieId));
    }
}
