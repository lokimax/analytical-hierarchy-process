package de.x132.ahp.mapper;

import de.x132.ahp.dto.AnalysisRequest;
import de.x132.ahp.dto.AnalysisResponse;
import de.x132.ahp.model.Analysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AnalysisMapper {

  @Autowired protected com.fasterxml.jackson.databind.ObjectMapper objectMapper;

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  @Mapping(target = "completedAt", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract Analysis toEntity(AnalysisRequest request);

  public abstract AnalysisResponse toResponse(Analysis analysis);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  @Mapping(target = "completedAt", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  public abstract void updateAnalysisFromRequest(
      AnalysisRequest request, @MappingTarget Analysis analysis);

  // Custom mapping methods
  protected de.x132.ahp.model.json.AnalysisResult mapToResult(String json) {
    if (json == null) return null;
    try {
      return objectMapper.readValue(json, de.x132.ahp.model.json.AnalysisResult.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid AnalysisResult JSON", e);
    }
  }

  protected String mapToString(de.x132.ahp.model.json.AnalysisResult result) {
    if (result == null) return null;
    try {
      return objectMapper.writeValueAsString(result);
    } catch (Exception e) {
      throw new IllegalArgumentException("Error serializing AnalysisResult", e);
    }
  }
}
