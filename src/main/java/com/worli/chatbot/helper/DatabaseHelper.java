package com.worli.chatbot.helper;

import com.worli.chatbot.model.GoogleResponseObject;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.mongo.models.ConversationalHistoryData;
import com.worli.chatbot.mongo.models.UserGoogleData;
import com.worli.chatbot.mongo.models.UserGoogleTokenData;
import com.worli.chatbot.mongo.models.UserProfileData;
import com.worli.chatbot.mongo.repository.ConversationHistoryDataRepository;
import com.worli.chatbot.mongo.repository.UserGoogleDataRepository;
import com.worli.chatbot.mongo.repository.UserGoogleTokenDataRepository;
import com.worli.chatbot.mongo.repository.UserProfileDataRepository;
import com.worli.chatbot.response.GetProfileDataResponse;
import com.worli.chatbot.response.GoogleGetTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseHelper {
    private final UserGoogleDataRepository userGoogleDataRepository;
    private final UserGoogleTokenDataRepository userGoogleTokenDataRepository;
    private final UserProfileDataRepository userProfileDataRepository;
    private final ConversationHistoryDataRepository conversationHistoryDataRepository;

    public void saveOrUpdateGoogleAuthResponseData(GoogleResponseObject googleResponseObject) {
        UserGoogleData userGoogleData = userGoogleDataRepository.findByGoogleId(googleResponseObject.getGoogleId());
        if(Objects.isNull(userGoogleData)) {
             userGoogleData = userGoogleDataRepository.save(UserGoogleData.builder()
                    .tokenId(googleResponseObject.getTokenId())
                    .accessToken(googleResponseObject.getAccessToken())
                    .googleId(googleResponseObject.getGoogleId())
                    .build());
        } else {
            if(Objects.nonNull(googleResponseObject.getAccessToken())) {
                userGoogleData.setAccessToken(googleResponseObject.getAccessToken());
            }
            if(Objects.nonNull(googleResponseObject.getTokenId())) {
                userGoogleData.setTokenId(googleResponseObject.getTokenId());
            }
            userGoogleData = userGoogleDataRepository.save(userGoogleData);
        }
        log.info("updated user_google_data : {} in mongo", userGoogleData);
    }

    public UserGoogleTokenData saveOrUpdateGoogleTokenData(GoogleGetTokenResponse getTokenResponse) {
        UserGoogleTokenData userGoogleTokenData = userGoogleTokenDataRepository.findByGoogleId(getTokenResponse.getGoogleId());
        if(Objects.isNull(userGoogleTokenData)) {
            userGoogleTokenData = userGoogleTokenDataRepository.save(UserGoogleTokenData.builder()
                            .idToken(getTokenResponse.getIdToken())
                            .tokenType(getTokenResponse.getTokenType())
                            .scope(getTokenResponse.getScope())
                            .expiresIn(getTokenResponse.getExpiresIn())
                            .refreshToken(getTokenResponse.getRefreshToken())
                            .accessToken(getTokenResponse.getAccessToken())
                            .googleId(getTokenResponse.getGoogleId())
                    .build());
        } else {
            Optional.ofNullable(getTokenResponse.getAccessToken()).ifPresent(userGoogleTokenData::setAccessToken);
            Optional.ofNullable(getTokenResponse.getScope()).ifPresent(userGoogleTokenData::setScope);
            userGoogleTokenData = userGoogleTokenDataRepository.save(userGoogleTokenData);
        }
        return userGoogleTokenData;
    }

    public UserProfileData saveOrUpdateUserProfileData(GetProfileDataResponse getProfileDataResponse){
        UserProfileData userProfileData = userProfileDataRepository.findByGoogleId(getProfileDataResponse.getGoogleId());
        if(Objects.isNull(userProfileData)) {
            userProfileData = UserProfileData.builder()
                    .googleId(userProfileData.getGoogleId())
                    .email(userProfileData.getEmail())
                    .familyName(userProfileData.getFamilyName())
                    .picture(userProfileData.getPicture())
                    .verifiedEmail(userProfileData.isVerifiedEmail())
                    .givenName(userProfileData.getGivenName())
                    .name(userProfileData.getName())
                    .build();
        } else {
            userProfileData.setEmail(userProfileData.getEmail());
            userProfileData.setName(userProfileData.getName());
            userProfileData.setPicture(userProfileData.getPicture());
            userProfileData.setVerifiedEmail(userProfileData.isVerifiedEmail());
            userProfileData.setGivenName(userProfileData.getGivenName());
            userProfileData.setFamilyName(userProfileData.getFamilyName());
        }
        return userProfileDataRepository.save(userProfileData);
    }

    public List<ConversationalHistoryData> getTopConversationalDatas(String email, int n) {
        return conversationHistoryDataRepository.findTopConversationalHistoryDataByEmailOrderByCreatedAtDesc(email, PageRequest.of(0, 20));
    }

    public ConversationalHistoryData saveConversationalHistoryData(MessageRecievedPojo messageRecievedPojo, String source, String openAiResponse) {
        try {
            return conversationHistoryDataRepository.save(ConversationalHistoryData.builder()
                            .source(source)
                            .email(messageRecievedPojo.getEmail())
                            .message(messageRecievedPojo.getMessage())
                            .subject(messageRecievedPojo.getSubject())
                            .responseFromOpenAi(openAiResponse)
                            .createdAt(System.currentTimeMillis())
                    .build());
        } catch (Exception e) {
            log.error("Error while saving ConversationalHistoryData for request : {}", messageRecievedPojo);
            return null;
        }
    }

}
