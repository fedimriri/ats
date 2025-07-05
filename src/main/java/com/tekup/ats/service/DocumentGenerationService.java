package com.tekup.ats.service;

import com.tekup.ats.entity.CvUpload;
import com.tekup.ats.entity.CvSection;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DocumentGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentGenerationService.class);
    
    public enum CVTemplate {
        MODERN, CLASSIC, ATS_OPTIMIZED
    }
    
    /**
     * Generate optimized CV in PDF format (using HTML content for now)
     */
    public byte[] generateOptimizedPdf(CvUpload cvUpload, List<CvSection> optimizedSections, CVTemplate template) {
        try {
            // For now, generate HTML content and return as bytes
            // In a production system, you would convert HTML to PDF using libraries like wkhtmltopdf or Flying Saucer
            String htmlContent = generateHtmlContent(cvUpload, optimizedSections, template);

            // Return HTML as bytes (can be enhanced to actual PDF later)
            return htmlContent.getBytes(StandardCharsets.UTF_8);

        } catch (Exception e) {
            logger.error("Error generating PDF for CV ID: {}", cvUpload.getId(), e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    /**
     * Generate HTML content for CV
     */
    private String generateHtmlContent(CvUpload cvUpload, List<CvSection> optimizedSections, CVTemplate template) {
        Map<CvSection.SectionType, CvSection> sectionMap = optimizedSections.stream()
            .collect(Collectors.toMap(CvSection::getSectionType, section -> section));

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>Optimized CV - ").append(cvUpload.getOriginalFilename()).append("</title>\n");

        // Add CSS based on template
        switch (template) {
            case MODERN:
                html.append(getModernCss());
                break;
            case CLASSIC:
                html.append(getClassicCss());
                break;
            case ATS_OPTIMIZED:
                html.append(getAtsOptimizedCss());
                break;
        }

        html.append("</head>\n<body>\n");

        // Header with contact info
        CvSection contactSection = sectionMap.get(CvSection.SectionType.CONTACT_INFO);
        if (contactSection != null && contactSection.getContent() != null) {
            String name = extractNameFromContact(contactSection.getContent());
            html.append("<div class='header'>\n");
            html.append("<h1 class='name'>").append(escapeHtml(name)).append("</h1>\n");
            html.append("<div class='contact'>").append(escapeHtml(contactSection.getContent())).append("</div>\n");
            html.append("</div>\n");
        }

        // Add sections
        addHtmlSection(html, sectionMap.get(CvSection.SectionType.PROFESSIONAL_SUMMARY), "Professional Summary");
        addHtmlSection(html, sectionMap.get(CvSection.SectionType.WORK_EXPERIENCE), "Work Experience");
        addHtmlSection(html, sectionMap.get(CvSection.SectionType.EDUCATION), "Education");
        addHtmlSection(html, sectionMap.get(CvSection.SectionType.SKILLS), "Skills");
        addHtmlSection(html, sectionMap.get(CvSection.SectionType.CERTIFICATIONS), "Certifications");
        addHtmlSection(html, sectionMap.get(CvSection.SectionType.PROJECTS), "Projects");
        addHtmlSection(html, sectionMap.get(CvSection.SectionType.LANGUAGES), "Languages");

        html.append("</body>\n</html>");
        return html.toString();
    }
    
    /**
     * Generate optimized CV in DOCX format
     */
    public byte[] generateOptimizedDocx(CvUpload cvUpload, List<CvSection> optimizedSections, CVTemplate template) {
        try {
            XWPFDocument document = new XWPFDocument();
            
            switch (template) {
                case MODERN:
                    generateModernDocxTemplate(document, cvUpload, optimizedSections);
                    break;
                case CLASSIC:
                    generateClassicDocxTemplate(document, cvUpload, optimizedSections);
                    break;
                case ATS_OPTIMIZED:
                    generateAtsOptimizedDocxTemplate(document, cvUpload, optimizedSections);
                    break;
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);
            document.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error generating DOCX for CV ID: {}", cvUpload.getId(), e);
            throw new RuntimeException("Failed to generate DOCX", e);
        }
    }
    
    /**
     * Add HTML section
     */
    private void addHtmlSection(StringBuilder html, CvSection section, String title) {
        if (section != null && section.getContent() != null && !section.getContent().trim().isEmpty()) {
            html.append("<div class='section'>\n");
            html.append("<h2 class='section-title'>").append(escapeHtml(title)).append("</h2>\n");
            html.append("<div class='section-content'>").append(escapeHtml(section.getContent())).append("</div>\n");
            html.append("</div>\n");
        }
    }

    /**
     * Escape HTML characters
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;")
                  .replace("\n", "<br>");
    }
    
    /**
     * Get Modern CSS styles
     */
    private String getModernCss() {
        return "<style>\n" +
               "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 40px; line-height: 1.6; color: #333; }\n" +
               ".header { text-align: center; margin-bottom: 30px; border-bottom: 3px solid #007acc; padding-bottom: 20px; }\n" +
               ".name { color: #007acc; font-size: 28px; margin: 0; font-weight: bold; }\n" +
               ".contact { font-size: 14px; color: #666; margin-top: 10px; }\n" +
               ".section { margin-bottom: 25px; }\n" +
               ".section-title { color: #007acc; font-size: 18px; border-bottom: 2px solid #007acc; padding-bottom: 5px; margin-bottom: 15px; }\n" +
               ".section-content { font-size: 14px; white-space: pre-line; }\n" +
               "</style>\n";
    }

    /**
     * Get Classic CSS styles
     */
    private String getClassicCss() {
        return "<style>\n" +
               "body { font-family: 'Times New Roman', serif; margin: 40px; line-height: 1.5; color: #000; }\n" +
               ".header { text-align: center; margin-bottom: 30px; }\n" +
               ".name { font-size: 24px; margin: 0; font-weight: bold; text-transform: uppercase; }\n" +
               ".contact { font-size: 12px; margin-top: 15px; }\n" +
               ".section { margin-bottom: 20px; }\n" +
               ".section-title { font-size: 16px; font-weight: bold; text-transform: uppercase; margin-bottom: 10px; border-bottom: 1px solid #000; }\n" +
               ".section-content { font-size: 12px; white-space: pre-line; }\n" +
               "</style>\n";
    }

    /**
     * Get ATS-Optimized CSS styles
     */
    private String getAtsOptimizedCss() {
        return "<style>\n" +
               "body { font-family: Arial, sans-serif; margin: 30px; line-height: 1.4; color: #000; }\n" +
               ".header { margin-bottom: 20px; }\n" +
               ".name { font-size: 20px; margin: 0; font-weight: bold; }\n" +
               ".contact { font-size: 11px; margin-top: 8px; }\n" +
               ".section { margin-bottom: 18px; }\n" +
               ".section-title { font-size: 14px; font-weight: bold; text-transform: uppercase; margin-bottom: 8px; }\n" +
               ".section-content { font-size: 11px; white-space: pre-line; }\n" +
               "</style>\n";
    }
    

    
    /**
     * Modern DOCX Template
     */
    private void generateModernDocxTemplate(XWPFDocument document, CvUpload cvUpload, List<CvSection> sections) {
        Map<CvSection.SectionType, CvSection> sectionMap = sections.stream()
            .collect(Collectors.toMap(CvSection::getSectionType, section -> section));

        // Header with name
        CvSection contactSection = sectionMap.get(CvSection.SectionType.CONTACT_INFO);
        if (contactSection != null && contactSection.getContent() != null) {
            String name = extractNameFromContact(contactSection.getContent());

            // Name title
            XWPFParagraph nameTitle = document.createParagraph();
            nameTitle.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun nameRun = nameTitle.createRun();
            nameRun.setText(name);
            nameRun.setBold(true);
            nameRun.setFontSize(20);
            nameRun.setColor("0066CC");

            // Contact info
            XWPFParagraph contactPara = document.createParagraph();
            contactPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun contactRun = contactPara.createRun();
            contactRun.setText(contactSection.getContent());
            contactRun.setFontSize(10);
        }

        // Add sections
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.PROFESSIONAL_SUMMARY), "Professional Summary");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.WORK_EXPERIENCE), "Work Experience");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.EDUCATION), "Education");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.SKILLS), "Skills");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.CERTIFICATIONS), "Certifications");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.PROJECTS), "Projects");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.LANGUAGES), "Languages");
    }

    /**
     * Classic DOCX Template
     */
    private void generateClassicDocxTemplate(XWPFDocument document, CvUpload cvUpload, List<CvSection> sections) {
        Map<CvSection.SectionType, CvSection> sectionMap = sections.stream()
            .collect(Collectors.toMap(CvSection::getSectionType, section -> section));

        // Header with name
        CvSection contactSection = sectionMap.get(CvSection.SectionType.CONTACT_INFO);
        if (contactSection != null && contactSection.getContent() != null) {
            String name = extractNameFromContact(contactSection.getContent());

            // Name title
            XWPFParagraph nameTitle = document.createParagraph();
            nameTitle.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun nameRun = nameTitle.createRun();
            nameRun.setText(name);
            nameRun.setBold(true);
            nameRun.setFontSize(18);

            // Separator line
            XWPFParagraph separator = document.createParagraph();
            separator.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun sepRun = separator.createRun();
            sepRun.setText("_".repeat(60));

            // Contact info
            XWPFParagraph contactPara = document.createParagraph();
            contactPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun contactRun = contactPara.createRun();
            contactRun.setText(contactSection.getContent());
            contactRun.setFontSize(10);
        }

        // Add sections in traditional order
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.PROFESSIONAL_SUMMARY), "PROFESSIONAL SUMMARY");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.WORK_EXPERIENCE), "WORK EXPERIENCE");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.EDUCATION), "EDUCATION");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.SKILLS), "SKILLS");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.CERTIFICATIONS), "CERTIFICATIONS");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.PROJECTS), "PROJECTS");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.LANGUAGES), "LANGUAGES");
    }

    /**
     * ATS-Optimized DOCX Template
     */
    private void generateAtsOptimizedDocxTemplate(XWPFDocument document, CvUpload cvUpload, List<CvSection> sections) {
        Map<CvSection.SectionType, CvSection> sectionMap = sections.stream()
            .collect(Collectors.toMap(CvSection::getSectionType, section -> section));

        // Simple header
        CvSection contactSection = sectionMap.get(CvSection.SectionType.CONTACT_INFO);
        if (contactSection != null && contactSection.getContent() != null) {
            String name = extractNameFromContact(contactSection.getContent());

            // Name title - simple format
            XWPFParagraph nameTitle = document.createParagraph();
            XWPFRun nameRun = nameTitle.createRun();
            nameRun.setText(name);
            nameRun.setBold(true);
            nameRun.setFontSize(14);

            // Contact info - simple format
            XWPFParagraph contactPara = document.createParagraph();
            XWPFRun contactRun = contactPara.createRun();
            contactRun.setText(contactSection.getContent());
            contactRun.setFontSize(11);
        }

        // ATS-friendly section order
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.PROFESSIONAL_SUMMARY), "SUMMARY");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.WORK_EXPERIENCE), "EXPERIENCE");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.SKILLS), "SKILLS");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.EDUCATION), "EDUCATION");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.CERTIFICATIONS), "CERTIFICATIONS");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.PROJECTS), "PROJECTS");
        addSectionToDocx(document, sectionMap.get(CvSection.SectionType.LANGUAGES), "LANGUAGES");
    }

    /**
     * Helper method to add a section to DOCX
     */
    private void addSectionToDocx(XWPFDocument document, CvSection section, String title) {
        if (section != null && section.getContent() != null && !section.getContent().trim().isEmpty()) {
            // Section header
            XWPFParagraph headerPara = document.createParagraph();
            XWPFRun headerRun = headerPara.createRun();
            headerRun.setText(title);
            headerRun.setBold(true);
            headerRun.setFontSize(12);
            headerRun.addBreak();

            // Section content
            XWPFParagraph contentPara = document.createParagraph();
            XWPFRun contentRun = contentPara.createRun();
            contentRun.setText(section.getContent());
            contentRun.setFontSize(10);
            contentRun.addBreak();
        }
    }

    /**
     * Extract name from contact information
     */
    private String extractNameFromContact(String contactInfo) {
        // Simple name extraction - take first line or first few words
        String[] lines = contactInfo.split("\n");
        if (lines.length > 0) {
            String firstLine = lines[0].trim();
            // If first line looks like a name (no @ or phone patterns), use it
            if (!firstLine.contains("@") && !firstLine.matches(".*\\d{3}.*")) {
                return firstLine;
            }
        }
        return "Professional Resume"; // Fallback
    }
}
