package com.worli.chatbot.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.model.GoogleResponseObject;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.mongo.models.CalendarData;
import com.worli.chatbot.mongo.models.ConversationalHistoryData;
import com.worli.chatbot.mongo.models.UserGoogleData;
import com.worli.chatbot.mongo.models.UserTokenData;
import com.worli.chatbot.mongo.models.UserIdCounter;
import com.worli.chatbot.mongo.models.UserProfileData;
import com.worli.chatbot.mongo.repository.CalendarDataRepository;
import com.worli.chatbot.mongo.repository.ConversationHistoryDataRepository;
import com.worli.chatbot.mongo.repository.UserGoogleDataRepository;
import com.worli.chatbot.mongo.repository.UserTokenDataRepository;
import com.worli.chatbot.mongo.repository.UserProfileDataRepository;
import com.worli.chatbot.request.GetTokenRequest;
import com.worli.chatbot.response.ConvModelPromptResponse;
import com.worli.chatbot.response.GetProfileDataResponse;
import com.worli.chatbot.response.GoogleGetTokenResponse;
import com.worli.chatbot.service.google.GetTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseHelper {
    private final UserGoogleDataRepository userGoogleDataRepository;
    private final UserTokenDataRepository userTokenDataRepository;
    private final UserProfileDataRepository userProfileDataRepository;
    private final ConversationHistoryDataRepository conversationHistoryDataRepository;
    private final MongoTemplate mongoTemplate;
    private final GetTokenService getTokenService;
    private final ObjectMapper objectMapper;
    private final CalendarDataRepository calendarDataRepository;

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

    public UserTokenData saveOrUpdateGoogleTokenData(GoogleGetTokenResponse getTokenResponse) {
        UserTokenData userTokenData = userTokenDataRepository.findByGoogleId(getTokenResponse.getGoogleId());
        if(Objects.isNull(userTokenData)) {
            Long userId = getAndIncrementUserIdCounterByOne();
            userTokenData = userTokenDataRepository.save(UserTokenData.builder()
                            .idToken(getTokenResponse.getIdToken())
                            .tokenType(getTokenResponse.getTokenType())
                            .scope(getTokenResponse.getScope())
                            .expiresIn((System.currentTimeMillis()/1000) + getTokenResponse.getExpiresIn())
                            .refreshToken(getTokenResponse.getRefreshToken())
                            .accessToken(getTokenResponse.getAccessToken())
                            .googleId(getTokenResponse.getGoogleId())
                            .userId(userId)
                            .verificationId(UUID.randomUUID().toString())
                    .build());
        } else {
            Optional.ofNullable(getTokenResponse.getAccessToken()).ifPresent(userTokenData::setAccessToken);
            Optional.ofNullable(getTokenResponse.getScope()).ifPresent(userTokenData::setScope);
            userTokenData.setExpiresIn(getTokenResponse.getExpiresIn() + (System.currentTimeMillis()/1000));
            userTokenData = userTokenDataRepository.save(userTokenData);
        }
        return userTokenData;
    }

    public UserProfileData saveOrUpdateUserProfileData(GetProfileDataResponse getProfileDataResponse, Long userId){
        UserProfileData userProfileData = userProfileDataRepository.findByGoogleId(getProfileDataResponse.getGoogleId());
        if(Objects.isNull(userProfileData)) {
            userProfileData = UserProfileData.builder()
                    .googleId(getProfileDataResponse.getGoogleId())
                    .email(getProfileDataResponse.getEmail())
                    .familyName(getProfileDataResponse.getFamilyName())
                    .picture(getProfileDataResponse.getPicture())
                    .verifiedEmail(getProfileDataResponse.isVerifiedEmail())
                    .givenName(getProfileDataResponse.getGivenName())
                    .name(getProfileDataResponse.getName())
                    .userId(userId)
                    .build();
        } else {
            userProfileData.setEmail(getProfileDataResponse.getEmail());
            userProfileData.setName(getProfileDataResponse.getName());
            userProfileData.setPicture(getProfileDataResponse.getPicture());
            userProfileData.setVerifiedEmail(getProfileDataResponse.isVerifiedEmail());
            userProfileData.setGivenName(getProfileDataResponse.getGivenName());
            userProfileData.setFamilyName(getProfileDataResponse.getFamilyName());
            userProfileData.setGoogleId(getProfileDataResponse.getGoogleId());
            userProfileData.setUserId(userId);
        }
        return userProfileDataRepository.save(userProfileData);
    }

    public List<ConversationalHistoryData> getTopConversationalDatas(String email, int n) {
        return conversationHistoryDataRepository.findTopConversationalHistoryDataByEmailOrderByCreatedAtDesc(email, PageRequest.of(0, 20));
    }

    public ConversationalHistoryData saveConversationalHistoryData(MessageRecievedPojo messageRecievedPojo, String source, ConvModelPromptResponse openAiResponse) {
        try {
            return conversationHistoryDataRepository.save(ConversationalHistoryData.builder()
                            .source(source)
                            .email(messageRecievedPojo.getEmail())
                            .message(messageRecievedPojo.getMessage())
                            .subject(messageRecievedPojo.getSubject())
                            .responseFromOpenAi(objectMapper.writeValueAsString(openAiResponse))
                            .isIntentFulfilled(isIntentFulfilled(openAiResponse))
                            .createdAt(System.currentTimeMillis())
                    .build());
        } catch (Exception e) {
            log.error("Error while saving ConversationalHistoryData for request : {}", messageRecievedPojo);
            return null;
        }
    }

    private boolean isIntentFulfilled(ConvModelPromptResponse openAiResponse) {
        if(openAiResponse.getIntent() == 4) {
            return false;
        } else if(openAiResponse.getIntent() == 1) {
            return !CollectionUtils.isEmpty(openAiResponse.getParticipants());
        }
        return false;
    }

    public Long getAndIncrementUserIdCounterByOne() {
        Query query = new Query();
        // Define the update operation to increment the counter by 1
        Update update = new Update().inc("counter", 1);
        // Execute the findAndModify operation
        UserIdCounter updatedCounter = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true), // Return the updated document
                UserIdCounter.class
        );
        // If no counter is found, create a new document with counter starting from 1
        if (updatedCounter == null) {
            UserIdCounter newCounter = UserIdCounter.builder().counter(1L).build();
            mongoTemplate.save(newCounter);
            return 1L;
        }
        // Return the new incremented value
        return updatedCounter.getCounter();
    }

    public UserTokenData findByVerificationId(String verificationId) {
        return userTokenDataRepository.findByVerificationId(verificationId);
    }

    public UserTokenData findUserTokenByUserId(Long userId) {
        return userTokenDataRepository.findByUserId(userId);
    }

    public UserTokenData checkAndUpdateAccessTokenIfExpired(UserTokenData userTokenData) throws JsonProcessingException {
        if((System.currentTimeMillis()/1000) + 100 > userTokenData.getExpiresIn()) {
            GoogleGetTokenResponse googleGetTokenResponse = getTokenService.getGoogleTokenResponse(GetTokenRequest.builder().refreshToken(userTokenData.getRefreshToken())
                            .grantType("refresh_token")
                    .build());
            userTokenData.setAccessToken(googleGetTokenResponse.getAccessToken());
            return userTokenDataRepository.save(userTokenData);
        }
        return userTokenData;
    }

    public UserProfileData findByEmail(String email) {
        return userProfileDataRepository.findByEmail(email);
    }
    public List<CalendarData> getCalendarDataAfterNSeconds(Long userId, Long timeInSeconds) {
        Long currEpochSeconds = System.currentTimeMillis()/1000;
        return calendarDataRepository.findByUserIdAndStartEpochTimestampBetween(userId, currEpochSeconds, currEpochSeconds + timeInSeconds);
    }
}
