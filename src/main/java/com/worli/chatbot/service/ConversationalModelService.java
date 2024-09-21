package com.worli.chatbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.config.http.HttpConnectionConfig;
import com.worli.chatbot.config.http.HttpConnectionProperties;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.request.OpenAIChatCompletionRequest;
import com.worli.chatbot.response.OpenAIChatCompletionResponse;
import com.worli.chatbot.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.worli.chatbot.constants.BeanNames.Rest.LLM_CONVERSATIONAL_REST_TEMPLATE;

@Service
@Slf4j
public class ConversationalModelService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HttpConnectionConfig httpConnectionConfig;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public ConversationalModelService(HttpConnectionProperties httpConnectionProperties, @Qualifier(LLM_CONVERSATIONAL_REST_TEMPLATE) RestTemplate restTemplate, ObjectMapper objectMapper, ApplicationProperties applicationProperties) {
        this.restTemplate = restTemplate;
        this.httpConnectionConfig = httpConnectionProperties.getLlmConversationalConfig();
        this.objectMapper = objectMapper;
        this.applicationProperties = applicationProperties;
    }

    public OpenAIChatCompletionResponse callOpenAiModel(OpenAIChatCompletionRequest request) {
        HttpEntity<OpenAIChatCompletionRequest> requestBody = new HttpEntity<>(request, getHeaders());
        OpenAIChatCompletionResponse openAIChatCompletionResponse = null;
        try {
            log.info("calling callOpenAiModel request : {}", request);
            ResponseEntity<String> response = restTemplate.exchange(httpConnectionConfig.getUrl(), HttpMethod.POST, requestBody, String.class);
            openAIChatCompletionResponse = objectMapper.readValue(response.getBody(), OpenAIChatCompletionResponse.class);
            log.info("gotten callOpenAiModel response : {}", response);
        } catch (Exception e) {
            LogUtils.downstreamUnsuccsessfulResponse(httpConnectionConfig.getUrl(), request, openAIChatCompletionResponse);
        }
        return openAIChatCompletionResponse;
    }

    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, applicationProperties.getOpenaiAuthorizationValue());
        return httpHeaders;
    }
}
