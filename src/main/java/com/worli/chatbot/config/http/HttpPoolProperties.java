package com.worli.chatbot.config.http;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "http.pool-properties")
public class HttpPoolProperties {
    private HttpPoolConfig llmConversationalPoolConfig;
    private HttpPoolConfig googleGetTokenPoolConfig;
    private HttpPoolConfig googleGetProfileDataPoolConfig;
    private HttpPoolConfig googleCalendarPoolConfig;
    private HttpPoolConfig googlePeoplePoolConfig;
    private HttpPoolConfig priorityModelPoolConfig;
}
