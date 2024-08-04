package se.magnus.microservices.core.rating.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RatingRepository extends CrudRepository<RatingEntity, String> {
    List<RatingEntity> findByMovieId(int movieId);
}
