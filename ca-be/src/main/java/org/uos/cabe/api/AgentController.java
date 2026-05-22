package org.uos.cabe.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.uos.cabe.service.AgentService; // Changed from OpenAIService

import java.util.Map;

@RestController
public class AgentController {

    @Autowired
    private AgentService agentService; // Injecting AgentService instead

    @PostMapping("/agent")
    public ResponseEntity<Map<String, String>> ask(@RequestBody Map<String, String> request) {
        String androidId = request.getOrDefault("android_id", "unknown_device");
        String message = request.getOrDefault("message", "");

        if (message.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("response", "Message is required."));
        }

        // Pass both the message AND the androidId to the service layer
        String aiResponse = agentService.request(androidId, message);

        return ResponseEntity.ok(Map.of("response", aiResponse));
    }
}