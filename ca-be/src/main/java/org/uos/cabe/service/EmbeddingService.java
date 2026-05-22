package org.uos.cabe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public EmbeddingService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.openai.com/v1").build();
    }

    public List<Double> createEmbedding(String text) {
        Map<String, Object> requestBody = Map.of(
                "model", "text-embedding-3-small",
                "input", text.trim()
        );

        Map<?, ?> response = webClient.post()
                .uri("/embeddings")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("data")) {
            throw new IllegalStateException("Failed to retrieve embeddings from OpenAI.");
        }

        List<?> data = (List<?>) response.get("data");
        Map<?, ?> first = (Map<?, ?>) data.get(0);

        return (List<Double>) first.get("embedding");
    }
}