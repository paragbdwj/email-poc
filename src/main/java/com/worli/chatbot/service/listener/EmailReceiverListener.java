package com.worli.chatbot.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.service.ChatAggregatorService;
import com.worli.chatbot.utils.SerializationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailReceiverListener {
    private final ChatAggregatorService chatAggregatorService;
    private final ObjectMapper objectMapper;
    @KafkaListener(
            topics = "${kafka.topic.email-receiver}",
            groupId = "my-group",
            autoStartup = "#{T(java.lang.Boolean).parseBoolean('${kafka.listener.enabled.email-receiver}')}"  // Uses the property to enable/disable
    )
    public void emailReceiverListener(ConsumerRecord<String, String> record) {
        log.info("In the function emailReceiverListener, received message : {}", record);
        try {
            String jsonString = record.value().substring(1, record.value().length() - 1);
            MessageRecievedPojo messageRecievedPojo = SerializationUtils.deserializeMessageReceivedPojo(jsonString);
            chatAggregatorService.processMessageReceived(messageRecievedPojo);
        } catch (Exception e) {
            log.info("Got exception in emailReceiverListener for record : {}", record);
        }
    }
}
