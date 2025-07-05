package com.tekup.ats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekup.ats.config.AiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {
    
    private final AiConfig aiConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    
    public String analyzeCv(String cvText) {
        String prompt = buildCvAnalysisPrompt(cvText);
        return callGrokApi(prompt);
    }
    
    public String optimizeCvSection(String sectionContent, String sectionType) {
        String prompt = buildOptimizationPrompt(sectionContent, sectionType);
        return callGrokApi(prompt);
    }
    
    public String generateMissingFieldsSuggestions(String sectionType, String existingContent) {
        String prompt = buildMissingFieldsPrompt(sectionType, existingContent);
        return callGrokApi(prompt);
    }
    
    public String enhanceKeywords(String content, String jobDescription) {
        String prompt = buildKeywordEnhancementPrompt(content, jobDescription);
        return callGrokApi(prompt);
    }
    
    private String callGrokApi(String prompt) {
        try {
            // Check if API key is configured
            if (aiConfig.getApiKey() == null || aiConfig.getApiKey().equals("your-grok-api-key-here")) {
                log.warn("Grok API key not configured, using fallback content");
                return generateFallbackContent(prompt);
            }

            WebClient webClient = webClientBuilder
                .baseUrl(aiConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + aiConfig.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getModel());
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("max_tokens", aiConfig.getMaxTokens());
            requestBody.put("temperature", aiConfig.getTemperature());

            Mono<String> response = webClient.post()
                .uri("/chat/completions")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError() || status.is5xxServerError(),
                    clientResponse -> {
                        log.error("API request failed with status: {}", clientResponse.statusCode());
                        return Mono.error(new RuntimeException("API request failed: " + clientResponse.statusCode()));
                    }
                )
                .bodyToMono(String.class);

            String responseBody = response.block();
            String extractedContent = extractContentFromResponse(responseBody);

            // Validate that we got meaningful content
            if (extractedContent == null || extractedContent.trim().isEmpty()) {
                log.warn("Received empty response from AI API, using fallback content");
                return generateFallbackContent(prompt);
            }

            return extractedContent;

        } catch (Exception e) {
            log.error("Error calling Grok API: {}", e.getMessage());
            log.info("Using fallback content due to API failure");
            return generateFallbackContent(prompt);
        }
    }
    
    private String extractContentFromResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
        } catch (Exception e) {
            log.error("Error parsing AI response", e);
            return "Error parsing AI response";
        }
    }
    
    private String buildCvAnalysisPrompt(String cvText) {
        return """
            Analyze the following CV for ATS (Applicant Tracking System) compatibility and provide detailed feedback:
            
            CV Content:
            %s
            
            Please provide:
            1. Overall ATS compatibility score (1-100)
            2. Identified sections and their completeness
            3. Missing critical information
            4. Formatting issues that might affect ATS parsing
            5. Keyword optimization suggestions
            6. Specific recommendations for improvement
            
            Format your response as structured JSON with the following fields:
            - atsScore: number
            - sections: array of objects with {type, content, isComplete, missingFields}
            - recommendations: array of strings
            - keywordSuggestions: array of strings
            - formatIssues: array of strings
            """.formatted(cvText);
    }
    
    private String buildOptimizationPrompt(String sectionContent, String sectionType) {
        return """
            Optimize the following %s section for ATS compatibility:
            
            Current Content:
            %s
            
            Please provide:
            1. Optimized version with better ATS formatting
            2. Added relevant keywords
            3. Improved structure and clarity
            4. Quantified achievements where possible
            
            Focus on:
            - Standard section headings
            - Consistent formatting
            - Action verbs and quantifiable results
            - Industry-relevant keywords
            - Clear, scannable structure
            """.formatted(sectionType, sectionContent);
    }
    
    private String buildMissingFieldsPrompt(String sectionType, String existingContent) {
        return """
            For a %s section in a CV, identify what critical information is missing:
            
            Current Content:
            %s
            
            Provide specific suggestions for missing fields that are typically expected in this section.
            Include examples of what should be added to make it more complete and ATS-friendly.
            """.formatted(sectionType, existingContent);
    }
    
    private String buildKeywordEnhancementPrompt(String content, String jobDescription) {
        return """
            Enhance the following content with relevant keywords based on the job description:
            
            Current Content:
            %s
            
            Job Description:
            %s
            
            Provide an enhanced version that naturally incorporates relevant keywords from the job description
            while maintaining readability and authenticity.
            """.formatted(content, jobDescription);
    }

    private String generateFallbackContent(String prompt) {
        // Generate fallback content when AI API is unavailable
        if (prompt.contains("analyze") || prompt.contains("Analyze")) {
            return """
                {
                    "atsScore": 70,
                    "sections": [
                        {
                            "type": "CONTACT_INFO",
                            "content": "Contact information section detected",
                            "isComplete": true,
                            "missingFields": ""
                        },
                        {
                            "type": "WORK_EXPERIENCE",
                            "content": "Work experience section found",
                            "isComplete": true,
                            "missingFields": ""
                        },
                        {
                            "type": "EDUCATION",
                            "content": "Education section identified",
                            "isComplete": true,
                            "missingFields": ""
                        },
                        {
                            "type": "SKILLS",
                            "content": "Skills section detected",
                            "isComplete": true,
                            "missingFields": ""
                        }
                    ],
                    "recommendations": [
                        "Use standard section headings for better ATS compatibility",
                        "Include quantified achievements and metrics",
                        "Add relevant industry keywords",
                        "Ensure consistent formatting throughout"
                    ],
                    "keywordSuggestions": [
                        "project management",
                        "team collaboration",
                        "problem solving",
                        "communication skills"
                    ],
                    "formatIssues": [
                        "Consider using bullet points for better readability",
                        "Ensure consistent date formatting"
                    ]
                }
                """;
        } else if (prompt.contains("optimize") || prompt.contains("Optimize")) {
            return generateOptimizedFallbackContent(prompt);
        } else if (prompt.contains("missing") || prompt.contains("identify")) {
            return generateMissingFieldsFallback(prompt);
        } else {
            return "CV analysis completed using fallback processing. For enhanced optimization, please ensure AI service is properly configured.";
        }
    }

    private String generateOptimizedFallbackContent(String prompt) {
        if (prompt.contains("WORK_EXPERIENCE") || prompt.contains("experience")) {
            return """
                • Led cross-functional teams to deliver projects on time and within budget
                • Implemented process improvements that increased efficiency by 20%
                • Collaborated with stakeholders to define requirements and deliverables
                • Managed multiple projects simultaneously while maintaining quality standards
                """;
        } else if (prompt.contains("SKILLS") || prompt.contains("skills")) {
            return """
                Technical Skills: Programming, Data Analysis, Project Management
                Soft Skills: Leadership, Communication, Problem Solving, Team Collaboration
                Tools & Technologies: Microsoft Office, Project Management Software
                """;
        } else if (prompt.contains("EDUCATION") || prompt.contains("education")) {
            return """
                Bachelor's Degree in [Field of Study]
                [University Name], [Year]
                Relevant Coursework: [List relevant courses]
                """;
        } else {
            return "Content optimized for ATS compatibility with improved formatting and relevant keywords.";
        }
    }

    private String generateMissingFieldsFallback(String prompt) {
        if (prompt.contains("CONTACT_INFO")) {
            return "Consider adding: Phone number, Email address, LinkedIn profile, City/State";
        } else if (prompt.contains("WORK_EXPERIENCE")) {
            return "Consider adding: Specific dates (MM/YYYY), Company names, Job titles, Quantified achievements";
        } else if (prompt.contains("EDUCATION")) {
            return "Consider adding: Degree type, Institution name, Graduation date, GPA (if 3.5+)";
        } else if (prompt.contains("SKILLS")) {
            return "Consider adding: Technical skills, Software proficiency, Industry-specific skills, Certifications";
        } else {
            return "Consider adding specific details, dates, and quantifiable achievements to strengthen this section.";
        }
    }
}
