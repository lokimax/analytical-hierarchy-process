package de.x132.ahp.model;

import de.x132.ahp.security.Ownable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

/**
 * JPA Entity for AHP node.
 *
 * @author Max Wick
 */
@Entity
@Table(
    name = "node",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "name"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"project", "outgoing", "ingoing"})
@Audited
public class Node implements Ownable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "node_seq")
  @SequenceGenerator(name = "node_seq", sequenceName = "node_seq", allocationSize = 1)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "content", length = 10000)
  private String content;

  @Column(name = "beschreibung", length = 10000)
  private String beschreibung;

  @OneToMany(mappedBy = "sourceNode", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @NotAudited
  private List<Connection> outgoing = new ArrayList<>();

  @OneToMany(mappedBy = "targetNode", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @NotAudited
  private List<Connection> ingoing = new ArrayList<>();

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
