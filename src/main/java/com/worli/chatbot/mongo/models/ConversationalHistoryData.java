package com.worli.chatbot.mongo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversational_history_data")
public class ConversationalHistoryData {
    @Id
    private String id;
    private String email;
    private String source; // "email"
    private String message;
    private String subject;
    private String responseFromOpenAi;
    private boolean isIntentFulfilled;
    private long createdAt;
}
