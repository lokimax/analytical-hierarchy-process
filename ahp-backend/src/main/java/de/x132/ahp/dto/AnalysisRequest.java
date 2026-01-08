package de.x132.ahp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequest {
  @NotBlank(message = "Name is required")
  private String name;

  private String beschreibung;
  private String criteriaComparisons;
  private String alternativeComparisons;
  private String results;
}
