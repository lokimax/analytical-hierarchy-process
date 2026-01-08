package de.x132.ahp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

  @NotBlank(message = "Project name is required")
  @Size(min = 1, max = 32, message = "Project name must be between 1 and 32 characters")
  private String name;

  @Size(max = 10000, message = "Description must not exceed 10000 characters")
  private String beschreibung;
}
