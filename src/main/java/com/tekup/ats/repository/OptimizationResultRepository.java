package com.tekup.ats.repository;

import com.tekup.ats.entity.OptimizationResult;
import com.tekup.ats.entity.CvUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptimizationResultRepository extends JpaRepository<OptimizationResult, Long> {
    
    List<OptimizationResult> findByCvUploadOrderByCreatedAtDesc(CvUpload cvUpload);
    
    List<OptimizationResult> findByCvUploadIdOrderByCreatedAtDesc(Long cvUploadId);
    
    List<OptimizationResult> findByStatus(OptimizationResult.OptimizationStatus status);
}
