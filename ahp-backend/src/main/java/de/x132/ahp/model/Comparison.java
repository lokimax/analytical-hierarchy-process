package de.x132.ahp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for pairwise comparison.
 *
 * @author Max Wick
 */
@Entity
@Table(name = "comparison")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"prioritisation", "parent", "leftNode", "rightNode"})
public class Comparison implements Comparable<Comparison> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comparison_seq")
    @SequenceGenerator(name = "comparison_seq", sequenceName = "comparison_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prioritisation_id", nullable = false)
    private Prioritisation prioritisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_node_id", nullable = false)
    private Node parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "left_node_id", nullable = false)
    private Node leftNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "right_node_id", nullable = false)
    private Node rightNode;

    @Column(name = "weight", nullable = false, precision = 10, scale = 2)
    private BigDecimal weight;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public int compareTo(Comparison o) {
        int compareToParent = this.parent.getName().compareTo(o.getParent().getName());
        if (compareToParent != 0) {
            return compareToParent;
        }

        int compareToLeft = this.leftNode.getName().compareTo(o.getLeftNode().getName());
        if (compareToLeft != 0) {
            return compareToLeft;
        }

        return this.rightNode.getName().compareTo(o.getRightNode().getName());
    }
}
