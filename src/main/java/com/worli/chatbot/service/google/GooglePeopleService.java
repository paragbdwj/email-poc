package com.worli.chatbot.service.google;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.config.http.HttpConnectionConfig;
import com.worli.chatbot.config.http.HttpConnectionProperties;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.constants.BeanNames.Rest;
import com.worli.chatbot.request.GoogleCalendarRequest;
import com.worli.chatbot.request.GooglePeopleRequest;
import com.worli.chatbot.response.GooglePeopleResponse;
import com.worli.chatbot.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.worli.chatbot.constants.BeanNames.Rest.GOOGLE_PEOPLE_REST_TEMPLATE;

@Slf4j
@Service
public class GooglePeopleService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpConnectionConfig httpConnectionConfig;
    private final ApplicationProperties applicationProperties;

    public GooglePeopleService(@Qualifier(GOOGLE_PEOPLE_REST_TEMPLATE)  RestTemplate restTemplate, ObjectMapper objectMapper, HttpConnectionProperties httpConnectionProperties, ApplicationProperties applicationProperties) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.httpConnectionConfig = httpConnectionProperties.getGoogleCalendarConfig();
        this.applicationProperties = applicationProperties;
    }

    public GooglePeopleResponse googlePeople(GooglePeopleRequest request) {
        HttpEntity<GooglePeopleRequest> requestBody = new HttpEntity<>(request, getHeaders(request));
        String url = String.format(
                applicationProperties.getGooglePeopleUrl(),
                request.getCommaSeparatedPersonFields()
        );
        GooglePeopleResponse googlePeopleResponse = null;
        try {
            log.info("calling google_people api with request : {}", request);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestBody, String.class);
            googlePeopleResponse = objectMapper.readValue(response.getBody(), GooglePeopleResponse.class);
            log.info("gotten  google_people api response : {}", response);
        } catch (Exception e) {
            LogUtils.downstreamUnsuccsessfulResponse(httpConnectionConfig.getUrl(), request, googlePeopleResponse);
        }
        return googlePeopleResponse;
    }

    public HttpHeaders getHeaders(GooglePeopleRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, request.getAccessToken());
        return httpHeaders;
    }
}
