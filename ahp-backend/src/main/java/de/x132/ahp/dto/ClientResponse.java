package de.x132.ahp.dto;

import de.x132.ahp.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private Long id;
    private String nickname;
    private String name;
    private String surename;
    private String email;
    private UserStatus status;
}
