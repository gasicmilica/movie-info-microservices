package se.magnus.microservices.core.rating;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import se.magnus.api.core.rating.Rating;
import se.magnus.microservices.core.rating.persistence.RatingEntity;
import se.magnus.microservices.core.rating.services.RatingMapper;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private RatingMapper mapper = Mappers.getMapper(RatingMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Rating api = new Rating(1, 1, "author", new Date(), 6, "mock address");

        RatingEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getMovieId(), entity.getMovieId());
        assertEquals(api.getRatingId(), entity.getRatingId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getRatingDate(), entity.getRatingDate());
        assertEquals(api.getRatingNumber(), entity.getRatingNumber());

        Rating api2 = mapper.entityToApi(entity);

        assertEquals(api.getMovieId(), api2.getMovieId());
        assertEquals(api.getRatingId(), api2.getRatingId());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getRatingDate(), api2.getRatingDate());
        assertEquals(api.getRatingNumber(), api2.getRatingNumber());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        Rating api = new Rating(1, 1,"author", new Date(), 5, "mock address");

        List<Rating> apiList = Collections.singletonList(api);

        List<RatingEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        RatingEntity entity = entityList.get(0);

        assertEquals(api.getMovieId(), entity.getMovieId());
        assertEquals(api.getRatingId(), entity.getRatingId());
        assertEquals(api.getAuthor(), entity.getAuthor());
        assertEquals(api.getRatingDate(), entity.getRatingDate());
        assertEquals(api.getRatingNumber(), entity.getRatingNumber());

        List<Rating> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Rating api2 = api2List.get(0);

        assertEquals(api.getMovieId(), api2.getMovieId());
        assertEquals(api.getRatingId(), api2.getRatingId());
        assertEquals(api.getAuthor(), api2.getAuthor());
        assertEquals(api.getRatingDate(), api2.getRatingDate());
        assertEquals(api.getRatingNumber(), api2.getRatingNumber());
        assertNull(api2.getServiceAddress());
    }
}
