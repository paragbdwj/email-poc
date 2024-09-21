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
@Document(collection = "user_google_token_data")
public class UserGoogleTokenData {
    private String googleId;  // for now let's rely on google's unique id
    private String accessToken;
    private int expiresIn;
    private String refreshToken;
    private String scope;
    private String tokenType;
    private String idToken;
}
