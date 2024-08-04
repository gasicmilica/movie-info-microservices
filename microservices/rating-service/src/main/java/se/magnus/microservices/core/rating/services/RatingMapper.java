package se.magnus.microservices.core.rating.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.rating.Rating;
import se.magnus.microservices.core.rating.persistence.RatingEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    Rating entityToApi(RatingEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    RatingEntity apiToEntity(Rating api);

    List<Rating> entityListToApiList(List<RatingEntity> entity);
    List<RatingEntity> apiListToEntityList(List<Rating> api);
}