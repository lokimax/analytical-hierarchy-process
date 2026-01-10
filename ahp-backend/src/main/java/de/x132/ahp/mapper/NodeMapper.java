package de.x132.ahp.mapper;

import de.x132.ahp.dto.NodeRequest;
import de.x132.ahp.dto.NodeResponse;
import de.x132.ahp.model.Node;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NodeMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "project", ignore = true)
  @Mapping(target = "outgoing", ignore = true)
  @Mapping(target = "ingoing", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Node toEntity(NodeRequest request);

  NodeResponse toResponse(Node node);
}
