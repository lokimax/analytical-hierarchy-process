package de.x132.ahp.mapper;

import de.x132.ahp.dto.ProjectRequest;
import de.x132.ahp.dto.ProjectResponse;
import de.x132.ahp.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "client", ignore = true)
  @Mapping(target = "nodes", ignore = true)
  @Mapping(target = "connections", ignore = true)
  @Mapping(target = "prioritisations", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Project toEntity(ProjectRequest request);

  @Mapping(target = "clientNickname", source = "client.nickname")
  ProjectResponse toResponse(Project project);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "client", ignore = true)
  @Mapping(target = "name", ignore = true)
  @Mapping(target = "nodes", ignore = true)
  @Mapping(target = "connections", ignore = true)
  @Mapping(target = "prioritisations", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateProjectFromRequest(ProjectRequest request, @MappingTarget Project project);
}
