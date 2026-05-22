package org.uos.cabe.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class ChatHistoryService {

    private final JdbcTemplate jdbcTemplate;

    public ChatHistoryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public String getFormattedHistory(String androidId) {
        String historyQuery = """
                SELECT user_message, ai_response 
                FROM chat_history 
                WHERE android_id = ? 
                ORDER BY timestamp DESC 
                LIMIT 3
                """;

        List<String> previousChatTurns = jdbcTemplate.query(historyQuery, (rs, rowNum) -> {
            return "User: " + rs.getString("user_message") + "\nAssistant: " + rs.getString("ai_response");
        }, androidId);

        // Reverse history so it reads from oldest to newest
        Collections.reverse(previousChatTurns);

        return String.join("\n", previousChatTurns);
    }

    /**
     * Saves a new chat transaction into the database.
     */
    public void saveChat(String androidId, String userMessage, String aiResponse) {
        String saveQuery = """
                INSERT INTO chat_history (android_id, user_message, ai_response) 
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(saveQuery, androidId, userMessage.trim(), aiResponse);
    }
}