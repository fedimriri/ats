package com.tekup.ats.service;

import com.tekup.ats.config.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingService {
    
    private final FileStorageConfig fileStorageConfig;
    
    public String saveFile(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(fileStorageConfig.getUploadDir());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String storedFilename = UUID.randomUUID().toString() + "." + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return storedFilename;
    }
    
    public String extractTextFromFile(String filePath) throws IOException {
        File file = new File(fileStorageConfig.getUploadDir(), filePath);
        String extension = getFileExtension(filePath).toLowerCase();
        
        switch (extension) {
            case "pdf":
                return extractTextFromPdf(file);
            case "doc":
                return extractTextFromDoc(file);
            case "docx":
                return extractTextFromDocx(file);
            default:
                throw new IllegalArgumentException("Unsupported file type: " + extension);
        }
    }
    
    private String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    private String extractTextFromDoc(File file) throws IOException {
        try (HWPFDocument document = new HWPFDocument(Files.newInputStream(file.toPath()));
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        }
    }
    
    private String extractTextFromDocx(File file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(Files.newInputStream(file.toPath()));
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }
    
    public boolean isValidFileType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return fileStorageConfig.getAllowedExtensions().contains(extension);
    }
    
    public boolean isValidFileSize(long fileSize) {
        return fileSize <= fileStorageConfig.getMaxFileSize();
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
    
    public void deleteFile(String filename) {
        try {
            Path filePath = Paths.get(fileStorageConfig.getUploadDir(), filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", filename, e);
        }
    }
}
