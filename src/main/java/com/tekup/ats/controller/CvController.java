package com.tekup.ats.controller;

import com.tekup.ats.dto.CvUploadDto;
import com.tekup.ats.dto.FileUploadResponse;
import com.tekup.ats.service.CvService;
import com.tekup.ats.service.DocumentGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
@Slf4j
public class CvController {

    private final CvService cvService;
    private final DocumentGenerationService documentGenerationService;
    
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadCv(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return ResponseEntity.badRequest()
                .body(new FileUploadResponse(false, "Invalid session", null, null, null, null));
        }
        
        FileUploadResponse response = cvService.uploadCv(file, sessionId);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<CvUploadDto>> getUserCvs(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<CvUploadDto> cvs = cvService.getUserCvs(sessionId);
        return ResponseEntity.ok(cvs);
    }
    
    @GetMapping("/{cvId}/status")
    public ResponseEntity<CvUploadDto> getCvStatus(
            @PathVariable Long cvId,
            HttpSession session) {
        
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        CvUploadDto cvStatus = cvService.getCvStatus(cvId, sessionId);
        if (cvStatus != null) {
            return ResponseEntity.ok(cvStatus);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{cvId}")
    public ResponseEntity<Void> deleteCv(
            @PathVariable Long cvId,
            HttpSession session) {

        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return ResponseEntity.badRequest().build();
        }

        cvService.deleteCv(cvId, sessionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{cvId}/download")
    public ResponseEntity<Resource> downloadOptimizedCv(
            @PathVariable Long cvId,
            @RequestParam(defaultValue = "pdf") String format,
            @RequestParam(defaultValue = "modern") String template,
            HttpSession session) {

        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Validate format
            if (!format.equalsIgnoreCase("pdf") && !format.equalsIgnoreCase("docx")) {
                return ResponseEntity.badRequest().build();
            }

            // Validate template
            DocumentGenerationService.CVTemplate cvTemplate;
            try {
                cvTemplate = DocumentGenerationService.CVTemplate.valueOf(template.toUpperCase());
            } catch (IllegalArgumentException e) {
                cvTemplate = DocumentGenerationService.CVTemplate.MODERN; // Default fallback
            }

            // Get CV data and generate document
            byte[] documentBytes = cvService.generateOptimizedDocument(cvId, sessionId, format, cvTemplate);

            if (documentBytes == null) {
                return ResponseEntity.notFound().build();
            }

            // Prepare response
            ByteArrayResource resource = new ByteArrayResource(documentBytes);

            String filename = "optimized_cv_" + cvId + "." + format.toLowerCase();
            String contentType = format.equalsIgnoreCase("pdf") ?
                "application/pdf" : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);

        } catch (Exception e) {
            log.error("Error generating document for CV ID: {}", cvId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
