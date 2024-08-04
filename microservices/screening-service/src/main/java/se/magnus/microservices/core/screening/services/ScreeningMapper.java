package se.magnus.microservices.core.screening.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.screening.Screening;
import se.magnus.microservices.core.screening.persistence.ScreeningEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScreeningMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    Screening entityToApi(ScreeningEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    ScreeningEntity apiToEntity(Screening api);

    List<Screening> entityListToApiList(List<ScreeningEntity> entity);
    List<ScreeningEntity> apiListToEntityList(List<Screening> api);
}