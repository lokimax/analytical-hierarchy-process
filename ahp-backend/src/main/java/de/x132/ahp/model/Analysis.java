package de.x132.ahp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "analysis")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analysis {
    
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
    
    @Column
    private LocalDateTime completedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
