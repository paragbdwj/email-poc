package com.worli.chatbot.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.service.email.GoogleGmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;


@Component
@RequiredArgsConstructor
@Slf4j
public class PubSubSubscriber {
//    private final ApplicationProperties applicationProperties;
//    private final GoogleGmailService googleGmailService;

//    @PostConstruct
//    public void startSubscriber() {
//
//        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(applicationProperties.getGoogleProjectId(), applicationProperties.getGoogleSubscriptionId());
//
//        MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
//            System.out.println("Received message: " + message.getData().toStringUtf8());
//
//            // Acknowledge the message
//            consumer.ack();
//        };
//
//        // Use an array to hold the subscriber, because array elements can be modified in lambdas
//        final Subscriber[] subscriber = new Subscriber[1];
//
//        try {
//            subscriber[0] = Subscriber.newBuilder(subscriptionName, receiver)
//                    .setCredentialsProvider(credentialsProvider)
//                    .build();
//
//            subscriber[0].addListener(new ApiService.Listener() {
//                @Override
//                public void failed(ApiService.State from, Throwable failure) {
//                    System.err.println("Subscriber failed: " + failure);
//                }
//            }, Runnable::run);
//
//            subscriber[0].startAsync().awaitRunning();
//            System.out.println("Subscriber is listening for messages...");
//        } catch (ApiException e) {
//            System.err.println("Failed to create subscriber: " + e.getStatusCode().getCode());
//        }
//
//        // Keep listening indefinitely
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            if (subscriber[0] != null) {
//                try {
//                    subscriber[0].stopAsync().awaitTerminated(10, TimeUnit.SECONDS);
//                } catch (TimeoutException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }));
//    }

//    @PostConstruct
//    public void startSubscriber() {
//        try {
//            // Load the credentials from the resources folder
//            InputStream credentialsStream = getClass().getResourceAsStream("/coherent-ascent-436020-n2-1cd2d8f09e35.json");
//            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
//
//            // Create a subscription name
//            ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(applicationProperties.getGoogleProjectId(), applicationProperties.getGoogleSubscriptionId());
//
//            // Create a message receiver
//            Subscriber subscriber = Subscriber.newBuilder(subscriptionName, (message, consumer) -> {
//                String receivedMessage = message.getData().toStringUtf8();
//                System.out.println("Received message: " + receivedMessage);
//
//                // Assuming receivedMessage contains the historyId
//                // historyId needs to be extracted
//
//                try {
////                    googleGmailService.getEmailHistory("me", BigInteger.valueOf(1582156));
//                    googleGmailService.fetchRecentMessages("me");
//                } catch (Exception e) {
//                    log.error("caught exception in gmailService.getEmailHistory() with stacktrace : {}", ExceptionUtils.getStackTrace(e), e);
//                }
//                System.out.println("Received message: " + message.getData().toStringUtf8());
//                consumer.ack(); // Acknowledge the message
//            }).setCredentialsProvider(() -> credentials).build();
//
//            // Start the subscriber
//            subscriber.startAsync().awaitRunning();
//            System.out.println("Subscriber is listening for messages...");
//
//        } catch (IOException e) {
//            System.err.println("Failed to load credentials: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}