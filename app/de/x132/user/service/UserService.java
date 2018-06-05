package de.x132.user.service;

import java.util.List;
import java.util.Optional;

import play.mvc.Http.Request;
import de.x132.common.exceptionhandling.NotFoundException;
import de.x132.user.models.Client;
import de.x132.user.transfer.UserDTO;

public interface UserService {

    Optional<UserDTO> delete(String nickname) throws NotFoundException;

    void activate(String activationcode);

    Optional<UserDTO> getUser(String nickname);

    Optional<UserDTO> create(UserDTO user);

    Optional<UserDTO> getUser(Request request);

    List<UserDTO> getUsers(int page, int count);

    Optional<UserDTO> update(UserDTO user) throws NotFoundException;
    
    Optional<Client> getClient(Request request);
}
