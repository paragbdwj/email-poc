package com.worli.chatbot.service;

import com.worli.chatbot.mongo.models.ConversationalHistoryData;
import com.worli.chatbot.mongo.repository.ConversationHistoryDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MongoHelper {
    private final ConversationHistoryDataRepository repository;

    public List<ConversationalHistoryData> getDocumentsUntilIntentFulfilled(String email, int maxChats) {
        List<ConversationalHistoryData> result = new ArrayList<>();

        // Create a Pageable instance to limit the number of results
        Page<ConversationalHistoryData> dataPage = repository.findByEmail(email, PageRequest.of(0, maxChats, Sort.by(Sort.Direction.DESC, "createdAt")));

        for (ConversationalHistoryData data : dataPage.getContent()) {
            if (data.isIntentFulfilled()) {
                break;
            }
            result.add(data);
            // Stop if isIntentFulfilled is true
        }
        return result;
    }
}
