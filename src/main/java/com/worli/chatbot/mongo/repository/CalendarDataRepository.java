package com.worli.chatbot.mongo.repository;

import com.worli.chatbot.mongo.models.CalendarData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarDataRepository extends MongoRepository<CalendarData, String> {
    List<CalendarData> findByUserIdAndStartEpochTimestampBetween(Long userId, Long startEpoch, Long endEpoch);
}
