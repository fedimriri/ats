package com.tekup.ats.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cv_uploads")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvUpload {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    
    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;
    
    @Column(name = "file_path", nullable = false)
    private String filePath;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "file_type")
    private String fileType;
    
    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status")
    private ProcessingStatus processingStatus = ProcessingStatus.UPLOADED;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "cvUpload", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CvSection> cvSections;
    
    @OneToMany(mappedBy = "cvUpload", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OptimizationResult> optimizationResults;
    
    public enum ProcessingStatus {
        UPLOADED,
        PROCESSING,
        PARSED,
        ANALYZING,
        OPTIMIZING,
        OPTIMIZED,
        PARTIALLY_OPTIMIZED,
        FAILED
    }
}
