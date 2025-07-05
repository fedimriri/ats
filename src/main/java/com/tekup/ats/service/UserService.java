package com.tekup.ats.service;

import com.tekup.ats.entity.User;
import com.tekup.ats.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    public User getOrCreateUserBySessionId(String sessionId) {
        Optional<User> existingUser = userRepository.findBySessionId(sessionId);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        // Create new user with session ID
        User newUser = new User();
        newUser.setSessionId(sessionId);
        newUser.setName("Anonymous User");
        newUser.setEmail("user_" + UUID.randomUUID().toString().substring(0, 8) + "@temp.com");
        
        return userRepository.save(newUser);
    }
    
    public User getUserBySessionId(String sessionId) {
        return userRepository.findBySessionId(sessionId).orElse(null);
    }
    
    public User updateUserInfo(String sessionId, String name, String email, String phone) {
        User user = getOrCreateUserBySessionId(sessionId);
        
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }
        
        if (email != null && !email.trim().isEmpty()) {
            // Check if email is already taken by another user
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isEmpty() || existingUser.get().getId().equals(user.getId())) {
                user.setEmail(email.trim());
            }
        }
        
        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone.trim());
        }
        
        return userRepository.save(user);
    }
    
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}
