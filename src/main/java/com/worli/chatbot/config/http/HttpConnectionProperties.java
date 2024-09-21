package com.worli.chatbot.config.http;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("service.config")
public class HttpConnectionProperties {
    private HttpConnectionConfig llmConversationalConfig;
    private HttpConnectionConfig googleGetTokenConfig;
    private HttpConnectionConfig googleGetProfileConfig;
}
