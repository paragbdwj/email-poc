package com.worli.chatbot.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanNames {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Rest {
        public static final String LLM_CONVERSATIONAL_REST_TEMPLATE = "llmConversationalRestTemplate";
        public static final String GOOGLE_GET_TOKEN_REST_TEMPLATE = "googleGetTokenRestTemplate";
        public static final String GOOGLE_GET_PROFILE_DATA_REST_TEMPLATE = "googleGetProfileDataRestTemplate";
        public static final String GOOGLE_CALENDAR_REST_TEMPLATE = "googleCalendarRestTemplate";
        public static final String GOOGLE_PEOPLE_REST_TEMPLATE = "googlePeopleRestTemplate";
    }
}
