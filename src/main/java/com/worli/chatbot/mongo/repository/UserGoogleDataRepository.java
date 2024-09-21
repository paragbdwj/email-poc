package com.worli.chatbot.mongo.repository;

import com.worli.chatbot.mongo.models.UserGoogleData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserGoogleDataRepository extends MongoRepository<UserGoogleData, Integer> {
    UserGoogleData findByGoogleId(String googleId);
}
