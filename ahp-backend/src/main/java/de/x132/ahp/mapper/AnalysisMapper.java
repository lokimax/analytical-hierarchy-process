package de.x132.ahp.mapper;

import de.x132.ahp.dto.AnalysisRequest;
import de.x132.ahp.dto.AnalysisResponse;
import de.x132.ahp.model.Analysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AnalysisMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  @Mapping(target = "completedAt", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Analysis toEntity(AnalysisRequest request);

  AnalysisResponse toResponse(Analysis analysis);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  @Mapping(target = "completedAt", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateAnalysisFromRequest(AnalysisRequest request, @MappingTarget Analysis analysis);
}
