package de.x132.ahp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String name;
    private String beschreibung;
    private String clientNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
