package com.worli.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.mongo.models.ConversationalHistoryData;
import com.worli.chatbot.mongo.models.UserProfileData;
import com.worli.chatbot.mongo.models.UserTokenData;
import com.worli.chatbot.request.OpenAIChatCompletionRequest;
import com.worli.chatbot.request.OpenAIChatCompletionRequest.Message;
import com.worli.chatbot.response.ConvModelPromptResponse;
import com.worli.chatbot.response.OpenAIChatCompletionResponse;
import com.worli.chatbot.service.email.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.worli.chatbot.constants.EmailTemplates.NOT_PART_OF_WORLI;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatAggregatorService {
    private final DatabaseHelper databaseHelper;
    private final ConversationalModelService conversationalModelService;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;
    private final EmailSenderService emailSenderService;
    private final MongoHelper mongoHelper;
    private final BusinessUseCaseHandlingService businessUseCaseHandlingService;
    public void processMessageReceived(MessageRecievedPojo messageRecievedPojo) throws JsonProcessingException {
        extractUserNameAndEmailAndSet(messageRecievedPojo);
        UserProfileData userProfileData = checkAndSetIfUserIsPartOfWorli(messageRecievedPojo);
        setAccessTokenInMessageReceivedPojo(messageRecievedPojo, userProfileData);
        List<ConversationalHistoryData> conversationalHistoryData = mongoHelper.getDocumentsUntilIntentFulfilled(messageRecievedPojo.getEmail(), 20);
        OpenAIChatCompletionResponse openAIChatCompletionResponse = conversationalModelService.callOpenAiModel(createOpenAIRequest(messageRecievedPojo, conversationalHistoryData));
        String message = openAIChatCompletionResponse.getChoices().get(0).getMessage().getContent();
        ConvModelPromptResponse convModelPromptResponse = objectMapper.readValue(message, ConvModelPromptResponse.class);
        businessUseCaseHandlingService.handleBusinessUseCaseBasisIntent(convModelPromptResponse, messageRecievedPojo);
        CompletableFuture.runAsync(() -> saveMessageInConversationalHistoryData(messageRecievedPojo, convModelPromptResponse));
    }

    private UserProfileData checkAndSetIfUserIsPartOfWorli(MessageRecievedPojo messageRecievedPojo) {
        UserProfileData userProfileData = databaseHelper.findByEmail(messageRecievedPojo.getEmail());
        if(Objects.isNull(userProfileData)) {
            messageRecievedPojo.setSenderPartOfWorli(false);
        } else {
            messageRecievedPojo.setSenderPartOfWorli(true);
        }
        return userProfileData;
    }

    private void extractUserNameAndEmailAndSet(MessageRecievedPojo messageRecievedPojo) {
        messageRecievedPojo.setUserName(Objects.requireNonNull(extractEmailAndUsername(messageRecievedPojo.getEmail()))[0]);
        messageRecievedPojo.setEmail(Objects.requireNonNull(extractEmailAndUsername(messageRecievedPojo.getEmail()))[1]);
    }

    private void setAccessTokenInMessageReceivedPojo(MessageRecievedPojo messageRecievedPojo, UserProfileData userProfileData) throws JsonProcessingException {
        UserTokenData userTokenData = databaseHelper.findUserTokenByUserId(userProfileData.getUserId());
        userTokenData = databaseHelper.checkAndUpdateAccessTokenIfExpired(userTokenData);
        messageRecievedPojo.setGoogleAccessToken(userTokenData.getAccessToken());
    }

    public static String[] extractEmailAndUsername(String input) {
        String regex = "(.*) <(.*)>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String username = matcher.group(1).trim(); // Extract username
            String email = matcher.group(2).trim();    // Extract email
            return new String[]{username, email};
        }
        return null; // Return null if no match found
    }

    private OpenAIChatCompletionRequest createOpenAIRequest(MessageRecievedPojo messageRecievedPojo, List<ConversationalHistoryData> conversationalHistoryData) {
        String finalPrompt = makeFinalPrompt(messageRecievedPojo, conversationalHistoryData);
        return OpenAIChatCompletionRequest.builder()
                .model("gpt-4")
                .messages(Collections.singletonList(Message.builder()
                                .content(finalPrompt)
                                .role("user")
                        .build()))
                .build();
    }

    private String makeFinalPrompt(MessageRecievedPojo messageRecievedPojo, List<ConversationalHistoryData> conversationalHistoryData) {
        String prompt = applicationProperties.getConversationalModelPrompt();
        String messageList = makeChatHistory(conversationalHistoryData, messageRecievedPojo);
        return String.format(prompt, messageList);
    }

    private String makeChatHistory(List<ConversationalHistoryData> conversationalHistoryData, MessageRecievedPojo messageRecievedPojo) {
        String chatHistory = "";
        Collections.reverse(conversationalHistoryData);
        for(int index = 0; index < conversationalHistoryData.size(); index++) {
            chatHistory = chatHistory + "- email_body : "
                    + normalizeNewlines(conversationalHistoryData.get(index).getMessage())
                    + ", subject : "
                    + normalizeNewlines(conversationalHistoryData.get(index).getSubject())
                    + "\n";
        }
        chatHistory = chatHistory + "- email_body : "
                + normalizeNewlines(messageRecievedPojo.getMessage())
                + ", subject : "
                + normalizeNewlines(messageRecievedPojo.getSubject())
                + "\n";
        return chatHistory;
    }

    private String normalizeNewlines(String input) {
        return input != null ? input.replace("\r\n", "\n").trim() : "";
    }

    private void saveMessageInConversationalHistoryData(MessageRecievedPojo messageRecievedPojo, ConvModelPromptResponse openAiResponse) {
        databaseHelper.saveConversationalHistoryData(messageRecievedPojo, "email", openAiResponse);
    }
}
