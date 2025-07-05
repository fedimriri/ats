package com.tekup.ats.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    
    private boolean success;
    private String message;
    private Long cvUploadId;
    private String filename;
    private Long fileSize;
    private String processingStatus;
}
