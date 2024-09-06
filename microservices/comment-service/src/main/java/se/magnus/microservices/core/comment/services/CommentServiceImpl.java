package se.magnus.microservices.core.comment.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.comment.CommentService;
import se.magnus.microservices.core.comment.persistence.CommentEntity;
import se.magnus.microservices.core.comment.persistence.CommentRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;


@RestController
public class CommentServiceImpl implements CommentService {
    private final ServiceUtil serviceUtil;
    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);
    private final CommentRepository repository;
    private final CommentMapper mapper;

    @Autowired
    public CommentServiceImpl(ServiceUtil serviceUtil, CommentRepository repository, CommentMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Flux<Comment> getComments(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        return repository.findByMovieId(movieId)
                .log()
                .map(mapper::entityToApi)
                .map(e -> { e.setServiceAddress(serviceUtil.getServiceAddress()); return e; });
    }

    @Override
    public Comment createComment(Comment body) {
        CommentEntity entity = mapper.apiToEntity(body);
        Mono<Comment> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Movie Id: " + body.getMovieId() + ", Comment Id:" + body.getCommentId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public void deleteComments(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);
        repository.deleteAll(repository.findByMovieId(movieId)).block();
    }
}
