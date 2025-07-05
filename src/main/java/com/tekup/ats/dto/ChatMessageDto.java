package com.tekup.ats.dto;

import com.tekup.ats.entity.ChatMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    
    private Long id;
    private String sessionId;
    private ChatMessage.SenderType senderType;
    private String message;
    private String messageType;
    private String metadata;
    private LocalDateTime createdAt;
}
