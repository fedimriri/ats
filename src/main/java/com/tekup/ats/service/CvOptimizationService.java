package com.tekup.ats.service;

import com.tekup.ats.entity.CvUpload;
import com.tekup.ats.entity.CvSection;
import com.tekup.ats.entity.OptimizationResult;
import com.tekup.ats.repository.CvSectionRepository;
import com.tekup.ats.repository.CvUploadRepository;
import com.tekup.ats.repository.OptimizationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CvOptimizationService {
    
    private final AiService aiService;
    private final CvSectionRepository cvSectionRepository;
    private final OptimizationResultRepository optimizationResultRepository;
    private final CvUploadRepository cvUploadRepository;
    
    public void optimizeCv(CvUpload cvUpload) {
        try {
            log.info("Starting CV optimization for upload ID: {}", cvUpload.getId());

            // Set status to ANALYZING
            cvUpload.setProcessingStatus(CvUpload.ProcessingStatus.ANALYZING);
            cvUploadRepository.save(cvUpload);

            // Get all sections for this CV
            List<CvSection> sections = cvSectionRepository.findByCvUploadOrderBySectionOrder(cvUpload);

            // Analyze overall CV
            String overallAnalysis = aiService.analyzeCv(cvUpload.getExtractedText());

            // Check if AI analysis failed
            if (overallAnalysis == null || overallAnalysis.startsWith("Error")) {
                throw new RuntimeException("AI analysis failed: " + overallAnalysis);
            }

            // Create overall optimization result
            OptimizationResult overallResult = new OptimizationResult();
            overallResult.setCvUpload(cvUpload);
            overallResult.setOptimizationType("OVERALL_ANALYSIS");
            overallResult.setOriginalContent(cvUpload.getExtractedText());
            overallResult.setAiAnalysis(overallAnalysis);
            overallResult.setStatus(OptimizationResult.OptimizationStatus.COMPLETED);

            optimizationResultRepository.save(overallResult);

            // Set status to OPTIMIZING
            cvUpload.setProcessingStatus(CvUpload.ProcessingStatus.OPTIMIZING);
            cvUploadRepository.save(cvUpload);

            // Track optimization success
            boolean allSectionsOptimized = true;
            int optimizedSections = 0;

            // Optimize each section
            for (CvSection section : sections) {
                boolean sectionOptimized = optimizeSection(section);
                if (sectionOptimized) {
                    optimizedSections++;
                } else {
                    allSectionsOptimized = false;
                }
            }

            // Only mark as OPTIMIZED if AI processing actually succeeded
            if (allSectionsOptimized && optimizedSections > 0) {
                cvUpload.setProcessingStatus(CvUpload.ProcessingStatus.OPTIMIZED);
                log.info("CV optimization completed successfully for upload ID: {} ({} sections optimized)",
                    cvUpload.getId(), optimizedSections);
            } else {
                cvUpload.setProcessingStatus(CvUpload.ProcessingStatus.PARTIALLY_OPTIMIZED);
                cvUpload.setErrorMessage(String.format("Only %d out of %d sections were successfully optimized",
                    optimizedSections, sections.size()));
                log.warn("CV optimization partially failed for upload ID: {} ({}/{} sections optimized)",
                    cvUpload.getId(), optimizedSections, sections.size());
            }

            cvUploadRepository.save(cvUpload);

        } catch (Exception e) {
            log.error("Error optimizing CV for upload ID: {}", cvUpload.getId(), e);

            // Update CV status to failed
            cvUpload.setProcessingStatus(CvUpload.ProcessingStatus.FAILED);
            cvUpload.setErrorMessage("Optimization failed: " + e.getMessage());
            cvUploadRepository.save(cvUpload);
        }
    }
    
    private boolean optimizeSection(CvSection section) {
        try {
            if (section.getContent() == null || section.getContent().trim().isEmpty()) {
                // Handle missing sections
                String suggestions = aiService.generateMissingFieldsSuggestions(
                    section.getSectionType().toString(),
                    ""
                );

                // Check if AI service failed
                if (suggestions == null || suggestions.startsWith("Error")) {
                    log.warn("Failed to generate suggestions for missing section {}: {}",
                        section.getSectionType(), suggestions);
                    section.setMissingFields("Unable to generate suggestions due to AI service error");
                    section.setIsComplete(false);
                    cvSectionRepository.save(section);
                    return false;
                }

                section.setMissingFields(suggestions);
                section.setIsComplete(false);
                cvSectionRepository.save(section);
                return true; // Successfully processed missing section

            } else {
                // Optimize existing content
                String optimizedContent = aiService.optimizeCvSection(
                    section.getContent(),
                    section.getSectionType().toString()
                );

                // Check if AI optimization failed
                if (optimizedContent == null || optimizedContent.startsWith("Error")) {
                    log.warn("Failed to optimize section {}: {}",
                        section.getSectionType(), optimizedContent);
                    section.setIsComplete(false);
                    cvSectionRepository.save(section);
                    return false;
                }

                // Create optimization result
                OptimizationResult result = new OptimizationResult();
                result.setCvUpload(section.getCvUpload());
                result.setOptimizationType("SECTION_" + section.getSectionType().toString());
                result.setOriginalContent(section.getContent());
                result.setOptimizedContent(optimizedContent);
                result.setStatus(OptimizationResult.OptimizationStatus.COMPLETED);

                optimizationResultRepository.save(result);

                // Update section with optimized content
                section.setContent(optimizedContent);
                section.setIsComplete(true);
                cvSectionRepository.save(section);
                return true; // Successfully optimized section
            }

        } catch (Exception e) {
            log.error("Error optimizing section {} for CV {}",
                section.getSectionType(), section.getCvUpload().getId(), e);
            section.setIsComplete(false);
            cvSectionRepository.save(section);
            return false;
        }
    }
    
    public List<OptimizationResult> getOptimizationResults(CvUpload cvUpload) {
        return optimizationResultRepository.findByCvUploadOrderByCreatedAtDesc(cvUpload);
    }
    
    public void requestOptimization(Long cvUploadId, String sessionId) {
        // This would typically be called asynchronously
        // For now, we'll implement it synchronously
        // In production, you'd use @Async or a message queue
        
        // Implementation would go here to trigger optimization
        log.info("Optimization requested for CV upload ID: {} by session: {}", cvUploadId, sessionId);
    }
}
