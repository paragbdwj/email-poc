package com.worli.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.mongo.models.ConversationalHistoryData;
import com.worli.chatbot.mongo.models.UserProfileData;
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
    public void processMessageReceived(MessageRecievedPojo messageRecievedPojo) throws JsonProcessingException {
        //check if user is part of worli
        messageRecievedPojo.setUserName(Objects.requireNonNull(extractEmailAndUsername(messageRecievedPojo.getEmail()))[0]);
        messageRecievedPojo.setEmail(Objects.requireNonNull(extractEmailAndUsername(messageRecievedPojo.getEmail()))[1]);
        UserProfileData userProfileData = databaseHelper.findByEmail(messageRecievedPojo.getEmail());
        if(Objects.isNull(userProfileData)) {
            emailSenderService.sendEmail(messageRecievedPojo.getEmail(), "Not Part of Worli", NOT_PART_OF_WORLI, false);
            return;
        }
        List<ConversationalHistoryData> conversationalHistoryData = mongoHelper.getDocumentsUntilIntentFulfilled(messageRecievedPojo.getEmail(), 20);
        OpenAIChatCompletionResponse openAIChatCompletionResponse = conversationalModelService.callOpenAiModel(createOpenAIRequest(messageRecievedPojo, conversationalHistoryData));
        String message = openAIChatCompletionResponse.getChoices().get(0).getMessage().getContent();
        ConvModelPromptResponse convModelPromptResponse = objectMapper.readValue(message, ConvModelPromptResponse.class);
        handleBusinessUseCaseBasisIntent(convModelPromptResponse, messageRecievedPojo);
        CompletableFuture.runAsync(() -> saveMessageInConversationalHistoryData(messageRecievedPojo, convModelPromptResponse));
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

    private void handleBusinessUseCaseBasisIntent(ConvModelPromptResponse convModelPromptResponse, MessageRecievedPojo messageRecievedPojo) {
        if(convModelPromptResponse.getIntent() == 4) {         // irrelevant_message intent
            emailSenderService.sendEmail(messageRecievedPojo.getEmail(), "Please include conversation related to emails only :)", "We only cater queries related to meetings. Hope you understand :)", false);
        } else if(convModelPromptResponse.getIntent() == 1) {  // schedule_meeting intent
            if(CollectionUtils.isEmpty(convModelPromptResponse.getParticipants())) {
                emailSenderService.sendEmail(messageRecievedPojo.getEmail(), "Receiver email missing", "Please provide us email of the receiver so that we can assist you better :)", false );
                return;
            }
            for(String participantEmail : convModelPromptResponse.getParticipants()) {
                emailSenderService.sendEmail(participantEmail, "You have a meet bud", "A meeting is scheduled with " + convModelPromptResponse.getToEmail(), false);
            }
            emailSenderService.sendEmail(messageRecievedPojo.getEmail(), "Your meeting is successful", "A meeting is scheduled with " + convModelPromptResponse.getToEmail(), false );
        }
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
        StringBuilder chatHistory = new StringBuilder();
        Collections.reverse(conversationalHistoryData);
        for(int index = 0; index < conversationalHistoryData.size(); index++) {
            chatHistory.append("- email_body : ")
                    .append(conversationalHistoryData.get(index).getMessage())
                    .append(", subject : ")
                    .append(conversationalHistoryData.get(index).getSubject())
                    .append("\n");
        }
        chatHistory.append("- email_body : ")
                .append(messageRecievedPojo.getMessage())
                .append(", subject : ")
                .append(messageRecievedPojo.getSubject())
                .append("\n");
        return chatHistory.toString();
    }

    private void saveMessageInConversationalHistoryData(MessageRecievedPojo messageRecievedPojo, ConvModelPromptResponse openAiResponse) {
        databaseHelper.saveConversationalHistoryData(messageRecievedPojo, "email", openAiResponse);
    }
}
