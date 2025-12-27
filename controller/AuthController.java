package com.example.chatbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        
        // Simple demo authentication
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        if ("demo".equals(username) && "demo".equals(password)) {
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("userId", UUID.randomUUID().toString());
            response.put("username", username);
        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> userData) {
        Map<String, Object> response = new HashMap<>();
        
        String username = userData.get("username");
        String email = userData.get("email");
        String password = userData.get("password");
        
        // Simple registration (no real database)
        response.put("success", true);
        response.put("message", "Registration successful");
        response.put("userId", UUID.randomUUID().toString());
        response.put("username", username);
        response.put("email", email);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}