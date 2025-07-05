package com.tekup.ats.service;

import com.tekup.ats.entity.CvUpload;
import com.tekup.ats.repository.CvUploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncCvProcessingService {
    
    private final CvUploadRepository cvUploadRepository;
    private final FileProcessingService fileProcessingService;
    private final CvParsingService cvParsingService;
    private final CvOptimizationService cvOptimizationService;
    
    @Async
    @Transactional
    public void processCvAsync(CvUpload cvUpload) {
        log.info("Starting async CV processing for upload ID: {}", cvUpload.getId());
        
        try {
            // Add a small delay to demonstrate async processing
            Thread.sleep(2000);
            
            // Set status to PROCESSING
            cvUpload.setProcessingStatus(CvUpload.ProcessingStatus.PROCESSING);
            cvUploadRepository.save(cvUpload);
            log.info("CV {} status updated to PROCESSING", cvUpload.getId());
            
            // Extract text from file
            String extractedText = fileProcessingService.extractTextFromFile(cvUpload.getStoredFilename());
            cvUpload.setExtractedText(extractedText);
            
            // Parse CV sections
            cvParsingService.parseCvText(extractedText, cvUpload);

            cvUpload.setProcessingStatus(CvUpload.ProcessingStatus.PARSED);
            cvUploadRepository.save(cvUpload);
            log.info("CV {} status updated to PARSED", cvUpload.getId());

            // Start optimization process
            cvOptimizationService.optimizeCv(cvUpload);
            
            log.info("Async CV processing completed for upload ID: {}", cvUpload.getId());
            
        } catch (Exception e) {
            log.error("Error processing CV asynchronously for upload ID: {}", cvUpload.getId(), e);
            cvUpload.setProcessingStatus(CvUpload.ProcessingStatus.FAILED);
            cvUpload.setErrorMessage("Async processing failed: " + e.getMessage());
            cvUploadRepository.save(cvUpload);
        }
    }
}
