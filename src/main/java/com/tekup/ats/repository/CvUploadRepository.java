package com.tekup.ats.repository;

import com.tekup.ats.entity.CvUpload;
import com.tekup.ats.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvUploadRepository extends JpaRepository<CvUpload, Long> {
    
    List<CvUpload> findByUserOrderByCreatedAtDesc(User user);
    
    List<CvUpload> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<CvUpload> findByIdAndUser(Long id, User user);
    
    List<CvUpload> findByProcessingStatus(CvUpload.ProcessingStatus status);
}
