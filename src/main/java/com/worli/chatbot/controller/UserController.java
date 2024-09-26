package com.worli.chatbot.controller;

import com.worli.chatbot.request.IsUserVerifiedRequest;
import com.worli.chatbot.response.GoogleOauthResponse;
import com.worli.chatbot.response.IsUserVerifiedResponse;
import com.worli.chatbot.service.UserService;
import com.worli.chatbot.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.worli.chatbot.constants.APIConstants.GOOGLE_OAUTH_REDIRECT_URI;
import static com.worli.chatbot.constants.APIConstants.GOOGLE_RECEIVE_EMAIL_WEBHOOK;
import static com.worli.chatbot.constants.APIConstants.UserAPIs.IS_USER_VERIFIED;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    @PostMapping(value = IS_USER_VERIFIED)
    public ResponseEntity<IsUserVerifiedResponse> isUserVerified(@RequestBody IsUserVerifiedRequest request) {
        IsUserVerifiedResponse isUserVerifiedResponse = IsUserVerifiedResponse.builder().build();
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            LogUtils.getRequestLog(IS_USER_VERIFIED, request);
            isUserVerifiedResponse = userService.isVerifiedUser(request);
            LogUtils.getResponseLog(IS_USER_VERIFIED, isUserVerifiedResponse);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            LogUtils.getExceptionLog(IS_USER_VERIFIED, request, e);
        }
        return ResponseEntity.status(httpStatus.value()).body(isUserVerifiedResponse);
    }
}
