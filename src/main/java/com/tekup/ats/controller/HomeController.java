package com.tekup.ats.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // Generate session ID if not exists
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            session.setAttribute("sessionId", sessionId);
        }
        
        model.addAttribute("sessionId", sessionId);
        return "index";
    }
    
    @GetMapping("/chat")
    public String chat(HttpSession session, Model model) {
        String sessionId = (String) session.getAttribute("sessionId");
        if (sessionId == null) {
            return "redirect:/";
        }
        
        model.addAttribute("sessionId", sessionId);
        return "chat";
    }
}
