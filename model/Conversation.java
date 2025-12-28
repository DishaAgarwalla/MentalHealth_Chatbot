package com.example.chatbot.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Conversation {
    private String id;
    private String userId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private List<ChatMessage> messages;
    
    public Conversation() {
        this.id = java.util.UUID.randomUUID().toString();
        this.startedAt = LocalDateTime.now();
        this.messages = new ArrayList<>();
    }
}