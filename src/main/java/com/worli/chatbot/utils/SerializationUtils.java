package com.worli.chatbot.utils;

import com.worli.chatbot.model.MessageRecievedPojo;

public class SerializationUtils {

    private static final String DELIMITER = "$%~$%";

    // Serialize function
    public static String serializeMessageReceivedPojo(MessageRecievedPojo messageRecievedPojo) {
        if (messageRecievedPojo == null) {
            return null; // or throw an exception if you prefer
        }
        String message = messageRecievedPojo.getMessage() != null ? messageRecievedPojo.getMessage() : "";
        String subject = messageRecievedPojo.getSubject() != null ? messageRecievedPojo.getSubject() : "";
        String email = messageRecievedPojo.getEmail() != null ? messageRecievedPojo.getEmail() : "";

        return message + DELIMITER + subject + DELIMITER + email;
    }

    // Deserialize function
    public static MessageRecievedPojo deserializeMessageReceivedPojo(String str) {
        if (str == null || str.isEmpty()) {
            return null; // or throw an exception if you prefer
        }

        String[] parts = str.split("\\" + DELIMITER);

        // Error handling for unexpected input format
        if (parts.length < 3) {
            throw new IllegalArgumentException("Invalid input string for deserialization: " + str);
        }

        String message = parts[0];
        String subject = parts[1];
        String email = parts[2];

        return MessageRecievedPojo.builder()
                .email(email.isEmpty() ? null : email) // Set to null if empty
                .message(message.isEmpty() ? null : message) // Set to null if empty
                .subject(subject.isEmpty() ? null : subject) // Set to null if empty
                .build();
    }

}
