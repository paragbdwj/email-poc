package com.worli.chatbot.mongo.repository;

import com.worli.chatbot.mongo.models.UserProfileData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileDataRepository extends MongoRepository<UserProfileData, Integer> {
    UserProfileData findByGoogleId(String googleId);
}
