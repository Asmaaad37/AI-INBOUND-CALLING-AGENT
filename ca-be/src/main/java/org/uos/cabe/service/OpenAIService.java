package org.uos.cabe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public OpenAIService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.openai.com/v1").build();
    }

    public String askChatGPT(String systemPrompt, String finalPrompt) {
        Map<String, Object> chatRequest = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", finalPrompt)
                ),
                "temperature", 0.0
        );

        Map<?, ?> response = webClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(chatRequest)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("choices")) {
            return "Maaf kijiyega, framework engine responses process nahi ho sakay.";
        }

        List<?> choices = (List<?>) response.get("choices");
        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) choice.get("message");

        return message.get("content").toString();
    }
}