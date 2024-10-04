package com.worli.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field.Str;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRecievedPojo {
    private String message;
    private String email;
    private String userName;
    private String subject;

    //Meta-data
    private String googleAccessToken;
    private boolean isSenderPartOfWorli;
    private boolean isReceiverPartOfWorli; // Soon, we'll have to handle one to many usecase
}
