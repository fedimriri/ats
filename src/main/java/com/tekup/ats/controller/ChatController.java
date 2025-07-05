package com.tekup.ats.controller;

import com.tekup.ats.dto.ChatMessageDto;
import com.tekup.ats.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    
    private final ChatService chatService;
    
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload Map<String, String> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = message.get("sessionId");
        String messageText = message.get("message");
        
        if (sessionId != null && messageText != null) {
            chatService.sendUserMessage(sessionId, messageText);
        }
    }
    
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload Map<String, String> message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = message.get("sessionId");
        
        if (sessionId != null) {
            // Initialize chat session
            chatService.getOrCreateChatSession(sessionId);
            headerAccessor.getSessionAttributes().put("sessionId", sessionId);
        }
    }
    
    @GetMapping("/history")
    @ResponseBody
    public List<ChatMessageDto> getChatHistory(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return List.of();
        }
        
        return chatService.getChatHistory(sessionId);
    }
}
