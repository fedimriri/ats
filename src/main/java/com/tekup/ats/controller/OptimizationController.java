package com.tekup.ats.controller;

import com.tekup.ats.dto.CvSectionDto;
import com.tekup.ats.dto.OptimizationResultDto;
import com.tekup.ats.entity.CvUpload;
import com.tekup.ats.entity.CvSection;
import com.tekup.ats.entity.OptimizationResult;
import com.tekup.ats.service.CvService;
import com.tekup.ats.service.CvOptimizationService;
import com.tekup.ats.repository.CvSectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/optimization")
@RequiredArgsConstructor
@Slf4j
public class OptimizationController {
    
    private final CvService cvService;
    private final CvOptimizationService cvOptimizationService;
    private final CvSectionRepository cvSectionRepository;
    
    @GetMapping("/{cvId}/sections")
    public ResponseEntity<List<CvSectionDto>> getCvSections(
            @PathVariable Long cvId,
            HttpSession session) {
        
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<CvUpload> cvUpload = cvService.getCvById(cvId, sessionId);
        if (cvUpload.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<CvSection> sections = cvSectionRepository.findByCvUploadOrderBySectionOrder(cvUpload.get());
        List<CvSectionDto> sectionDtos = sections.stream()
            .map(this::convertSectionToDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(sectionDtos);
    }
    
    @GetMapping("/{cvId}/results")
    public ResponseEntity<List<OptimizationResultDto>> getOptimizationResults(
            @PathVariable Long cvId,
            HttpSession session) {
        
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<CvUpload> cvUpload = cvService.getCvById(cvId, sessionId);
        if (cvUpload.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<OptimizationResult> results = cvOptimizationService.getOptimizationResults(cvUpload.get());
        List<OptimizationResultDto> resultDtos = results.stream()
            .map(this::convertResultToDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(resultDtos);
    }
    
    @PostMapping("/{cvId}/optimize")
    public ResponseEntity<String> requestOptimization(
            @PathVariable Long cvId,
            HttpSession session) {
        
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return ResponseEntity.badRequest().body("Invalid session");
        }
        
        Optional<CvUpload> cvUpload = cvService.getCvById(cvId, sessionId);
        if (cvUpload.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        cvOptimizationService.requestOptimization(cvId, sessionId);
        return ResponseEntity.ok("Optimization started");
    }
    
    private CvSectionDto convertSectionToDto(CvSection section) {
        return new CvSectionDto(
            section.getId(),
            section.getSectionType(),
            section.getSectionTitle(),
            section.getContent(),
            section.getOriginalContent(),
            section.getIsComplete(),
            section.getMissingFields(),
            section.getSectionOrder()
        );
    }
    
    private OptimizationResultDto convertResultToDto(OptimizationResult result) {
        return new OptimizationResultDto(
            result.getId(),
            result.getOptimizationType(),
            result.getOriginalContent(),
            result.getOptimizedContent(),
            result.getSuggestions(),
            result.getKeywordsAdded(),
            result.getAtsScoreBefore(),
            result.getAtsScoreAfter(),
            result.getImprovements(),
            result.getAiAnalysis(),
            result.getStatus(),
            result.getCreatedAt()
        );
    }
}
