package com.worli.chatbot.config;

import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.mongo.models.UserTokenData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestResponseInterceptor implements HandlerInterceptor {

    private final DatabaseHelper databaseHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Inside pre-handle method with request uri : {}, method : {}, headers : {}", request.getRequestURI(), request.getMethod(), request.getHeaderNames());
        String verificationId = request.getHeader("verification-id");
        UserTokenData userTokenData = databaseHelper.findByVerificationId(verificationId);
        if(Objects.isNull(userTokenData)) {
            log.error("Invalid verification-id : {}", verificationId);
            throw new RuntimeException();
        }
        userTokenData = databaseHelper.checkAndUpdateAccessTokenIfExpired(userTokenData);
        MDC.put("user-id", userTokenData.getUserId().toString());
        MDC.put("access-token", userTokenData.getAccessToken());
        return true;
    }

    // Post-processing
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}

