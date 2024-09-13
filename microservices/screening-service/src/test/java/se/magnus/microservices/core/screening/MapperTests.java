package se.magnus.microservices.core.screening;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import se.magnus.api.core.screening.Screening;
import se.magnus.microservices.core.screening.persistence.ScreeningEntity;
import se.magnus.microservices.core.screening.services.ScreeningMapper;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private ScreeningMapper mapper = Mappers.getMapper(ScreeningMapper.class);


    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Screening api = new Screening(1, 2, "cn", new Date(), 6, "l", "mock address");

        ScreeningEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getMovieId(), entity.getMovieId());
        assertEquals(api.getScreeningId(), entity.getScreeningId());
        assertEquals(api.getCinemaName(), entity.getCinemaName());
        assertEquals(api.getScreeningDate(), entity.getScreeningDate());
//        assertEquals(api.getPrice(), entity.getPrice());
        assertEquals(api.getLocation(), entity.getLocation());

        Screening api2 = mapper.entityToApi(entity);

        assertEquals(api.getMovieId(), api2.getMovieId());
        assertEquals(api.getScreeningId(), api2.getScreeningId());
        assertEquals(api.getCinemaName(), api2.getCinemaName());
        assertEquals(api.getScreeningDate(), api2.getScreeningDate());
        assertEquals(api.getPrice(), api2.getPrice(), 0);
        assertEquals(api.getLocation(), api2.getLocation());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        Screening api = new Screening(1, 3, "cn", new Date(), 6, "l", "mock address");
        List<Screening> apiList = Collections.singletonList(api);

        List<ScreeningEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        ScreeningEntity entity = entityList.get(0);

        assertEquals(api.getMovieId(), entity.getMovieId());
        assertEquals(api.getScreeningId(), entity.getScreeningId());
        assertEquals(api.getCinemaName(), entity.getCinemaName());
        assertEquals(api.getScreeningDate(), entity.getScreeningDate());
        assertEquals(api.getPrice(), entity.getPrice(), 0);
        assertEquals(api.getLocation(), entity.getLocation());

        List<Screening> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Screening api2 = api2List.get(0);

        assertEquals(api.getMovieId(), api2.getMovieId());
        assertEquals(api.getScreeningId(), api2.getScreeningId());
        assertEquals(api.getCinemaName(), api2.getCinemaName());
        assertEquals(api.getScreeningDate(), api2.getScreeningDate());
//        assertEquals(api.getPrice(), api2.getPrice());
        assertEquals(api.getLocation(), api2.getLocation());
        assertNull(api2.getServiceAddress());
    }
}
