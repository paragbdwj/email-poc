package com.worli.chatbot.service.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.config.http.HttpConnectionConfig;
import com.worli.chatbot.config.http.HttpConnectionProperties;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.constants.BeanNames.Rest;
import com.worli.chatbot.request.GetProfileDataRequest;
import com.worli.chatbot.response.GetProfileDataResponse;
import com.worli.chatbot.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class GetProfileDataService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpConnectionConfig httpConnectionConfig;
    private final ApplicationProperties applicationProperties;

    public GetProfileDataService(@Qualifier(Rest.GOOGLE_GET_PROFILE_DATA_REST_TEMPLATE)  RestTemplate restTemplate, ObjectMapper objectMapper, HttpConnectionProperties httpConnectionProperties, ApplicationProperties applicationProperties) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.httpConnectionConfig = httpConnectionProperties.getGoogleGetProfileConfig();
        this.applicationProperties = applicationProperties;
    }

    public GetProfileDataResponse getProfileData(GetProfileDataRequest getProfileDataRequest) throws JsonProcessingException {
        String requestUrl = UriComponentsBuilder.fromHttpUrl(httpConnectionConfig.getUrl())
                .queryParam("access_token", getProfileDataRequest.getAccessToken())
                .toUriString();
        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
        GetProfileDataResponse getProfileDataResponse = null;
        if(response.getStatusCode().is2xxSuccessful()) {
            getProfileDataResponse = objectMapper.readValue(response.getBody(), GetProfileDataResponse.class);
        } else {
            LogUtils.downstreamUnsuccsessfulResponse(httpConnectionConfig.getUrl(), getProfileDataRequest, response.getBody());
        }
        return getProfileDataResponse;
    }
}
