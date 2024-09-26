package com.worli.chatbot.service;

import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.mongo.models.UserProfileData;
import com.worli.chatbot.request.IsUserVerifiedRequest;
import com.worli.chatbot.response.IsUserVerifiedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final DatabaseHelper databaseHelper;

    public IsUserVerifiedResponse isVerifiedUser(IsUserVerifiedRequest request) {
        UserProfileData response = databaseHelper.findByEmail(request.getEmail());
        return IsUserVerifiedResponse.builder()
                .isUserVerified(Objects.nonNull(response))
                .build();
    }
}
