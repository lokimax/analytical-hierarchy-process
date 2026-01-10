package de.x132.ahp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a critical point where the ranking changes in sensitivity analysis.
 *
 * @author Max Wick
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriticalPoint {
  /** The criterion weight threshold where ranking changes (0.0 - 1.0) */
  private double weightThreshold;

  /** Alternative ID that was winning before this threshold */
  private Long beforeWinnerId;

  /** Alternative name that was winning before this threshold */
  private String beforeWinnerName;

  /** Alternative ID that wins after this threshold */
  private Long afterWinnerId;

  /** Alternative name that wins after this threshold */
  private String afterWinnerName;

  /** Human-readable description of the change */
  private String description;
}
