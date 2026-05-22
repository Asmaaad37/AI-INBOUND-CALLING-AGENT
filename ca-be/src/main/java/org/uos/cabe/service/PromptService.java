package org.uos.cabe.service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PromptService {

    public String getSystemPrompt() {
        return """
                You are 'UOS Assistant', a helpful conversational assistant for the University of Sargodha.

                Instructions:
                - Review the 'Conversation History' to understand the context of follow-up questions.
                - Use 'Retrieved records' to build an accurate response.
                - Provide the final answer strictly in proper Urdu text (Urdu Script / اردو رسم الخط). Do NOT use Roman Urdu.

                Response rules:
                1. Rely primarily on the retrieved records to answer.
                2. Your response must be in clean Urdu text.
                3. Keep the answer short, clear, and under 30 words.
                4. Do not mention technical system keywords like "context", "history", or "data".
                5. If the records are unrelated, reply EXACTLY with: "معذرت، اس بارے میں فی الحال میرے پاس معلومات نہیں ہیں۔ آپ 048-111-867-111 پر رابطہ کر سکتے ہیں۔"
                """;
    }

    // UPDATE THIS METHOD DEFINITION TO ACCEPT 3 ARGUMENTS:
    public String createFinalPrompt(List<String> matchedDocs, String conversationHistory, String userQuestion) {
        String context = String.join("\n", matchedDocs);

        return """
                Retrieved university records:
                %s

                Conversation History (Previous turns):
                %s

                Current User question:
                %s

                Direct short answer strictly in Urdu text (اردو):
                """.formatted(context, conversationHistory, userQuestion.trim());
    }
}