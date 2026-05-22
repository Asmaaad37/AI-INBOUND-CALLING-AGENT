package org.uos.cabe.service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AgentService {

    private final EmbeddingService embeddingService;
    private final VectorSearchService vectorSearchService;
    private final PromptService promptService;
    private final OpenAIService openAIService;
    private final ChatHistoryService chatHistoryService; // Clean Injection

    public AgentService(EmbeddingService embeddingService,
                        VectorSearchService vectorSearchService,
                        PromptService promptService,
                        OpenAIService openAIService,
                        ChatHistoryService chatHistoryService) {
        this.embeddingService = embeddingService;
        this.vectorSearchService = vectorSearchService;
        this.promptService = promptService;
        this.openAIService = openAIService;
        this.chatHistoryService = chatHistoryService;
    }

    public String request(String androidId, String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return "Barah-e-karam apna sawal btayn.";
        }

        // 1. Get isolated history using our new service
        String conversationHistory = chatHistoryService.getFormattedHistory(androidId);

        // 2. Vector search (RAG Context)
        List<Double> embedding = embeddingService.createEmbedding(userQuestion);
        List<String> matchedDocs = vectorSearchService.search(embedding);

        if (matchedDocs.isEmpty()) {
            return "معذرت، اس بارے میں فی الحال میرے پاس معلومات نہیں ہیں۔ آپ 048-111-867-111 پر رابطہ کر سکتے ہیں۔";
        }

        // 3. Prompt compilation
        String systemPrompt = promptService.getSystemPrompt();
        String finalPrompt = promptService.createFinalPrompt(matchedDocs, conversationHistory, userQuestion);

        // 4. Hit LLM Engine
        String aiResponse = openAIService.askChatGPT(systemPrompt, finalPrompt);

        // 5. Save background transaction using our new service
        chatHistoryService.saveChat(androidId, userQuestion, aiResponse);

        return aiResponse;
    }
}