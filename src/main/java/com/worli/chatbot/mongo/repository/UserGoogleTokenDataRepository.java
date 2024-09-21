package com.worli.chatbot.mongo.repository;

import com.worli.chatbot.mongo.models.UserGoogleTokenData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGoogleTokenDataRepository extends MongoRepository<UserGoogleTokenData, Integer> {
    UserGoogleTokenData findByGoogleId(String googleId);
}
