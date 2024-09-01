package com.worli.chatbot.model;

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
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleResponseObject {
        private String ca;
        private Xc xc;
        private Wt wt;
        private String googleId;
        private TokenObj tokenObj;
        private String tokenId;
        private String accessToken;
        private ProfileObj profileObj;

        @Data
        public static class Xc {
            private String tokenType;
            private String accessToken;
            private String scope;
            private String loginHint;
            private int expiresIn;
            private String idToken;
            private SessionState sessionState;
            private long firstIssuedAt;
            private long expiresAt;
            private String idpId;
        }

        @Data
        public static class SessionState {
            private ExtraQueryParams extraQueryParams;
        }

        @Data
        public static class ExtraQueryParams {
            private String authuser;
        }

        @Data
        public static class Wt {
            private String nt;
            private String ad;
            private String rv;
            private String ut;
            private String cu;
        }

        @Data
        @JsonNaming(SnakeCaseStrategy.class)
        public static class TokenObj {
            private String tokenType;
            private String accessToken;
            private String scope;
            private String loginHint;
            private int expiresIn;
            private String idToken;
            private SessionState sessionState;
            private long firstIssuedAt;
            private long expiresAt;
            private String idpId;
        }

        @Data
        public static class ProfileObj {
            private String googleId;
            private String email;
            private String name;
            private String givenName;
            private String familyName;
        }
}
