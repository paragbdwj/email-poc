package com.worli.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.model.GoogleResponseObject;
import com.worli.chatbot.mongo.models.UserTokenData;
import com.worli.chatbot.mongo.models.UserProfileData;
import com.worli.chatbot.request.GetProfileDataRequest;
import com.worli.chatbot.request.GetTokenRequest;
import com.worli.chatbot.request.GoogleAuthRequest;
import com.worli.chatbot.response.GetProfileDataResponse;
import com.worli.chatbot.response.GoogleAuthResponse;
import com.worli.chatbot.response.GoogleGetTokenResponse;
import com.worli.chatbot.response.GoogleOauthResponse;
import com.worli.chatbot.service.google.GetProfileDataService;
import com.worli.chatbot.service.google.GetTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleService {
    private final DatabaseHelper databaseHelper;
    private final ObjectMapper objectMapper;
    private final ApplicationProperties applicationProperties;
    private final GetTokenService getTokenService;
    private final GetProfileDataService getProfileDataService;

    public GoogleAuthResponse consumeGoogleAuthResponse(GoogleAuthRequest request) throws JsonProcessingException {
        GoogleResponseObject googleResponseObject = objectMapper.readValue(request.getResponseFromGoogle(), GoogleResponseObject.class);
        databaseHelper.saveOrUpdateGoogleAuthResponseData(googleResponseObject);
        return GoogleAuthResponse.builder()
                .success(true)
                .build();
    }

    public GoogleOauthResponse handleGoogleOauthCallback(String code) throws JsonProcessingException {
        GoogleGetTokenResponse googleGetTokenResponse = getTokenService.getGoogleTokenResponse(GetTokenRequest.builder().code(code).grantType("authorization_code").build());
        UserTokenData userTokenData = databaseHelper.saveOrUpdateGoogleTokenData(googleGetTokenResponse);
        UserProfileData userProfileData = saveUserProfileData(userTokenData);
        return GoogleOauthResponse.builder()
                .verificationId(userTokenData.getVerificationId())
                .userProfileData(userProfileData)
                .build();
    }

    // TODO : scope for parallelization ???
    private UserProfileData saveUserProfileData(UserTokenData userTokenData) throws JsonProcessingException {
        GetProfileDataResponse getProfileDataResponse = getProfileDataService.getProfileData(GetProfileDataRequest.builder()
                .accessToken(userTokenData.getAccessToken())
                .build());
        return databaseHelper.saveOrUpdateUserProfileData(getProfileDataResponse, userTokenData.getUserId());
    }
}
