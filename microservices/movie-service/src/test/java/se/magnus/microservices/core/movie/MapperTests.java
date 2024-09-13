package se.magnus.microservices.core.movie;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import se.magnus.api.core.movie.Movie;
import se.magnus.microservices.core.movie.persistence.MovieEntity;
import se.magnus.microservices.core.movie.services.MovieMapper;

import static org.junit.Assert.*;

public class MapperTests {

    private MovieMapper mapper = Mappers.getMapper(MovieMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Movie api = new Movie(1,"title", "Director", 2010, 2, "Genre", "mock-address");

        MovieEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getMovieId(), entity.getMovieId());
        assertEquals(api.getTitle(), entity.getTitle());
        assertEquals(api.getDirector(), entity.getDirector());
        assertEquals(api.getReleaseYear(), entity.getReleaseYear());
        assertEquals(api.getDuration(), entity.getDuration());
        assertEquals(api.getGenre(), entity.getGenre());

        Movie api2 = mapper.entityToApi(entity);

        assertEquals(api.getMovieId(), api2.getMovieId());
        assertEquals(api.getTitle(), api2.getTitle());
        assertEquals(api.getDirector(), api2.getDirector());
        assertEquals(api.getReleaseYear(), api2.getReleaseYear());
        assertEquals(api.getDuration(), api2.getDuration());
        assertEquals(api.getGenre(), api2.getGenre());
        assertNull(api2.getServiceAddress());
    }
}
