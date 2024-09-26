package com.worli.chatbot.mongo.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "calendar_data")
public class CalendarData {
    private Long userId;
    private Long meetingId;  // this will be google's event_id
    private Double priorityScore;
    private String currentStatus;
    private String location;
    private String agenda;
    private String meetingLink;
    private Long startEpochTimestamp;
    private Long endEpochTimestamp;
}
