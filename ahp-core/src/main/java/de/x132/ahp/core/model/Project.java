package de.x132.ahp.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an AHP project containing nodes and their relationships.
 *
 * @author Max Wick
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    /**
     * Unique identifier of the project.
     */
    private Long id;

    /**
     * Name of the project.
     */
    private String name;

    /**
     * Description of the project.
     */
    private String description;

    /**
     * All nodes in this project.
     */
    @Builder.Default
    private List<Node> nodes = new ArrayList<>();

    /**
     * Adds a node to this project.
     *
     * @param node the node to add
     */
    public void addNode(Node node) {
        if (this.nodes == null) {
            this.nodes = new ArrayList<>();
        }
        this.nodes.add(node);
    }

    /**
     * Finds a node by its name.
     *
     * @param name the name to search for
     * @return the node with the given name, or null if not found
     */
    public Node findNodeByName(String name) {
        return nodes.stream()
                .filter(n -> n.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
