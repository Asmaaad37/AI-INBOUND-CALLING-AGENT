package org.uos.cabe.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VectorSearchService {

    private final JdbcTemplate jdbcTemplate;

    public VectorSearchService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> search(List<Double> embedding) {
        String vectorString = embedding.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        String sql = """
                SELECT content,
                       embedding <=> CAST(? AS vector) AS distance
                FROM admission_documents
                ORDER BY distance
                LIMIT 3
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
                    double distance = rs.getDouble("distance");

                    // Filter out unrelated data before it reaches the orchestrator
                    if (distance > 0.85) {
                        return null;
                    }
                    return rs.getString("content");
                }, vectorString)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}