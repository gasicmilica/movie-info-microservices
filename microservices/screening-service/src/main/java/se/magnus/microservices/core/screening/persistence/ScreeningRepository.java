package se.magnus.microservices.core.screening.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ScreeningRepository extends PagingAndSortingRepository<ScreeningEntity, Integer> {

    @Transactional(readOnly = true)
    List<ScreeningEntity> findByMovieId(int movieId);
}
