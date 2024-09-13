package se.magnus.microservices.core.comment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import se.magnus.microservices.core.comment.persistence.CommentEntity;
import se.magnus.microservices.core.comment.persistence.CommentRepository;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest(properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTests {

    @Autowired
    private CommentRepository repository;

    private CommentEntity savedEntity;

    @Before
   	public void setupDb() {
   		repository.deleteAll().block();

        CommentEntity entity = new CommentEntity(1, 2, "a", "text", new Date());
        savedEntity = repository.save(entity).block();

        assertEqualsComment(entity, savedEntity);
    }


    @Test
   	public void create() {

        CommentEntity newEntity = new CommentEntity(1, 3, "a", "text", new Date());
        repository.save(newEntity).block();

        CommentEntity foundEntity = repository.findById(newEntity.getId()).block();
        assertEqualsComment(newEntity, foundEntity);

        assertEquals(2, (long)repository.count().block());
    }

    @Test
   	public void update() {
        savedEntity.setAuthor("a2");
        repository.save(savedEntity).block();

        CommentEntity foundEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity).block();
        assertFalse(repository.existsById(savedEntity.getId()).block());
    }

    @Test
   	public void getByProductId() {
        List<CommentEntity> entityList = repository.findByMovieId(savedEntity.getMovieId()).collectList().block();

        assertThat(entityList, hasSize(1));
        assertEqualsComment(savedEntity, entityList.get(0));
    }

    @Test(expected = DuplicateKeyException.class)
   	public void duplicateError() {
        CommentEntity entity = new CommentEntity(1, 2, "a", "t", new Date());
        repository.save(entity).block();
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        CommentEntity entity1 = repository.findById(savedEntity.getId()).block();
        CommentEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setAuthor("a1");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setAuthor("a2");
            repository.save(entity2).block();

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        CommentEntity updatedEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getAuthor());
    }

    private void assertEqualsComment(CommentEntity expectedEntity, CommentEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getMovieId(),        actualEntity.getMovieId());
        assertEquals(expectedEntity.getCommentId(), actualEntity.getCommentId());
        assertEquals(expectedEntity.getAuthor(),           actualEntity.getAuthor());
        assertEquals(expectedEntity.getCommentText(),           actualEntity.getCommentText());
        assertEquals(expectedEntity.getCommentDate(),          actualEntity.getCommentDate());
    }
}
