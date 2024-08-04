package se.magnus.microservices.core.movie.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.movie.Movie;
import se.magnus.microservices.core.movie.persistence.MovieEntity;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    Movie entityToApi(MovieEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    MovieEntity apiToEntity(Movie api);
}
