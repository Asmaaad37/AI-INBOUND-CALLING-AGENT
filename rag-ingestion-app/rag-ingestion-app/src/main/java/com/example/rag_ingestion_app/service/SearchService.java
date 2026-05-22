package com.example.rag_ingestion_app.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    private final EmbeddingService embeddingService;
    private final JdbcTemplate jdbcTemplate;

    public SearchService(EmbeddingService embeddingService, JdbcTemplate jdbcTemplate) {
        this.embeddingService = embeddingService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> search(String userQuery) {
        System.out.println(" Searching for: " + userQuery);

        List<Float> queryEmbedding = embeddingService.generateEmbedding(userQuery);
        String vectorString = queryEmbedding.toString();

        String sql = """
            SELECT content, category, source_file, 
            (embedding <=> ?::vector) AS distance
            FROM admission_documents
            ORDER BY distance ASC
            LIMIT 10
            """;

        // Pehle yahan (sql, vectorString, vectorString) tha, jo error de raha tha
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, vectorString);

        System.out.println("📊 Results found in DB: " + results.size());
        return results;
    }
}