package com.worli.chatbot.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConvModelPromptResponse {
    private String fromEmail;
    private String toEmail;
    private String meetingTime;
    private String cancelMeetingDate;
    private String newMeetingDate;
    private String agenda;
    private String suggestedSlots;
    private String deadlineDate;
    private String fromName;
    private String toName;
    private int intent;
    private String fromPhone;
    private String subject;
    private String body;
    private boolean isFollowUp;
}
