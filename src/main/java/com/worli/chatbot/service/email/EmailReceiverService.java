package com.worli.chatbot.service.email;


import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.service.ChatAggregatorService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.*;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.angus.mail.imap.IMAPFolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

// TODO : Need to make it scalable (Possibly using google-project)

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailReceiverService {
    private final ObjectMapper objectMapper;
    private final ChatAggregatorService chatAggregatorService;
    private final EmailSenderService emailSenderService;
    private final ApplicationProperties applicationProperties;

    @PostConstruct
    public void startListeningForEmails() {
        if(!applicationProperties.isReceiverActivated()) {
            return;
        }
        try {
            Properties properties = getPropertiesForEmails();
            Session session = Session.getDefaultInstance(properties, null);
            Store store = session.getStore();
            store.connect(applicationProperties.getReceiverEmail(), applicationProperties.getReceiverPassword());

            IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);


            // TODO : make this asynchronous or add a listener to receive new messages
            inbox.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent event) {
                    log.info("inside ducking listener");
                    Message[] messages = event.getMessages();
                    for (Message message : messages) {
                        try {
                            if (message instanceof MimeMessage) {
                                MimeMessage mimeMessage = (MimeMessage) message;
                                chatAggregatorService.processMessageReceived(MessageRecievedPojo.builder()
                                                .message(getEmailContent(mimeMessage))
                                                .subject(mimeMessage.getSubject())
                                                .email(mimeMessage.getFrom()[0].toString())
                                        .build());
                            }
                        } catch (Exception e) {
                            log.error("getting error in messagesAdded with stack_trace : {}", ExceptionUtils.getStackTrace(e));
                        }
                    }
                }
            });

            while (true) {
                if (!inbox.isOpen()) {
                    inbox.open(Folder.READ_WRITE);
                }
                inbox.idle();
            }

        } catch (Exception e) {
            log.error("got exception in startListeningForEmails with stack_trace : {}", ExceptionUtils.getStackTrace(e));
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
                    contentBuilder.append(bodyPart.getContent().toString());
                }
                // handle other content types (e.g., attachments) here if needed
            }
        }

        return contentBuilder.toString();
    }
}
