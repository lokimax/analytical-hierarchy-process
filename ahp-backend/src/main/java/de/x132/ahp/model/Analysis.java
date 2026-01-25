package de.x132.ahp.model;

import de.x132.ahp.security.Ownable;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

/**
 * JPA Entity for Analysis results.
 *
 * @author Max Wick
 */
@Entity
@Table(
    name = "analysis",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "name"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"project"})
@Audited
public class Analysis implements Ownable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analysis_seq")
  @SequenceGenerator(name = "analysis_seq", sequenceName = "analysis_seq", allocationSize = 1)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String beschreibung;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Column(columnDefinition = "TEXT")
  private String criteriaComparisons; // JSON

  @Column(columnDefinition = "TEXT")
  private String alternativeComparisons; // JSON

  @Column(columnDefinition = "TEXT")
  private String results; // JSON with weights and final scores

  @Column private LocalDateTime completedAt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Override
  public Client getClient() {
    return project.getClient();
  }
}
