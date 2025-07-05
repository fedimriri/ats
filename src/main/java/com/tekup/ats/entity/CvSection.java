package com.tekup.ats.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cv_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvSection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_upload_id", nullable = false)
    private CvUpload cvUpload;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "section_type", nullable = false)
    private SectionType sectionType;
    
    @Column(name = "section_title")
    private String sectionTitle;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "original_content", columnDefinition = "TEXT")
    private String originalContent;
    
    @Column(name = "is_complete")
    private Boolean isComplete = false;
    
    @Column(name = "missing_fields", columnDefinition = "TEXT")
    private String missingFields;
    
    @Column(name = "section_order")
    private Integer sectionOrder;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum SectionType {
        CONTACT_INFO,
        PROFESSIONAL_SUMMARY,
        WORK_EXPERIENCE,
        EDUCATION,
        SKILLS,
        CERTIFICATIONS,
        PROJECTS,
        LANGUAGES,
        ACHIEVEMENTS,
        REFERENCES,
        OTHER
    }
}
