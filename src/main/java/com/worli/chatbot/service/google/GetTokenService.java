package com.worli.chatbot.service.google;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.config.http.HttpConnectionConfig;
import com.worli.chatbot.config.http.HttpConnectionProperties;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.constants.BeanNames.Rest;
import com.worli.chatbot.request.GetTokenRequest;
import com.worli.chatbot.response.GoogleGetTokenResponse;
import com.worli.chatbot.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.worli.chatbot.constants.CommonConstants.GRANT_TYPE_AUTHORIZATION_CODE;
import static com.worli.chatbot.constants.CommonConstants.GRANT_TYPE_REFRESH_TOKEN;

@Service
@Slf4j
public class GetTokenService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpConnectionConfig httpConnectionConfig;
    private final ApplicationProperties applicationProperties;

    public GetTokenService(@Qualifier(Rest.GOOGLE_GET_TOKEN_REST_TEMPLATE)  RestTemplate restTemplate, ObjectMapper objectMapper, HttpConnectionProperties httpConnectionProperties, ApplicationProperties applicationProperties) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.httpConnectionConfig = httpConnectionProperties.getGoogleGetTokenConfig();
        this.applicationProperties = applicationProperties;
    }

    public GoogleGetTokenResponse getGoogleTokenResponse(GetTokenRequest getTokenRequest) throws JsonProcessingException {
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(getFormParams(getTokenRequest), getHeaders());
        ResponseEntity<String> response = restTemplate.exchange(httpConnectionConfig.getUrl(), HttpMethod.POST, request, String.class);
        GoogleGetTokenResponse getTokenResponse = null;
        if(response.getStatusCode().is2xxSuccessful()) {
            getTokenResponse = objectMapper.readValue(response.getBody(), GoogleGetTokenResponse.class);
        } else {
            LogUtils.downstreamUnsuccsessfulResponse(httpConnectionConfig.getUrl(), getTokenRequest, response.getBody());
        }
        return getTokenResponse;
    }

    private MultiValueMap<String, String> getFormParams(GetTokenRequest getTokenRequest) {
        MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
        String grantType = getTokenRequest.getGrantType();
        formParams.add("grant_type", grantType);
        if(GRANT_TYPE_AUTHORIZATION_CODE.equals(grantType)) {
            formParams.add(GRANT_TYPE_AUTHORIZATION_CODE, getTokenRequest.getCode());
        } else if(GRANT_TYPE_REFRESH_TOKEN.equals(grantType)) {
            formParams.add(GRANT_TYPE_REFRESH_TOKEN, getTokenRequest.getRefreshToken());
        }
        formParams.add("client_id", applicationProperties.getGoogleOauthClientId());
        formParams.add("client_secret", applicationProperties.getGoogleOauthClientSecret());
        formParams.add("redirect_uri", applicationProperties.getGoogleOauthRedirectUri());
        return formParams;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }
}
