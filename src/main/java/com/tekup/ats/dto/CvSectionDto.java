package com.tekup.ats.dto;

import com.tekup.ats.entity.CvSection;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CvSectionDto {
    
    private Long id;
    private CvSection.SectionType sectionType;
    private String sectionTitle;
    private String content;
    private String originalContent;
    private Boolean isComplete;
    private String missingFields;
    private Integer sectionOrder;
}
