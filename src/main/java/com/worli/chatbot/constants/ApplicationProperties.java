package com.worli.chatbot.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApplicationProperties {

    @Value("${mail.smtp.user}")
    private String senderEmail;

    // google related properties
    @Value("${google.client.id}")
    private String googleOauthClientId;

    @Value("${google.client.secret}")
    private String googleOauthClientSecret;

    @Value("${google.redirect-uri}")
    private String googleOauthRedirectUri;

    @Value("${openai.authorization-value}")
    private String openaiAuthorizationValue;

    @Value("${google.project-id}")
    private String googleProjectId;

    @Value("${google.subscription-id}")
    private String googleSubscriptionId;

    // email receiver properties
    @Value("${receiver-worli.email}")
    private String receiverEmail;

    @Value("${receiver-worli.email-password}")
    private String receiverPassword;

    @Value("${receiver-worli.activated}")
    private boolean receiverActivated;

    @Value("${conversational-model.prompt}")
    private String conversationalModelPrompt;

    //kafka related
    @Value("${kafka.topic.email-receiver}")
    private String topicEmailReceiver;

}
