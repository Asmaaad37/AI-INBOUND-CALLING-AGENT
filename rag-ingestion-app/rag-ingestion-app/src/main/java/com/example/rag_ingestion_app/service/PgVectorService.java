package com.example.rag_ingestion_app.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PgVectorService {

    private final JdbcTemplate jdbcTemplate;

    public PgVectorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String content, List<Float> embedding, String category, String sourceFile) {
        // Correct way: embedding.toString() returns "[0.1, 0.2...]"
        // JdbcTemplate will handle the parameter binding safely.
        String vectorString = embedding.toString();

        String sql = """
                INSERT INTO admission_documents
                (content, embedding, category, source_file)
                VALUES (?, ?::vector, ?, ?)
                """;

        try {
            jdbcTemplate.update(sql, content, vectorString, category, sourceFile);
        } catch (Exception e) {
            System.err.println(" Database Save Error: " + e.getMessage());
            throw e;
        }
    }
}