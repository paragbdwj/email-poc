package com.worli.chatbot.config;

import com.worli.chatbot.service.email.EmailReceiverService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class EmailReceiverInitializer implements ApplicationListener<ContextRefreshedEvent> {
    private final EmailReceiverService emailReceiverService;

    public EmailReceiverInitializer(EmailReceiverService emailReceiverService) {
        this.emailReceiverService = emailReceiverService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        emailReceiverService.startListeningForEmails();
    }
}

