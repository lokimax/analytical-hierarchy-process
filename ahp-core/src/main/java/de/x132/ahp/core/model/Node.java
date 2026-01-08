package de.x132.ahp.core.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a node in the AHP hierarchy. A node can have multiple children and parent nodes.
 *
 * @author Max Wick
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Node {

  /** Unique identifier of the node. */
  private Long id;

  /** Name of the node. */
  private String name;

  /** Description of the node. */
  private String description;

  /** List of child nodes. */
  @Builder.Default private List<Node> children = new ArrayList<>();

  /** List of parent nodes. */
  @Builder.Default private List<Node> parents = new ArrayList<>();

  /**
   * Adds a child node to this node.
   *
   * @param child the child node to add
   */
  public void addChild(Node child) {
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(child);
  }

  /**
   * Adds a parent node to this node.
   *
   * @param parent the parent node to add
   */
  public void addParent(Node parent) {
    if (this.parents == null) {
      this.parents = new ArrayList<>();
    }
    this.parents.add(parent);
  }
}
