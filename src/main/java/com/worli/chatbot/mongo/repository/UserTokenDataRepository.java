package com.worli.chatbot.mongo.repository;

import com.worli.chatbot.mongo.models.UserTokenData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenDataRepository extends MongoRepository<UserTokenData, Integer> {
    UserTokenData findByGoogleId(String googleId);
    UserTokenData findByVerificationId(String verificationId);

}
