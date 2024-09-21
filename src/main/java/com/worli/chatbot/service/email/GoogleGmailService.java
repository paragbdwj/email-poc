package com.worli.chatbot.service.email;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.History;
import com.google.api.services.gmail.model.HistoryMessageAdded;
import com.google.api.services.gmail.model.ListHistoryResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Service
public class GoogleGmailService {

//    private final Gmail gmailService;
//
//    public GoogleGmailService(Gmail gmailService) {
//        this.gmailService = gmailService;
//    }
//
//    public void fetchRecentMessages(String userId) throws IOException {
//        // Get the latest historyId
//        Gmail.Users.Messages.List listRequest = gmailService.users().messages().list(userId).setLabelIds(List.of("INBOX"));
//        ListMessagesResponse listResponse = listRequest.execute();
//
//        if (listResponse.getMessages() != null && !listResponse.getMessages().isEmpty()) {
//            // Use the latest historyId from the last message
//            String latestMessageId = listResponse.getMessages().get(0).getId();
//            Message latestMessage = gmailService.users().messages().get(userId, latestMessageId).execute();
//            BigInteger latestHistoryId = latestMessage.getHistoryId();
//
//            // Use the latest historyId for further tracking
//            getEmailHistory(userId, latestHistoryId);
//        } else {
//            System.out.println("No recent messages found.");
//        }
//    }
//
//
//    // Function to retrieve email history
//    public void getEmailHistory(String userId, BigInteger historyId) throws IOException {
//        Gmail.Users.History.List request = gmailService.users().history().list(userId)
//                .setStartHistoryId(historyId)
//                .setHistoryTypes(List.of("messageAdded"));
//
//        // Get the history of email changes
//        ListHistoryResponse response = request.execute();
//
//        if (response.getHistory() != null) {
//            for (History history : response.getHistory()) {
//                if (history.getMessagesAdded() != null) {
//                    for (HistoryMessageAdded addedMessage : history.getMessagesAdded()) {
//                        String messageId = addedMessage.getMessage().getId();
//                        System.out.println("Message ID: " + messageId);
//
//                        // Fetch the full message using the message ID
//                        Message message = getEmailMessage(userId, messageId);
//                        System.out.println("Message Snippet: " + message.getSnippet());
//
//                        // To get the body of the email
//                        String emailBody = getEmailBody(message);
//                        System.out.println("Email Body: " + emailBody);
//                    }
//                }
//            }
//        } else {
//            System.out.println("No email changes found for this historyId.");
//        }
//    }
//
//    // Function to fetch the full email message based on the message ID
//    public Message getEmailMessage(String userId, String messageId) throws IOException {
//        return gmailService.users().messages().get(userId, messageId).setFormat("full").execute();
//    }
//
//    // Extracts email body from the message
//    private String getEmailBody(Message message) {
//        return message.getPayload().getParts().stream()
//                .filter(part -> part.getMimeType().equals("text/plain"))
//                .map(part -> new String(part.getBody().getData()))
//                .findFirst()
//                .orElse("No body found");
//    }
}

