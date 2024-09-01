package com.worli.chatbot.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

@UtilityClass
@Slf4j
public class LogUtils {
    public void getRequestLog(String apiName, Object request) {
        log.info("for the api : {}, got request : {}", apiName, request);
    }

    public void getResponseLog(String apiName, Object response) {
        log.info("for the api : {}, got response : {}", apiName, response);
    }

    public void getExceptionLog(String apiName, Object request, Exception e) {
        log.error("got exception in : {} for request : {} with stack_trace : {}", apiName, request, ExceptionUtils.getStackTrace(e));
    }

    public void getWrongCredentialsExceptionLog(String apiName, Object request, Exception e) {
        log.error("got wrong_credentials_exception in : {} for request : {} with stack_trace : {}", apiName, request, ExceptionUtils.getStackTrace(e));
    }
}
