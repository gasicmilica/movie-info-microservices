package se.magnus.microservices.core.comment.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.comment.Comment;
import se.magnus.api.core.comment.CommentService;
import se.magnus.microservices.core.comment.persistence.CommentEntity;
import se.magnus.microservices.core.comment.persistence.CommentRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;


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
    public List<Comment> getComments(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        List<CommentEntity> entityList = repository.findByMovieId(movieId);
        List<Comment> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getComments: response size: {}", list.size());

        return list;
    }

    @Override
    public Comment createComment(Comment body) {
        try {
            CommentEntity entity = mapper.apiToEntity(body);
            CommentEntity newEntity = repository.save(entity);

            LOG.debug("createComment: created a comment entity: {}/{}", body.getMovieId(), body.getCommentId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Rating Id: " + body.getMovieId() + ", Comment Id:" + body.getCommentId());
        }
    }

    @Override
    public void deleteComments(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);
        repository.deleteAll(repository.findByMovieId(movieId));
    }
}
