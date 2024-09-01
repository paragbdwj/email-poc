package com.worli.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.model.GoogleResponseObject;
import com.worli.chatbot.request.GoogleAuthRequest;
import com.worli.chatbot.response.GoogleAuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService {
    private final DatabaseHelper databaseHelper;
    private final ObjectMapper objectMapper;
    public GoogleAuthResponse consumeGoogleAuthResponse(GoogleAuthRequest request) throws JsonProcessingException {
        GoogleResponseObject googleResponseObject = objectMapper.readValue(request.getResponseFromGoogle(), GoogleResponseObject.class);
        databaseHelper.saveGoogleAuthResponseData(googleResponseObject);
        return GoogleAuthResponse.builder()
                .success(true)
                .build();
    }
}
