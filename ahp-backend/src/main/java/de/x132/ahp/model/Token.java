package de.x132.ahp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

/**
 * JPA Entity for authentication tokens.
 *
 * @author Max Wick
 */
@Entity
@Table(name = "token")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = "client")
public class Token {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_seq")
  @SequenceGenerator(name = "token_seq", sequenceName = "token_seq", allocationSize = 1)
  private Long id;

  @Column(name = "token", nullable = false, unique = true, length = 255)
  private String token;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private TokenType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  private Client client;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  /**
   * Checks if the token is expired.
   *
   * @return true if expired, false otherwise
   */
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiresAt);
  }
}
