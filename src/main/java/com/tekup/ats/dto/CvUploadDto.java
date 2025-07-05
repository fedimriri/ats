package com.tekup.ats.dto;

import com.tekup.ats.entity.CvUpload;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvUploadDto {
    
    private Long id;
    private String originalFilename;
    private String fileType;
    private Long fileSize;
    private CvUpload.ProcessingStatus processingStatus;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
