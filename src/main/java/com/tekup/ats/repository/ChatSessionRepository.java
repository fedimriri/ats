package com.tekup.ats.repository;

import com.tekup.ats.entity.ChatSession;
import com.tekup.ats.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    
    Optional<ChatSession> findBySessionId(String sessionId);
    
    List<ChatSession> findByUserOrderByCreatedAtDesc(User user);
    
    List<ChatSession> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<ChatSession> findByUserAndStatus(User user, ChatSession.SessionStatus status);
}
