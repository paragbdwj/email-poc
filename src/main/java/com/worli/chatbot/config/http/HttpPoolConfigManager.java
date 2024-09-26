package com.worli.chatbot.config.http;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static com.worli.chatbot.constants.BeanNames.Rest.GOOGLE_CALENDAR_REST_TEMPLATE;
import static com.worli.chatbot.constants.BeanNames.Rest.GOOGLE_GET_PROFILE_DATA_REST_TEMPLATE;
import static com.worli.chatbot.constants.BeanNames.Rest.GOOGLE_GET_TOKEN_REST_TEMPLATE;
import static com.worli.chatbot.constants.BeanNames.Rest.GOOGLE_PEOPLE_REST_TEMPLATE;
import static com.worli.chatbot.constants.BeanNames.Rest.LLM_CONVERSATIONAL_REST_TEMPLATE;

@Configuration
@Data
@Slf4j
public class HttpPoolConfigManager {

    @Bean(LLM_CONVERSATIONAL_REST_TEMPLATE)
    public RestTemplate llmGeminiRestTemplate(HttpPoolProperties httpPoolProperties) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(getHttpClientFactory(httpPoolProperties.getLlmConversationalPoolConfig()));
        return new RestTemplate(requestFactory);
    }

    @Bean(GOOGLE_GET_TOKEN_REST_TEMPLATE)
    public RestTemplate googleGetTokenRestTemplate(HttpPoolProperties httpPoolProperties) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(getHttpClientFactory(httpPoolProperties.getGoogleGetTokenPoolConfig()));
        return new RestTemplate(requestFactory);
    }

    @Bean(GOOGLE_GET_PROFILE_DATA_REST_TEMPLATE)
    public RestTemplate googleGetProfileDataRestTemplate(HttpPoolProperties httpPoolProperties) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(getHttpClientFactory(httpPoolProperties.getGoogleGetProfileDataPoolConfig()));
        return new RestTemplate(requestFactory);
    }

    @Bean(GOOGLE_CALENDAR_REST_TEMPLATE)
    public RestTemplate googleCalendarRestTemplate(HttpPoolProperties httpPoolProperties) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(getHttpClientFactory(httpPoolProperties.getGoogleCalendarPoolConfig()));
        return new RestTemplate(requestFactory);
    }

    @Bean(GOOGLE_PEOPLE_REST_TEMPLATE)
    public RestTemplate googlePeopleRestTemplate(HttpPoolProperties httpPoolProperties) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(getHttpClientFactory(httpPoolProperties.getGooglePeoplePoolConfig()));
        return new RestTemplate(requestFactory);
    }

    private CloseableHttpClient getHttpClientFactory(HttpPoolConfig httpPoolConfig) {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnPerRoute(httpPoolConfig.getDefaultMaxPerRoute())
                .setMaxConnTotal(httpPoolConfig.getMaxTotal())
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofMilliseconds(httpPoolConfig.getSocketTimeout()))
                        .setConnectTimeout(Timeout.ofMilliseconds(httpPoolConfig.getConnectionTimeout()))
                        .build())
                .build();
        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .build();
    }
}
