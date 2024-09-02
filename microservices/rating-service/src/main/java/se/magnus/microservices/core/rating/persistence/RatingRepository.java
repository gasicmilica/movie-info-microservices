package se.magnus.microservices.core.rating.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface RatingRepository extends ReactiveCrudRepository<RatingEntity, String> {
    Flux<RatingEntity> findByMovieId(int movieId);
}
