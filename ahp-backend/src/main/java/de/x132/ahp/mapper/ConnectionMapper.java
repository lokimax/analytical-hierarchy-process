package de.x132.ahp.mapper;

import de.x132.ahp.dto.ConnectionRequest;
import de.x132.ahp.dto.ConnectionResponse;
import de.x132.ahp.model.Connection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConnectionMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  @Mapping(target = "sourceNode", ignore = true) // Set manually or via helper
  @Mapping(target = "targetNode", ignore = true) // Set manually or via helper
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Connection toEntity(ConnectionRequest request);

  @Mapping(target = "projectId", source = "project.id")
  @Mapping(target = "sourceNodeName", source = "sourceNode.name")
  @Mapping(target = "targetNodeName", source = "targetNode.name")
  ConnectionResponse toResponse(Connection connection);
}
