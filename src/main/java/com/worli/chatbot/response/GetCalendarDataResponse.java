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

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCalendarDataResponse {
    List<MeetingData> meetingDataList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(SnakeCaseStrategy.class)
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeetingData {
        private Long meetingId;
        private Double priorityScore;
        private String currentStatus;
        private String location;
        private String agenda;
        private String meetingLink;
        private Long startEpochTimestamp;
        private Long endEpochTimestamp;
    }
}
