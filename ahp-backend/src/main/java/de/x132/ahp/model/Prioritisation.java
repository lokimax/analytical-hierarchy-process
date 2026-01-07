package de.x132.ahp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity for prioritization.
 *
 * @author Max Wick
 */
@Entity
@Table(name = "prioritisation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "name"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"project", "comparisons"})
public class Prioritisation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prioritisation_seq")
    @SequenceGenerator(name = "prioritisation_seq", sequenceName = "prioritisation_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false)
    private SolvingMethod method;

    @OneToMany(mappedBy = "prioritisation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comparison> comparisons = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
