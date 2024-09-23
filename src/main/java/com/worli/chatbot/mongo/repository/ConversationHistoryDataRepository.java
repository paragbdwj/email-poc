package com.worli.chatbot.mongo.repository;

import com.worli.chatbot.mongo.models.ConversationalHistoryData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationHistoryDataRepository extends MongoRepository<ConversationalHistoryData, String> {
    List<ConversationalHistoryData> findTopConversationalHistoryDataByEmailOrderByCreatedAtDesc(String email, Pageable pageable);
    Page<ConversationalHistoryData> findByEmail(String email, Pageable pageable);
}
