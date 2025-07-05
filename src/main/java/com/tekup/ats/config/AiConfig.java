package com.tekup.ats.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.ai.grok")
@Data
public class AiConfig {
    
    private String apiKey;
    private String baseUrl = "https://api.x.ai/v1";
    private String model = "grok-beta";
    private int maxTokens = 4000;
    private double temperature = 0.7;
}
