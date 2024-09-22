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
@Document(collection = "user_token_data")
public class UserTokenData {
    @Id
    private String id;
    private Long userId;
    private String googleId;
    private String accessToken;
    private long expiresIn;
    private String refreshToken;
    private String scope;
    private String tokenType;
    private String idToken;
    private String verificationId;
}
