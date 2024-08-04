//package se.magnus.microservices.core.screening;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.dao.OptimisticLockingFailureException;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//import se.magnus.microservices.core.screening.persistence.ScreeningEntity;
//import se.magnus.microservices.core.screening.persistence.ScreeningRepository;
//
//import java.util.Date;
//import java.util.List;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.junit.Assert.*;
//import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
//
//@RunWith(SpringRunner.class)
//@DataJpaTest
//@Transactional(propagation = NOT_SUPPORTED)
//public class PersistenceTests {
//
//    @Autowired
//    private ScreeningRepository repository;
//
//    private ScreeningEntity savedEntity;
//
//    @Before
//   	public void setupDb() {
//   		repository.deleteAll();
//
//        ScreeningEntity entity = new ScreeningEntity(1, 2, "c", new Date(), 200, "l");
//        savedEntity = repository.save(entity);
//
//        assertEqualsScreenings(entity, savedEntity);
//    }
//
//
//    @Test
//   	public void create() {
//
//        ScreeningEntity newEntity = new ScreeningEntity(2, 3, "c", new Date(), 200, "l");
//        repository.save(newEntity);
//
//        ScreeningEntity foundEntity = repository.findById(newEntity.getId()).get();
//        assertEqualsScreenings(newEntity, foundEntity);
//
//        assertEquals(2, repository.count());
//    }
//
//    @Test
//   	public void update() {
//        savedEntity.setLocation("l2");
//        repository.save(savedEntity);
//
//        ScreeningEntity foundEntity = repository.findById(savedEntity.getId()).get();
//        assertEquals(1, (long)foundEntity.getVersion());
//        assertEquals("l2", foundEntity.getLocation());
//    }
//
//    @Test
//   	public void delete() {
//        repository.delete(savedEntity);
//        assertFalse(repository.existsById(savedEntity.getId()));
//    }
//
//    @Test
//   	public void getByProductId() {
//        List<ScreeningEntity> entityList = repository.findByMovieId(savedEntity.getMovieId());
//
//        assertThat(entityList, hasSize(1));
//        assertEqualsScreenings(savedEntity, entityList.get(0));
//    }
//
//    @Test(expected = DataIntegrityViolationException.class)
//   	public void duplicateError() {
//        ScreeningEntity entity = new ScreeningEntity(1, 2, "c", new Date(), 200, "l");
//        repository.save(entity);
//    }
//
//    @Test
//   	public void optimisticLockError() {
//        ScreeningEntity entity1 = repository.findById(savedEntity.getId()).get();
//        ScreeningEntity entity2 = repository.findById(savedEntity.getId()).get();
//
//        entity1.setLocation("l1");
//        repository.save(entity1);
//
//        try {
//            entity2.setLocation("l2");
//            repository.save(entity2);
//
//            fail("Expected an OptimisticLockingFailureException");
//        } catch (OptimisticLockingFailureException e) {}
//
//        ScreeningEntity updatedEntity = repository.findById(savedEntity.getId()).get();
//        assertEquals(1, (int)updatedEntity.getVersion());
//        assertEquals("l1", updatedEntity.getLocation());
//    }
//
//    private void assertEqualsScreenings(ScreeningEntity expectedEntity, ScreeningEntity actualEntity) {
//        assertEquals(expectedEntity.getId(),        actualEntity.getId());
//        assertEquals(expectedEntity.getVersion(),   actualEntity.getVersion());
//        assertEquals(expectedEntity.getMovieId(), actualEntity.getMovieId());
//        assertEquals(expectedEntity.getScreeningId(),  actualEntity.getScreeningId());
//        assertEquals(expectedEntity.getScreeningDate(),    actualEntity.getScreeningDate());
//        assertEquals(expectedEntity.getCinemaName(),   actualEntity.getCinemaName());
//        assertEquals(expectedEntity.getLocation(),   actualEntity.getLocation());
//        assertEquals(expectedEntity.getPrice(),   actualEntity.getPrice(), 0);
//    }
//}