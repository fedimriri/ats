package com.tekup.ats.dto;

import com.tekup.ats.entity.OptimizationResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResultDto {
    
    private Long id;
    private String optimizationType;
    private String originalContent;
    private String optimizedContent;
    private String suggestions;
    private String keywordsAdded;
    private Integer atsScoreBefore;
    private Integer atsScoreAfter;
    private String improvements;
    private String aiAnalysis;
    private OptimizationResult.OptimizationStatus status;
    private LocalDateTime createdAt;
}
