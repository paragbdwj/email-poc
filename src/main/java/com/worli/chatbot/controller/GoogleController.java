package com.worli.chatbot.controller;

import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.request.GoogleAuthRequest;
import com.worli.chatbot.request.ReceiveEmailRequest;
import com.worli.chatbot.response.GoogleAuthResponse;
import com.worli.chatbot.response.GoogleOauthResponse;
import com.worli.chatbot.response.ReceiveEmailResponse;
import com.worli.chatbot.service.GoogleService;
import com.worli.chatbot.service.email.EmailSenderService;
import com.worli.chatbot.utils.LogUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.SchemePortResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.worli.chatbot.constants.APIConstants.GET_GOOGLE_AUTH_RESPONSE;
import static com.worli.chatbot.constants.APIConstants.GOOGLE_OAUTH_REDIRECT_URI;
import static com.worli.chatbot.constants.APIConstants.GOOGLE_RECEIVE_EMAIL_WEBHOOK;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GoogleController {
    private final GoogleService googleService;
    private final EmailSenderService emailSenderService;
    private final DatabaseHelper databaseHelper;

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

    // TODO : Possible need to expose below webhook to google (Remove if not required)
    @PostMapping(value = GOOGLE_RECEIVE_EMAIL_WEBHOOK,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReceiveEmailResponse> receiveEmails(@RequestBody ReceiveEmailRequest request) {
        ReceiveEmailResponse response = ReceiveEmailResponse.builder()
                .success(false)
                .build();
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            LogUtils.getRequestLog(GOOGLE_RECEIVE_EMAIL_WEBHOOK, request);
            LogUtils.getResponseLog(GOOGLE_RECEIVE_EMAIL_WEBHOOK, response);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            LogUtils.getExceptionLog(GOOGLE_RECEIVE_EMAIL_WEBHOOK, request, e);
        }
        return ResponseEntity.status(httpStatus.value()).body(response);
    }

    @GetMapping(value = GOOGLE_OAUTH_REDIRECT_URI)
    public ResponseEntity<GoogleOauthResponse> handleGoogleOauthCallback(@RequestParam String code) {
        GoogleOauthResponse googleOauthResponse = GoogleOauthResponse.builder().build();
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            LogUtils.getRequestLog(GOOGLE_RECEIVE_EMAIL_WEBHOOK, code);
            googleOauthResponse = googleService.handleGoogleOauthCallback(code);
            LogUtils.getResponseLog(GOOGLE_RECEIVE_EMAIL_WEBHOOK, googleOauthResponse);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            LogUtils.getExceptionLog(GOOGLE_OAUTH_REDIRECT_URI, code, e);
        }
        return ResponseEntity.status(httpStatus.value()).body(googleOauthResponse);
    }

    //TODO: Remove below controller if not using
    @GetMapping("/test")
    public void f() {
        databaseHelper.saveConversationalHistoryData(new MessageRecievedPojo(), "sorce", "opon");
        emailSenderService.sendEmail("paragbhardwaj5@gmail.com", "test_sub", "test_text", false);
    }
}
