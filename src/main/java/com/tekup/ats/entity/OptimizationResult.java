package com.tekup.ats.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "optimization_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_upload_id", nullable = false)
    private CvUpload cvUpload;
    
    @Column(name = "optimization_type", nullable = false)
    private String optimizationType;
    
    @Column(name = "original_content", columnDefinition = "LONGTEXT")
    private String originalContent;
    
    @Column(name = "optimized_content", columnDefinition = "LONGTEXT")
    private String optimizedContent;
    
    @Column(name = "suggestions", columnDefinition = "TEXT")
    private String suggestions;
    
    @Column(name = "keywords_added", columnDefinition = "TEXT")
    private String keywordsAdded;
    
    @Column(name = "ats_score_before")
    private Integer atsScoreBefore;
    
    @Column(name = "ats_score_after")
    private Integer atsScoreAfter;
    
    @Column(name = "improvements", columnDefinition = "TEXT")
    private String improvements;
    
    @Column(name = "ai_analysis", columnDefinition = "LONGTEXT")
    private String aiAnalysis;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OptimizationStatus status = OptimizationStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum OptimizationStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}
