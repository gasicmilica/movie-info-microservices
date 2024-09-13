package se.magnus.microservices.core.comment;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import se.magnus.api.core.comment.Comment;
import se.magnus.microservices.core.comment.persistence.CommentEntity;
import se.magnus.microservices.core.comment.services.CommentMapper;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Comment api = new Comment(1, 1,"author", new Date(), "content", "mock address");

        CommentEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getMovieId(), entity.getMovieId());
        assertEquals(api.getCommentId(), entity.getCommentId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getCommentDate(), entity.getCommentDate());
        assertEquals(api.getCommentText(), entity.getCommentText());

        Comment api2 = mapper.entityToApi(entity);

        assertEquals(api.getMovieId(), api2.getMovieId());
        assertEquals(api.getCommentId(), api2.getCommentId());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getCommentDate(), api2.getCommentDate());
        assertEquals(api.getCommentText(), api2.getCommentText());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        Comment api = new Comment(1, 1,"author", new Date(), "content", "mock address");

        List<Comment> apiList = Collections.singletonList(api);

        List<CommentEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        CommentEntity entity = entityList.get(0);

        assertEquals(api.getMovieId(), entity.getMovieId());
        assertEquals(api.getCommentId(), entity.getCommentId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getCommentDate(), entity.getCommentDate());
        assertEquals(api.getCommentText(), entity.getCommentText());

        List<Comment> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Comment api2 = api2List.get(0);

        assertEquals(api.getMovieId(), api2.getMovieId());
        assertEquals(api.getCommentId(), api2.getCommentId());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getCommentDate(), api2.getCommentDate());
        assertEquals(api.getCommentText(), api2.getCommentText());
        assertNull(api2.getServiceAddress());
    }
}
