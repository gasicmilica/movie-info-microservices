package se.magnus.microservices.core.comment.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<CommentEntity, String> {
    List<CommentEntity> findByMovieId(int movieId);
}
