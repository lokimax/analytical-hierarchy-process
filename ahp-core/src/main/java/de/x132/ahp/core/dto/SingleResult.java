package de.x132.ahp.core.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single AHP calculation result for a node. This class holds the node name and its
 * calculated priority value.
 *
 * @author Max Wick
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleResult {

  /** Name of the node. */
  private String nodeName;

  /** The calculated priority value for the node. */
  private BigDecimal value;

  /**
   * Creates a new SingleResult with the given node name and value.
   *
   * @param nodeName the name of the node
   * @param value the priority value
   * @return a new SingleResult instance
   */
  public static SingleResult of(String nodeName, BigDecimal value) {
    return new SingleResult(nodeName, value);
  }
}
