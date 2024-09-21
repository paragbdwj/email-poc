package com.worli.chatbot.mongo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversational_history_data")
public class ConversationalHistoryData {
    private String email;
    private String source; // can be email,whatsapp
    private String message;
    private String subject;
    private String responseFromOpenAi;
    private long createdAt;
}
