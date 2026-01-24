package de.x132.ahp.mapper;

import de.x132.ahp.dto.AuthResponse;
import de.x132.ahp.dto.ClientRegistrationRequest;
import de.x132.ahp.dto.ClientResponse;
import de.x132.ahp.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true) // Set by service or default
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "activationCode", ignore = true)
  @Mapping(target = "projects", ignore = true)
  @Mapping(target = "tokens", ignore = true)
  Client toEntity(ClientRegistrationRequest request);

  ClientResponse toResponse(Client client);

  @Mapping(target = "token", source = "token")
  @Mapping(target = "nickname", source = "client.nickname")
  @Mapping(target = "name", source = "client.name")
  @Mapping(target = "surename", source = "client.surename")
  @Mapping(target = "email", source = "client.email")
  AuthResponse toAuthResponse(Client client, String token);
}
