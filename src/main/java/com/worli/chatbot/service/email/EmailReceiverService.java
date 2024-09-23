package com.worli.chatbot.service.email;


import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.service.KafkaProducerService;
import com.worli.chatbot.utils.SerializationUtils;
import jakarta.mail.*;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.eclipse.angus.mail.imap.IMAPFolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// TODO : Need to make it scalable (Possibly using google-project)

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailReceiverService {
    private final ApplicationProperties applicationProperties;
    private final KafkaProducerService kafkaProducerService;

    private IMAPFolder inbox;

    public void startListeningForEmails() {
        if (!applicationProperties.isReceiverActivated()) {
            return;
        }

        // Start a new thread to handle email listening
        new Thread(this::connectAndListenForEmails).start();
    }

    private void connectAndListenForEmails() {
        while (true) {
            try {
                connectToEmail();
                listenForEmails();
            } catch (Exception e) {
                log.error("Error in email listening: {}. Reconnecting...", e.getMessage());
            }

            // Sleep before attempting to reconnect
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(3000)); // wait before reconnecting
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }
    }

    private void connectToEmail() throws Exception {
        Properties properties = getPropertiesForEmails();
        Session session = Session.getDefaultInstance(properties, null);
        Store store = session.getStore();

        // Retry connecting with exponential backoff
        int retries = 5;
        for (int i = 0; i < retries; i++) {
            try {
                store.connect(applicationProperties.getReceiverEmail(), applicationProperties.getReceiverPassword());
                inbox = (IMAPFolder) store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);
                return; // Exit on successful connection
            } catch (Exception e) {
                log.error("SocketException on connect: {}. Retrying... ({}/{})", e.getMessage(), i + 1, retries);
                Thread.sleep((long) Math.pow(2, i) * 1000); // Exponential backoff
            }
        }
    }

    private void listenForEmails() {
        inbox.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent event) {
                processMessages(event.getMessages());
            }
        });

        // Use a loop to keep idling
        while (true) {
            try {
                inbox.idle();
            } catch (MessagingException e) {
                log.error("Error during idle: {}", e.getMessage());
                break; // Exit to reconnect if idle fails
            }
        }
    }

    private void processMessages(Message[] messages) {
        for (Message message : messages) {
            try {
                if (message instanceof MimeMessage) {
                    MimeMessage mimeMessage = (MimeMessage) message;
                    kafkaProducerService.sendMessage(applicationProperties.getTopicEmailReceiver(), SerializationUtils.serializeMessageReceivedPojo(MessageRecievedPojo.builder()
                            .message(getEmailContent(mimeMessage))
                            .subject(mimeMessage.getSubject())
                            .email(mimeMessage.getFrom()[0].toString())
                            .build()));
                }
            } catch (Exception e) {
                log.error("Error processing message: {}", e.getMessage());
            }
        }
    }

    private Properties getPropertiesForEmails() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        return properties;
    }

    private String getEmailContent(MimeMessage mimeMessage) throws IOException, MessagingException {
        StringBuilder contentBuilder = new StringBuilder();
        Object content = mimeMessage.getContent();
        if (content instanceof String) {
            contentBuilder.append((String) content);
        } else if (content instanceof Multipart) {
            // Multipart content (text and/or attachments)
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String contentType = bodyPart.getContentType();
                if (contentType.toLowerCase().startsWith("text/plain") || contentType.toLowerCase().contains("text/html")) {
                    String partContent = bodyPart.getContent().toString();
                    // Remove content starting from the first <div> tag
                    int divIndex = partContent.indexOf("<div>");
                    if (divIndex != -1) {
                        partContent = partContent.substring(0, divIndex);
                    }
                    contentBuilder.append(partContent);
                }
                // handle other content types (e.g., attachments) here if needed
            }
        }
        String finalString = removeHtmlAfterDiv(contentBuilder.toString());
        return removeQuotedSectionsForThreadedEmails(finalString);
    }

    private String removeQuotedSectionsForThreadedEmails(String content) {
        // Split by common reply markers such as "On [date] wrote:" and "From:"
        String[] lines = content.split("\r\n|\r|\n");
        StringBuilder newContent = new StringBuilder();
        boolean isQuotedSection = false;

        for (String line : lines) {
            // Check for the start of quoted sections or signature
            if (line.startsWith("On ") && line.contains(" wrote:")) {
                isQuotedSection = true; // Start of quoted section
            } else if (line.startsWith(">") || line.startsWith("From:")) {
                isQuotedSection = true; // Additional markers for quoted text
            } else if (!isQuotedSection) {
                newContent.append(line).append("\r\n");
            }
        }

        return newContent.toString().trim();
    }

    private static String removeHtmlAfterDiv(String input) {
        int divIndex = input.indexOf("<div");
        if (divIndex != -1) {
            // Return substring up to the start of <div>
            return input.substring(0, divIndex).trim();
        }
        return input; // Return original string if <div> not found
    }
}
