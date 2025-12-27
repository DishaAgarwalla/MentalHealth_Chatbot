package com.example.chatbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ChatController {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String userId = request.getOrDefault("userId", "guest_" + System.currentTimeMillis());
        
        Map<String, Object> response = new HashMap<>();
        response.put("reply", generateResponse(message));
        response.put("userId", userId);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("sentiment", analyzeSentiment(message));
        
        // Send via WebSocket
        messagingTemplate.convertAndSend("/topic/chat/" + userId, response);
        
        return ResponseEntity.ok(response);
    }
    
    private String generateResponse(String message) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("sad") || lowerMessage.contains("depressed")) {
            return "I'm sorry you're feeling this way. Remember, it's okay to feel sad. Would you like to talk about it?";
        } else if (lowerMessage.contains("anxious") || lowerMessage.contains("stress")) {
            return "I understand anxiety can be overwhelming. Try taking deep breaths: inhale 4s, hold 4s, exhale 4s.";
        } else if (lowerMessage.contains("happy") || lowerMessage.contains("good")) {
            return "That's wonderful to hear! I'm glad you're feeling good today!";
        } else if (lowerMessage.contains("help") || lowerMessage.contains("emergency")) {
            return "If you're in crisis, please contact KIRAN Helpline: 1800-599-0019 immediately.";
        }
        
        return "Thank you for sharing. I'm here to listen. How are you feeling today?";
    }
    
    private Map<String, Object> analyzeSentiment(String message) {
        Map<String, Object> sentiment = new HashMap<>();
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("sad") || lowerMessage.contains("depressed") || lowerMessage.contains("hopeless")) {
            sentiment.put("emotion", "sad");
            sentiment.put("score", -0.8);
        } else if (lowerMessage.contains("anxious") || lowerMessage.contains("stress") || lowerMessage.contains("worried")) {
            sentiment.put("emotion", "anxious");
            sentiment.put("score", -0.6);
        } else if (lowerMessage.contains("happy") || lowerMessage.contains("good") || lowerMessage.contains("great")) {
            sentiment.put("emotion", "happy");
            sentiment.put("score", 0.8);
        } else if (lowerMessage.contains("angry") || lowerMessage.contains("mad") || lowerMessage.contains("frustrated")) {
            sentiment.put("emotion", "angry");
            sentiment.put("score", -0.7);
        } else {
            sentiment.put("emotion", "neutral");
            sentiment.put("score", 0.0);
        }
        
        return sentiment;
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Mental Health Chatbot");
        response.put("websocket", "enabled");
        return ResponseEntity.ok(response);
    }
    
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Map<String, Object> handleWebSocketMessage(Map<String, String> message) {
        String userId = message.get("userId");
        String text = message.get("message");
        
        Map<String, Object> response = new HashMap<>();
        response.put("reply", "Via WebSocket: " + generateResponse(text));
        response.put("userId", userId);
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("via", "websocket");
        
        return response;
    }
}