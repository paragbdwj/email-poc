package com.worli.chatbot.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(SnakeCaseStrategy.class)
public class PriorityModelRequest {
    private String eventId;
    private String status;
    private String summary;
    private String location;
    private String description;
    private String start;          // Date and time in String format
    private String end;            // Date and time in String format
    private String organizer;
    private String organizerEmail;
    private String creatorEmail;
    private String visibility;
    private String hangoutLink;
    private List<String> attendees;
    private List<String> reminders;
    private String isRecurring;
    private String frequency;
    private String colorId;
    private String createdDate;
    private String updatedDate;
    private String eventType;
}
