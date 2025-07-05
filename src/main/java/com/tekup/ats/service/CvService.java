package com.tekup.ats.service;

import com.tekup.ats.dto.CvUploadDto;
import com.tekup.ats.dto.FileUploadResponse;
import com.tekup.ats.entity.CvUpload;
import com.tekup.ats.entity.CvSection;
import com.tekup.ats.entity.User;
import com.tekup.ats.repository.CvUploadRepository;
import com.tekup.ats.repository.CvSectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CvService {
    
    private final CvUploadRepository cvUploadRepository;
    private final CvSectionRepository cvSectionRepository;
    private final FileProcessingService fileProcessingService;
    private final DocumentGenerationService documentGenerationService;
    private final UserService userService;
    private final AsyncCvProcessingService asyncCvProcessingService;
    
    public FileUploadResponse uploadCv(MultipartFile file, String sessionId) {
        try {
            // Validate file
            if (!fileProcessingService.isValidFileType(file.getOriginalFilename())) {
                return new FileUploadResponse(false, "Invalid file type. Only PDF, DOC, and DOCX files are allowed.", 
                    null, null, null, null);
            }
            
            if (!fileProcessingService.isValidFileSize(file.getSize())) {
                return new FileUploadResponse(false, "File size exceeds maximum limit of 10MB.", 
                    null, null, null, null);
            }
            
            // Get or create user
            User user = userService.getOrCreateUserBySessionId(sessionId);
            
            // Save file
            String storedFilename = fileProcessingService.saveFile(file);
            
            // Create CV upload record
            CvUpload cvUpload = new CvUpload();
            cvUpload.setUser(user);
            cvUpload.setOriginalFilename(file.getOriginalFilename());
            cvUpload.setStoredFilename(storedFilename);
            cvUpload.setFilePath(storedFilename);
            cvUpload.setFileSize(file.getSize());
            cvUpload.setFileType(file.getContentType());
            cvUpload.setProcessingStatus(CvUpload.ProcessingStatus.UPLOADED);
            
            cvUpload = cvUploadRepository.save(cvUpload);
            
            // Process file asynchronously
            asyncCvProcessingService.processCvAsync(cvUpload);
            
            return new FileUploadResponse(true, "File uploaded successfully", 
                cvUpload.getId(), file.getOriginalFilename(), file.getSize(), 
                cvUpload.getProcessingStatus().toString());
                
        } catch (Exception e) {
            log.error("Error uploading CV", e);
            return new FileUploadResponse(false, "Error uploading file: " + e.getMessage(), 
                null, null, null, null);
        }
    }
    

    
    public List<CvUploadDto> getUserCvs(String sessionId) {
        User user = userService.getUserBySessionId(sessionId);
        if (user == null) {
            return List.of();
        }
        
        return cvUploadRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    public Optional<CvUpload> getCvById(Long id, String sessionId) {
        User user = userService.getUserBySessionId(sessionId);
        if (user == null) {
            return Optional.empty();
        }
        
        return cvUploadRepository.findByIdAndUser(id, user);
    }
    
    public CvUploadDto getCvStatus(Long cvId, String sessionId) {
        Optional<CvUpload> cvUpload = getCvById(cvId, sessionId);
        return cvUpload.map(this::convertToDto).orElse(null);
    }
    
    public void deleteCv(Long cvId, String sessionId) {
        Optional<CvUpload> cvUpload = getCvById(cvId, sessionId);
        if (cvUpload.isPresent()) {
            CvUpload cv = cvUpload.get();

            // Delete physical file
            fileProcessingService.deleteFile(cv.getStoredFilename());

            // Delete database record
            cvUploadRepository.delete(cv);
        }
    }

    public byte[] generateOptimizedDocument(Long cvId, String sessionId, String format,
                                          DocumentGenerationService.CVTemplate template) {
        try {
            // Get CV upload
            Optional<CvUpload> cvUploadOpt = getCvById(cvId, sessionId);
            if (cvUploadOpt.isEmpty()) {
                log.warn("CV not found for ID: {} and session: {}", cvId, sessionId);
                return null;
            }

            CvUpload cvUpload = cvUploadOpt.get();

            // Check if CV is optimized
            if (cvUpload.getProcessingStatus() != CvUpload.ProcessingStatus.OPTIMIZED) {
                log.warn("CV not yet optimized for ID: {}, status: {}", cvId, cvUpload.getProcessingStatus());
                return null;
            }

            // Get optimized CV sections
            List<CvSection> optimizedSections = cvSectionRepository.findByCvUploadIdOrderBySectionOrder(cvId);

            if (optimizedSections.isEmpty()) {
                log.warn("No CV sections found for CV ID: {}", cvId);
                return null;
            }

            // Generate document based on format
            if ("pdf".equalsIgnoreCase(format)) {
                return documentGenerationService.generateOptimizedPdf(cvUpload, optimizedSections, template);
            } else if ("docx".equalsIgnoreCase(format)) {
                return documentGenerationService.generateOptimizedDocx(cvUpload, optimizedSections, template);
            } else {
                log.error("Unsupported format: {}", format);
                return null;
            }

        } catch (Exception e) {
            log.error("Error generating optimized document for CV ID: {}", cvId, e);
            return null;
        }
    }
    
    private CvUploadDto convertToDto(CvUpload cvUpload) {
        return new CvUploadDto(
            cvUpload.getId(),
            cvUpload.getOriginalFilename(),
            cvUpload.getFileType(),
            cvUpload.getFileSize(),
            cvUpload.getProcessingStatus(),
            cvUpload.getErrorMessage(),
            cvUpload.getCreatedAt(),
            cvUpload.getUpdatedAt()
        );
    }
}
