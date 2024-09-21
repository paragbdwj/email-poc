package com.worli.chatbot.config.http;

import lombok.Data;

@Data
public class HttpPoolConfig {
    private int defaultMaxPerRoute;
    private int maxTotal;
    private int connectionTimeout;
    private int requestTimeout;
    private int socketTimeout;
}
