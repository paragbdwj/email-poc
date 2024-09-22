package com.worli.chatbot.mongo.repository;

import com.worli.chatbot.mongo.models.UserIdCounter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserIdCounterRepository extends MongoRepository<UserIdCounter, Integer> {
}
