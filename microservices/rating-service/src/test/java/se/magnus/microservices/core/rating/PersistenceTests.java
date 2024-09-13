package se.magnus.microservices.core.rating;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import se.magnus.microservices.core.rating.persistence.RatingEntity;
import se.magnus.microservices.core.rating.persistence.RatingRepository;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest(properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTests {

    @Autowired
    private RatingRepository repository;

    private RatingEntity savedEntity;

    @Before
   	public void setupDb() {
   		repository.deleteAll().block();

        RatingEntity entity = new RatingEntity(1, 2, "a", new Date(), 8);
        savedEntity = repository.save(entity).block();

        assertEqualsRating(entity, savedEntity);
    }


    @Test
   	public void create() {

        RatingEntity newEntity = new RatingEntity(1, 3, "a", new Date(), 8);

        repository.save(newEntity).block();

        RatingEntity foundEntity = repository.findById(newEntity.getId()).block();
        assertEqualsRating(newEntity, foundEntity);

        assertEquals(2, (long)repository.count().block());
    }

    @Test
   	public void update() {
        savedEntity.setAuthor("a2");
        repository.save(savedEntity).block();

        RatingEntity foundEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("a2", foundEntity.getAuthor());
    }

    @Test
   	public void delete() {
        repository.delete(savedEntity).block();
        assertFalse(repository.existsById(savedEntity.getId()).block());
    }

    @Test
   	public void getByMovieId() {
        List<RatingEntity> entityList = repository.findByMovieId(savedEntity.getMovieId()).collectList().block();

        assertThat(entityList, hasSize(1));
        assertEqualsRating(savedEntity, entityList.get(0));
    }

    @Test(expected = DuplicateKeyException.class)
   	public void duplicateError() {
        RatingEntity entity = new RatingEntity(1, 2, "a", new Date(), 10);
        repository.save(entity).block();
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        RatingEntity entity1 = repository.findById(savedEntity.getId()).block();
        RatingEntity entity2 = repository.findById(savedEntity.getId()).block();

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
        RatingEntity updatedEntity = repository.findById(savedEntity.getId()).block();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("a1", updatedEntity.getAuthor());
    }

    private void assertEqualsRating(RatingEntity expectedEntity, RatingEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getMovieId(),        actualEntity.getMovieId());
        assertEquals(expectedEntity.getRatingId(), actualEntity.getRatingId());
        assertEquals(expectedEntity.getAuthor(),           actualEntity.getAuthor());
        assertEquals(expectedEntity.getRatingDate(),           actualEntity.getRatingDate());
        assertEquals(expectedEntity.getRatingNumber(),          actualEntity.getRatingNumber());
    }
}
