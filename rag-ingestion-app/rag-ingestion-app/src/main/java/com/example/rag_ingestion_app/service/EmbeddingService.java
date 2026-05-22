package com.example.rag_ingestion_app.service;

import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.embedding.EmbeddingRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmbeddingService {

    private final OpenAiService openAiService;

    public EmbeddingService(@Value("${openai.api.key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public List<Float> generateEmbedding(String text) {
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model("text-embedding-3-small")
                .input(List.of(text))
                .build();

        try {
            List<Float> embeddings = openAiService.createEmbeddings(request)
                    .getData()
                    .get(0)
                    .getEmbedding()
                    .stream()
                    .map(Double::floatValue)
                    .toList();

            System.out.println("Vector created. Size: " + embeddings.size());
            return embeddings;
        } catch (Exception e) {
            System.err.println("OpenAI API Error: " + e.getMessage());
            throw e;
        }
    }
}