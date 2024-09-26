package com.worli.chatbot.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class APIConstants {
    public static final String GET_GOOGLE_AUTH_RESPONSE = "/google/response";
    public static final String GOOGLE_RECEIVE_EMAIL_WEBHOOK = "/google/receive-emails";
    public static final String GOOGLE_OAUTH_REDIRECT_URI = "/google/auth-callback";

    @UtilityClass
    public class HomePageAPIs {
        public static final String GET_CALENDAR_DATA = "user/calendar/get";
        public static final String UPDATE_MEETING_RESPONSE = "user/calendar/get";
    }

    @UtilityClass
    public class UserAPIs {
        public static final String IS_USER_VERIFIED = "/user/is-verified";
    }
}
