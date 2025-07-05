package com.tekup.ats.repository;

import com.tekup.ats.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findBySessionId(String sessionId);
    
    boolean existsByEmail(String email);
}
