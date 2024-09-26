package com.worli.chatbot.controller;

import com.worli.chatbot.response.GetCalendarDataResponse;
import com.worli.chatbot.response.GoogleOauthResponse;
import com.worli.chatbot.service.HomePageService;
import com.worli.chatbot.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.worli.chatbot.constants.APIConstants.GOOGLE_OAUTH_REDIRECT_URI;
import static com.worli.chatbot.constants.APIConstants.GOOGLE_RECEIVE_EMAIL_WEBHOOK;
import static com.worli.chatbot.constants.APIConstants.HomePageAPIs.GET_CALENDAR_DATA;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HomePageController {
    private final HomePageService homePageService;

    @GetMapping(GET_CALENDAR_DATA)
    public ResponseEntity<GetCalendarDataResponse> getCalendarData() {
        String userId = MDC.get("user-id");
        String accessToken = MDC.get("access-token");
        GetCalendarDataResponse getCalendarDataResponse = GetCalendarDataResponse.builder().build();
        HttpStatus httpStatus = HttpStatus.OK;
        try {
            LogUtils.getRequestLog(GET_CALENDAR_DATA, userId);
            getCalendarDataResponse = homePageService.getCalendarData(userId, accessToken);
            LogUtils.getResponseLog(GET_CALENDAR_DATA, getCalendarDataResponse);
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            LogUtils.getExceptionLog(GET_CALENDAR_DATA, userId, e);
        }
        return ResponseEntity.status(httpStatus.value()).body(getCalendarDataResponse);
    }

}
