package com.worli.chatbot.controller;

import com.worli.chatbot.request.GoogleAuthRequest;
import com.worli.chatbot.response.GoogleAuthResponse;
import com.worli.chatbot.service.GoogleService;
import com.worli.chatbot.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.worli.chatbot.constants.APIConstants.GET_GOOGLE_AUTH_RESPONSE;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GoogleController {
    private final GoogleService googleService;

    @PostMapping(value = GET_GOOGLE_AUTH_RESPONSE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GoogleAuthResponse> consumeGoogleAuthResponse(@RequestBody GoogleAuthRequest request) {
        GoogleAuthResponse response = GoogleAuthResponse.builder()
                .success(false)
                .build();
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            LogUtils.getRequestLog(GET_GOOGLE_AUTH_RESPONSE, request);
            response = googleService.consumeGoogleAuthResponse(request);
            LogUtils.getResponseLog(GET_GOOGLE_AUTH_RESPONSE, response);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            LogUtils.getExceptionLog(GET_GOOGLE_AUTH_RESPONSE, request, e);
        }
        return ResponseEntity.status(httpStatus.value()).body(response);
    }
}
