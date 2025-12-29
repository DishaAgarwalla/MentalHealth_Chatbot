package com.example.chatbot.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MoodTrackerService {
    
    private final Map<String, List<Map<String, Object>>> moodHistory = new HashMap<>();
    
    public void trackMood(String userId, Map<String, Object> sentiment, String message) {
        Map<String, Object> moodEntry = new HashMap<>();
        moodEntry.put("timestamp", LocalDateTime.now().toString());
        moodEntry.put("sentiment", sentiment.get("sentiment"));
        moodEntry.put("emotion", sentiment.get("emotion"));
        moodEntry.put("score", sentiment.get("score"));
        moodEntry.put("message", message.substring(0, Math.min(50, message.length())));
        
        moodHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(moodEntry);
        
        // Keep only last 20 entries per user
        if (moodHistory.get(userId).size() > 20) {
            moodHistory.get(userId).remove(0);
        }
    }
    
    public Map<String, Object> getMoodHistory(String userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        
        List<Map<String, Object>> history = moodHistory.getOrDefault(userId, new ArrayList<>());
        response.put("moodHistory", history);
        response.put("totalEntries", history.size());
        
        if (!history.isEmpty()) {
            // Calculate average mood
            double avgScore = history.stream()
                .mapToDouble(entry -> (Double) entry.get("score"))
                .average()
                .orElse(0.0);
            
            response.put("averageMoodScore", avgScore);
            response.put("currentMood", history.get(history.size() - 1));
        }
        
        return response;
    }
    
    public Map<String, Object> getMoodSummary(String userId) {
        List<Map<String, Object>> history = moodHistory.getOrDefault(userId, new ArrayList<>());
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("userId", userId);
        summary.put("totalDays", history.size());
        
        if (!history.isEmpty()) {
            Map<String, Long> emotionCount = new HashMap<>();
            for (Map<String, Object> entry : history) {
                String emotion = (String) entry.get("emotion");
                emotionCount.put(emotion, emotionCount.getOrDefault(emotion, 0L) + 1);
            }
            
            summary.put("emotionDistribution", emotionCount);
            summary.put("mostCommonEmotion", 
                emotionCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("neutral"));
        }
        
        return summary;
    }
}