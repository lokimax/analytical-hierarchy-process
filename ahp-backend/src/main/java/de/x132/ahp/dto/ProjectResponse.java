package de.x132.ahp.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
