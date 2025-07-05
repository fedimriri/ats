package com.tekup.ats.repository;

import com.tekup.ats.entity.CvSection;
import com.tekup.ats.entity.CvUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvSectionRepository extends JpaRepository<CvSection, Long> {
    
    List<CvSection> findByCvUploadOrderBySectionOrder(CvUpload cvUpload);
    
    List<CvSection> findByCvUploadIdOrderBySectionOrder(Long cvUploadId);
    
    Optional<CvSection> findByCvUploadAndSectionType(CvUpload cvUpload, CvSection.SectionType sectionType);
    
    List<CvSection> findByCvUploadAndIsCompleteFalse(CvUpload cvUpload);
}
