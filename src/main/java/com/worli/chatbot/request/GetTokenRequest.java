package com.worli.chatbot.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTokenRequest {
    String code;
    String grantType;
    String refreshToken;
}
