package com.tekup.ats.service;

import com.tekup.ats.dto.ChatMessageDto;
import com.tekup.ats.entity.ChatMessage;
import com.tekup.ats.entity.ChatSession;
import com.tekup.ats.entity.User;
import com.tekup.ats.repository.ChatMessageRepository;
import com.tekup.ats.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatService {
    
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    
    public ChatSession getOrCreateChatSession(String sessionId) {
        Optional<ChatSession> existingSession = chatSessionRepository.findBySessionId(sessionId);
        
        if (existingSession.isPresent()) {
            return existingSession.get();
        }
        
        // Create new chat session
        User user = userService.getOrCreateUserBySessionId(sessionId);
        
        ChatSession chatSession = new ChatSession();
        chatSession.setUser(user);
        chatSession.setSessionId(sessionId);
        chatSession.setStatus(ChatSession.SessionStatus.ACTIVE);
        chatSession.setCurrentStep("welcome");
        
        chatSession = chatSessionRepository.save(chatSession);
        
        // Send welcome message
        sendBotMessage(chatSession, "Welcome to the ATS CV Optimizer! I'm here to help you create an ATS-friendly CV. " +
            "Please upload your CV file (PDF, DOC, or DOCX) to get started.", "welcome");
        
        return chatSession;
    }
    
    public ChatMessageDto sendUserMessage(String sessionId, String message) {
        ChatSession chatSession = getOrCreateChatSession(sessionId);
        
        // Save user message
        ChatMessage userMessage = new ChatMessage();
        userMessage.setChatSession(chatSession);
        userMessage.setSenderType(ChatMessage.SenderType.USER);
        userMessage.setMessage(message);
        userMessage.setMessageType("text");
        
        userMessage = chatMessageRepository.save(userMessage);
        
        // Process user message and generate bot response
        processUserMessage(chatSession, message);
        
        return convertToDto(userMessage);
    }
    
    public void sendBotMessage(ChatSession chatSession, String message, String messageType) {
        ChatMessage botMessage = new ChatMessage();
        botMessage.setChatSession(chatSession);
        botMessage.setSenderType(ChatMessage.SenderType.BOT);
        botMessage.setMessage(message);
        botMessage.setMessageType(messageType);
        
        botMessage = chatMessageRepository.save(botMessage);
        
        // Send via WebSocket
        ChatMessageDto messageDto = convertToDto(botMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + chatSession.getSessionId(), messageDto);
    }
    
    private void processUserMessage(ChatSession chatSession, String message) {
        String currentStep = chatSession.getCurrentStep();
        
        switch (currentStep) {
            case "welcome":
                handleWelcomeStep(chatSession, message);
                break;
            case "file_uploaded":
                handleFileUploadedStep(chatSession, message);
                break;
            case "missing_info":
                handleMissingInfoStep(chatSession, message);
                break;
            case "optimization":
                handleOptimizationStep(chatSession, message);
                break;
            default:
                sendBotMessage(chatSession, "I'm not sure how to help with that. Please upload a CV file to get started.", "error");
        }
    }
    
    private void handleWelcomeStep(ChatSession chatSession, String message) {
        if (message.toLowerCase().contains("upload") || message.toLowerCase().contains("file")) {
            sendBotMessage(chatSession, "Great! Please use the file upload button below to upload your CV. " +
                "I accept PDF, DOC, and DOCX files up to 10MB.", "file_prompt");
        } else {
            sendBotMessage(chatSession, "To get started, please upload your CV file. " +
                "I'll analyze it and help you optimize it for ATS systems.", "instruction");
        }
    }
    
    private void handleFileUploadedStep(ChatSession chatSession, String message) {
        sendBotMessage(chatSession, "I'm analyzing your CV now. This may take a few moments...", "processing");
        chatSession.setCurrentStep("processing");
        chatSessionRepository.save(chatSession);
    }
    
    private void handleMissingInfoStep(ChatSession chatSession, String message) {
        // Process user's response to missing information prompts
        sendBotMessage(chatSession, "Thank you for the additional information. Let me update your CV...", "processing");
    }
    
    private void handleOptimizationStep(ChatSession chatSession, String message) {
        if (message.toLowerCase().contains("yes") || message.toLowerCase().contains("optimize")) {
            sendBotMessage(chatSession, "I'll optimize your CV now. This may take a few minutes...", "processing");
        } else if (message.toLowerCase().contains("no") || message.toLowerCase().contains("skip")) {
            sendBotMessage(chatSession, "No problem! You can download your current CV or make manual changes.", "completion");
        } else {
            sendBotMessage(chatSession, "Would you like me to optimize your CV for better ATS compatibility? (Yes/No)", "confirmation");
        }
    }
    
    public void notifyFileProcessed(String sessionId, Long cvId, boolean success) {
        Optional<ChatSession> sessionOpt = chatSessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isPresent()) {
            ChatSession chatSession = sessionOpt.get();
            
            if (success) {
                sendBotMessage(chatSession, "Great! I've analyzed your CV and found some areas for improvement. " +
                    "Let me show you what I found...", "analysis_complete");
                chatSession.setCurrentStep("analysis_complete");
            } else {
                sendBotMessage(chatSession, "I encountered an error processing your CV. Please try uploading again " +
                    "or contact support if the problem persists.", "error");
                chatSession.setCurrentStep("error");
            }
            
            chatSessionRepository.save(chatSession);
        }
    }
    
    public List<ChatMessageDto> getChatHistory(String sessionId) {
        Optional<ChatSession> sessionOpt = chatSessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isEmpty()) {
            return List.of();
        }
        
        return chatMessageRepository.findByChatSessionOrderByCreatedAt(sessionOpt.get())
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }
    
    private ChatMessageDto convertToDto(ChatMessage message) {
        return new ChatMessageDto(
            message.getId(),
            message.getChatSession().getSessionId(),
            message.getSenderType(),
            message.getMessage(),
            message.getMessageType(),
            message.getMetadata(),
            message.getCreatedAt()
        );
    }
}
