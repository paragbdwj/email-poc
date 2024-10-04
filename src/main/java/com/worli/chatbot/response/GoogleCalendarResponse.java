package com.worli.chatbot.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
public class GoogleCalendarResponse {
    private String kind;
    private String etag;
    private String summary;
    private String updated;
    private String timeZone;
    private String accessRole;
    private List<Event> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Event {
        private String id;
        //TODO: remove this priority score
        private double priorityScore;
        private String status;
        private String summary;
        private String location;
        private String description;
        private DateTime start;
        private DateTime end;
        private Organizer organizer;
        private Creator creator;
        private String visibility;
        private String hangoutLink;
        private List<Attendee> attendees;
        private Reminders reminders;
        private String recurrence;
        private Boolean recurringEvent;
        private String colorId;
        private String created;
        private String updated;
        private String eventType;
        private String htmlLink; // Add this to include the event's link to Google Calendar UI
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateTime {
        private String dateTime;  // e.g., "2024-09-20T10:00:00Z"
        private String timeZone;  // e.g., "America/New_York"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Organizer {
        private String email;
        private String displayName;
        private Boolean self;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Creator {
        private String email;
        private String displayName;
        private Boolean self;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attendee {
        private String email;
        private String displayName;
        private String responseStatus;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Reminders {
        private Boolean useDefault;
        private List<Override> overrides;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(Include.NON_NULL)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Override {
            private String method;  // e.g., "email", "popup"
            private Integer minutes;  // Reminder time before the event
        }
    }
}
