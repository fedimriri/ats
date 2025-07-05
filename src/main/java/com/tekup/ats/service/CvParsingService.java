package com.tekup.ats.service;

import com.tekup.ats.entity.CvSection;
import com.tekup.ats.entity.CvUpload;
import com.tekup.ats.repository.CvSectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class CvParsingService {
    
    private final CvSectionRepository cvSectionRepository;
    
    public List<CvSection> parseCvText(String cvText, CvUpload cvUpload) {
        List<CvSection> sections = new ArrayList<>();
        
        // Parse different sections using regex patterns
        sections.add(parseContactInfo(cvText, cvUpload));
        sections.add(parseProfessionalSummary(cvText, cvUpload));
        sections.add(parseWorkExperience(cvText, cvUpload));
        sections.add(parseEducation(cvText, cvUpload));
        sections.add(parseSkills(cvText, cvUpload));
        sections.add(parseCertifications(cvText, cvUpload));
        sections.add(parseProjects(cvText, cvUpload));
        sections.add(parseLanguages(cvText, cvUpload));
        
        // Save all sections
        return cvSectionRepository.saveAll(sections);
    }
    
    private CvSection parseContactInfo(String cvText, CvUpload cvUpload) {
        CvSection section = new CvSection();
        section.setCvUpload(cvUpload);
        section.setSectionType(CvSection.SectionType.CONTACT_INFO);
        section.setSectionTitle("Contact Information");
        section.setSectionOrder(1);
        
        StringBuilder contactInfo = new StringBuilder();
        List<String> missingFields = new ArrayList<>();
        
        // Extract email
        Pattern emailPattern = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
        Matcher emailMatcher = emailPattern.matcher(cvText);
        if (emailMatcher.find()) {
            contactInfo.append("Email: ").append(emailMatcher.group()).append("\n");
        } else {
            missingFields.add("Email address");
        }
        
        // Extract phone number
        Pattern phonePattern = Pattern.compile("(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
        Matcher phoneMatcher = phonePattern.matcher(cvText);
        if (phoneMatcher.find()) {
            contactInfo.append("Phone: ").append(phoneMatcher.group()).append("\n");
        } else {
            missingFields.add("Phone number");
        }
        
        // Extract LinkedIn profile
        Pattern linkedinPattern = Pattern.compile("linkedin\\.com/in/[\\w-]+", Pattern.CASE_INSENSITIVE);
        Matcher linkedinMatcher = linkedinPattern.matcher(cvText);
        if (linkedinMatcher.find()) {
            contactInfo.append("LinkedIn: ").append(linkedinMatcher.group()).append("\n");
        } else {
            missingFields.add("LinkedIn profile");
        }
        
        section.setContent(contactInfo.toString());
        section.setOriginalContent(contactInfo.toString());
        section.setIsComplete(missingFields.isEmpty());
        section.setMissingFields(String.join(", ", missingFields));
        
        return section;
    }
    
    private CvSection parseProfessionalSummary(String cvText, CvUpload cvUpload) {
        CvSection section = new CvSection();
        section.setCvUpload(cvUpload);
        section.setSectionType(CvSection.SectionType.PROFESSIONAL_SUMMARY);
        section.setSectionTitle("Professional Summary");
        section.setSectionOrder(2);
        
        // Look for summary/objective sections
        Pattern summaryPattern = Pattern.compile(
            "(?i)(summary|objective|profile|about)\\s*:?\\s*([\\s\\S]*?)(?=\\n\\s*[A-Z][^\\n]*:|$)",
            Pattern.MULTILINE
        );
        
        Matcher summaryMatcher = summaryPattern.matcher(cvText);
        String summaryContent = "";
        if (summaryMatcher.find()) {
            summaryContent = summaryMatcher.group(2).trim();
        }
        
        section.setContent(summaryContent);
        section.setOriginalContent(summaryContent);
        section.setIsComplete(!summaryContent.isEmpty() && summaryContent.length() > 50);
        
        if (!section.getIsComplete()) {
            section.setMissingFields("Professional summary should be 2-3 sentences highlighting key qualifications");
        }
        
        return section;
    }
    
    private CvSection parseWorkExperience(String cvText, CvUpload cvUpload) {
        CvSection section = new CvSection();
        section.setCvUpload(cvUpload);
        section.setSectionType(CvSection.SectionType.WORK_EXPERIENCE);
        section.setSectionTitle("Work Experience");
        section.setSectionOrder(3);
        
        // Look for experience sections
        Pattern experiencePattern = Pattern.compile(
            "(?i)(experience|employment|work history)\\s*:?\\s*([\\s\\S]*?)(?=\\n\\s*[A-Z][^\\n]*:|$)",
            Pattern.MULTILINE
        );
        
        Matcher experienceMatcher = experiencePattern.matcher(cvText);
        String experienceContent = "";
        if (experienceMatcher.find()) {
            experienceContent = experienceMatcher.group(2).trim();
        }
        
        section.setContent(experienceContent);
        section.setOriginalContent(experienceContent);
        
        // Check for completeness (should have job titles, companies, dates, descriptions)
        boolean hasJobTitles = experienceContent.matches("(?s).*\\b(manager|developer|analyst|coordinator|specialist)\\b.*");
        boolean hasDates = experienceContent.matches("(?s).*\\b(20\\d{2}|19\\d{2})\\b.*");
        boolean hasCompanies = experienceContent.length() > 100; // Basic check
        
        section.setIsComplete(hasJobTitles && hasDates && hasCompanies);
        
        List<String> missingFields = new ArrayList<>();
        if (!hasJobTitles) missingFields.add("Job titles");
        if (!hasDates) missingFields.add("Employment dates");
        if (!hasCompanies) missingFields.add("Company names and job descriptions");
        
        section.setMissingFields(String.join(", ", missingFields));
        
        return section;
    }
    
    private CvSection parseEducation(String cvText, CvUpload cvUpload) {
        CvSection section = new CvSection();
        section.setCvUpload(cvUpload);
        section.setSectionType(CvSection.SectionType.EDUCATION);
        section.setSectionTitle("Education");
        section.setSectionOrder(4);
        
        Pattern educationPattern = Pattern.compile(
            "(?i)(education|academic|qualification)\\s*:?\\s*([\\s\\S]*?)(?=\\n\\s*[A-Z][^\\n]*:|$)",
            Pattern.MULTILINE
        );
        
        Matcher educationMatcher = educationPattern.matcher(cvText);
        String educationContent = "";
        if (educationMatcher.find()) {
            educationContent = educationMatcher.group(2).trim();
        }
        
        section.setContent(educationContent);
        section.setOriginalContent(educationContent);
        
        boolean hasDegree = educationContent.matches("(?s).*\\b(bachelor|master|phd|degree|diploma)\\b.*");
        boolean hasInstitution = educationContent.length() > 20;
        
        section.setIsComplete(hasDegree && hasInstitution);
        
        if (!section.getIsComplete()) {
            section.setMissingFields("Degree type, institution name, graduation year");
        }
        
        return section;
    }
    
    private CvSection parseSkills(String cvText, CvUpload cvUpload) {
        CvSection section = new CvSection();
        section.setCvUpload(cvUpload);
        section.setSectionType(CvSection.SectionType.SKILLS);
        section.setSectionTitle("Skills");
        section.setSectionOrder(5);
        
        Pattern skillsPattern = Pattern.compile(
            "(?i)(skills|competencies|technologies)\\s*:?\\s*([\\s\\S]*?)(?=\\n\\s*[A-Z][^\\n]*:|$)",
            Pattern.MULTILINE
        );
        
        Matcher skillsMatcher = skillsPattern.matcher(cvText);
        String skillsContent = "";
        if (skillsMatcher.find()) {
            skillsContent = skillsMatcher.group(2).trim();
        }
        
        section.setContent(skillsContent);
        section.setOriginalContent(skillsContent);
        section.setIsComplete(!skillsContent.isEmpty() && skillsContent.length() > 20);
        
        if (!section.getIsComplete()) {
            section.setMissingFields("Technical skills, soft skills, programming languages, tools");
        }
        
        return section;
    }
    
    private CvSection parseCertifications(String cvText, CvUpload cvUpload) {
        return createOptionalSection(cvText, cvUpload, CvSection.SectionType.CERTIFICATIONS, 
            "Certifications", 6, "(certification|certificate|license)");
    }
    
    private CvSection parseProjects(String cvText, CvUpload cvUpload) {
        return createOptionalSection(cvText, cvUpload, CvSection.SectionType.PROJECTS, 
            "Projects", 7, "(project|portfolio)");
    }
    
    private CvSection parseLanguages(String cvText, CvUpload cvUpload) {
        return createOptionalSection(cvText, cvUpload, CvSection.SectionType.LANGUAGES, 
            "Languages", 8, "(language|linguistic)");
    }
    
    private CvSection createOptionalSection(String cvText, CvUpload cvUpload, 
                                         CvSection.SectionType sectionType, String title, 
                                         int order, String pattern) {
        CvSection section = new CvSection();
        section.setCvUpload(cvUpload);
        section.setSectionType(sectionType);
        section.setSectionTitle(title);
        section.setSectionOrder(order);
        
        Pattern sectionPattern = Pattern.compile(
            "(?i)" + pattern + "\\s*:?\\s*([\\s\\S]*?)(?=\\n\\s*[A-Z][^\\n]*:|$)",
            Pattern.MULTILINE
        );
        
        Matcher matcher = sectionPattern.matcher(cvText);
        String content = "";
        if (matcher.find()) {
            content = matcher.group(1).trim();
        }
        
        section.setContent(content);
        section.setOriginalContent(content);
        section.setIsComplete(!content.isEmpty());
        
        return section;
    }
}
