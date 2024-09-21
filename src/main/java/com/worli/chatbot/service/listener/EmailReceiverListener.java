package com.worli.chatbot.service.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailReceiverListener {
    @KafkaListener(
            topics = "test-topic",
            groupId = "my-group",
            autoStartup = "#{T(java.lang.Boolean).parseBoolean('${kafka.listener.enabled.email-receiver}')}"  // Uses the property to enable/disable
    )
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("Received message: " + record.value());
    }
}
