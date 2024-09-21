package com.worli.chatbot.utils;

import com.worli.chatbot.model.MessageRecievedPojo;

public class SerializationUtils {

    private static final String DELIMITER = "$%~$%";

    // Serialize function
    public static String serializeMessageReceivedPojo(MessageRecievedPojo messageRecievedPojo) {
        return messageRecievedPojo.getMessage() + DELIMITER + messageRecievedPojo.getSubject() + DELIMITER + messageRecievedPojo.getEmail();
    }

    // Deserialize function
    public static MessageRecievedPojo deserializeMessageReceivedPojo(String str) {
        String[] parts = str.split("\\" + DELIMITER);
        String message = parts[0];
        String subject = parts[1];
        String email = parts[2];
        return MessageRecievedPojo.builder()
                .email(email)
                .message(message)
                .subject(subject)
                .build();
    }
}
