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
public class GooglePeopleResponse {
    private List<Person> connections;
    private String nextPageToken;
    private Integer totalItems;
    private Integer totalPeople;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Person {
        private List<Name> names;
        private List<EmailAddress> emailAddresses;
        private List<PhoneNumber> phoneNumbers;
        private List<Photo> photos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name {
        private String displayName;
        private String familyName;
        private String givenName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EmailAddress {
        private String value;
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PhoneNumber {
        private String value;
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Photo {
        private String url;
        private Boolean defaultPhoto;
    }
}
