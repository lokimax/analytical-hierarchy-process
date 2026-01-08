package de.x132.ahp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeResponse {
  private Long id;
  private String name;
  private String beschreibung;
  private String content;
}
