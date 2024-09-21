package com.worli.chatbot.mongo.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.worli.chatbot.model.GoogleResponseObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

//TODO : think about deprecation of this database
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_google_data")
public class UserGoogleData {
    private String ca;
    private GoogleResponseObject.Xc xc;
    private GoogleResponseObject.Wt wt;
    private String googleId;  // this should be unique
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
        private GoogleResponseObject.SessionState sessionState;
        private long firstIssuedAt;
        private long expiresAt;
        private String idpId;
    }

    @Data
    public static class SessionState {
        private GoogleResponseObject.ExtraQueryParams extraQueryParams;
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
    public static class TokenObj {
        private String tokenType;
        private String accessToken;
        private String scope;
        private String loginHint;
        private int expiresIn;
        private String idToken;
        private GoogleResponseObject.SessionState sessionState;
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
