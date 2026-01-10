package de.x132.ahp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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

/**
 * JPA Entity for user/client.
 *
 * @author Max Wick
 */
@Entity
@Table(name = "client")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"projects", "tokens"})
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
  @SequenceGenerator(name = "client_seq", sequenceName = "client_seq", allocationSize = 1)
  private Long id;

  @Column(name = "name", length = 32)
  private String name;

  @Column(name = "nickname", nullable = false, length = 32, unique = true)
  private String nickname;

  @Column(name = "email", nullable = false, length = 120)
  private String email;

  @Column(name = "password", nullable = false, length = 255)
  private String password;

  @Column(name = "surename", nullable = false, length = 32)
  private String surename;

  @Column(name = "activation_code", unique = true, length = 128)
  private String activationCode;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private UserStatus status;

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Project> projects = new ArrayList<>();

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Token> tokens = new ArrayList<>();

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
