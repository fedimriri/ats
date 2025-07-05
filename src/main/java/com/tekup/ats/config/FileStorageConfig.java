package com.tekup.ats.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.file")
@Data
public class FileStorageConfig {
    
    private String uploadDir = "./uploads";
    private List<String> allowedExtensions = List.of("pdf", "doc", "docx");
    private long maxFileSize = 10 * 1024 * 1024; // 10MB
}
