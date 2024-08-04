package se.magnus.microservices.core.screening.services;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.screening.Screening;
import se.magnus.api.core.screening.ScreeningService;
import se.magnus.microservices.core.screening.persistence.ScreeningEntity;
import se.magnus.microservices.core.screening.persistence.ScreeningRepository;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;
import java.util.function.Supplier;

import static java.util.logging.Level.FINE;

@RestController
public class ScreeningServiceImpl implements ScreeningService {
    private final ServiceUtil serviceUtil;
    private static final Logger LOG = LoggerFactory.getLogger(ScreeningServiceImpl.class);
    private final ScreeningRepository repository;
    private final ScreeningMapper mapper;

    @Autowired
    public ScreeningServiceImpl(ServiceUtil serviceUtil, ScreeningRepository repository, ScreeningMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public List<Screening> getScreenings(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        List<ScreeningEntity> entityList = repository.findByMovieId(movieId);
        List<Screening> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getScreenings: response size: {}", list.size());

        return list;
    }

    @Override
    public Screening createScreening(Screening body) {
        if (body.getMovieId() < 1) throw new InvalidInputException("Invalid movieId: " + body.getMovieId());

        try {
            ScreeningEntity entity = mapper.apiToEntity(body);
            ScreeningEntity newEntity = repository.save(entity);

            LOG.debug("createScreening: created a screening entity: {}/{}", body.getMovieId(), body.getScreeningId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Movie Id: " + body.getMovieId() + ", Screening Id:" + body.getScreeningId());
        }
    }

    @Override
    public void deleteScreenings(int movieId) {
        if (movieId < 1) throw new InvalidInputException("Invalid movieId: " + movieId);

        LOG.debug("deleteScreenings: tries to delete screenings for the product with movieId: {}", movieId);
        repository.deleteAll(repository.findByMovieId(movieId));
    }
}
