package com.worli.chatbot.service.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.config.http.HttpConnectionConfig;
import com.worli.chatbot.config.http.HttpConnectionProperties;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.request.GoogleCalendarRequest;
import com.worli.chatbot.response.GoogleCalendarResponse;
import com.worli.chatbot.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.worli.chatbot.constants.BeanNames.Rest.GOOGLE_CALENDAR_REST_TEMPLATE;

@Service
@Slf4j
public class GoogleCalendarService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpConnectionConfig httpConnectionConfig;
    private final ApplicationProperties applicationProperties;

    public GoogleCalendarService(@Qualifier(GOOGLE_CALENDAR_REST_TEMPLATE)  RestTemplate restTemplate, ObjectMapper objectMapper, HttpConnectionProperties httpConnectionProperties, ApplicationProperties applicationProperties, ApplicationProperties applicationProperties1) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.httpConnectionConfig = httpConnectionProperties.getGoogleCalendarConfig();
        this.applicationProperties = applicationProperties1;
    }

    public GoogleCalendarResponse googleCalendar(GoogleCalendarRequest request) {
        HttpEntity<GoogleCalendarRequest> requestBody = new HttpEntity<>(request, getHeaders(request));
        String url = String.format(
                applicationProperties.getGoogleCalendarEventsUrl(),
                request.getMaxResults(),
                request.getOrderBy(),
                request.getSingleEvents(),
                request.getTimeMin(),
                request.getTimeMax()
        );
        GoogleCalendarResponse googleCalendarResponse = null;
        try {
            log.info("calling google_calendar request : {}", request);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestBody, String.class);
            googleCalendarResponse = objectMapper.readValue(response.getBody(), GoogleCalendarResponse.class);
            log.info("gotten google_calendar response : {}", response);
        } catch (Exception e) {
            LogUtils.downstreamUnsuccsessfulResponse(httpConnectionConfig.getUrl(), request, googleCalendarResponse);
        }
        return googleCalendarResponse;
    }

    public HttpHeaders getHeaders(GoogleCalendarRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, request.getAccessToken());
        return httpHeaders;
    }
}
