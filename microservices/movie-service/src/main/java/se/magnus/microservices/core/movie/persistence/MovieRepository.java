package se.magnus.microservices.core.movie.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MovieRepository extends CrudRepository<MovieEntity, String> {
    Optional<MovieEntity> findByMovieId(int movieId);
}
