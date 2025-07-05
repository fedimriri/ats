package com.tekup.ats.repository;

import com.tekup.ats.entity.ChatMessage;
import com.tekup.ats.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByChatSessionOrderByCreatedAt(ChatSession chatSession);
    
    List<ChatMessage> findByChatSessionIdOrderByCreatedAt(Long chatSessionId);
}
