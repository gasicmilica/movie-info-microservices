package se.magnus.microservices.core.movie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;
import se.magnus.microservices.core.movie.persistence.MovieEntity;
import se.magnus.microservices.core.movie.persistence.MovieRepository;

@RunWith(SpringRunner.class)
@DataMongoTest(properties = {"spring.cloud.config.enabled=false"})
public class PersistenceTests {

    @Autowired
    private MovieRepository repository;

    private MovieEntity savedEntity;

    @Before
   	public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        MovieEntity entity = new MovieEntity(1, "title", "director", 2000, 2, "genre");
        StepVerifier.create(repository.save(entity))
            .expectNextMatches(createdEntity -> {
                savedEntity = createdEntity;
                return areMoviesEqual(entity, savedEntity);
            })
            .verifyComplete();
    }


    @Test
   	public void create() {
        MovieEntity newEntity = new MovieEntity(2, "title2", "director", 2000, 2, "genre");

        StepVerifier.create(repository.save(newEntity))
            .expectNextMatches(createdEntity -> newEntity.getMovieId() == createdEntity.getMovieId())
            .verifyComplete();

        StepVerifier.create(repository.findById(newEntity.getId()))
            .expectNextMatches(foundEntity -> areMoviesEqual(newEntity, foundEntity))
            .verifyComplete();

        StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
    }

    @Test
   	public void update() {
        savedEntity.setDirector("director2");
        StepVerifier.create(repository.save(savedEntity))
            .expectNextMatches(updatedEntity -> updatedEntity.getDirector().equals("director2"))
            .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
            .expectNextMatches(foundEntity ->
                foundEntity.getVersion() == 1 &&
                foundEntity.getDirector().equals("director2"))
            .verifyComplete();
    }

    @Test
   	public void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

    @Test
   	public void getByProductId() {

        StepVerifier.create(repository.findByMovieId(savedEntity.getMovieId()))
            .expectNextMatches(foundEntity -> areMoviesEqual(savedEntity, foundEntity))
            .verifyComplete();
    }

    @Test
   	public void duplicateError() {
        MovieEntity entity = new MovieEntity(savedEntity.getMovieId(), "title", "director", 2000, 2, "genre");
        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
    }

    @Test
   	public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        MovieEntity entity1 = repository.findById(savedEntity.getId()).block();
        MovieEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setDirector("director1");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId()))
            .expectNextMatches(foundEntity ->
                foundEntity.getVersion() == 1 &&
                foundEntity.getDirector().equals("director1"))
            .verifyComplete();
    }

    private boolean areMoviesEqual(MovieEntity expectedEntity, MovieEntity actualEntity) {
        return
            (expectedEntity.getId().equals(actualEntity.getId()))
            && (expectedEntity.getVersion() == actualEntity.getVersion())
            && (expectedEntity.getMovieId() == actualEntity.getMovieId())
            && (expectedEntity.getTitle().equals(actualEntity.getTitle()))
            && (expectedEntity.getDirector().equals(actualEntity.getDirector()))
            && (expectedEntity.getDuration() == actualEntity.getDuration())
            && (expectedEntity.getReleaseYear() == actualEntity.getReleaseYear());
    }
}
