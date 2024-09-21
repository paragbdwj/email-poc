package com.worli.chatbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worli.chatbot.constants.ApplicationProperties;
import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.mongo.models.ConversationalHistoryData;
import com.worli.chatbot.request.OpenAIChatCompletionRequest;
import com.worli.chatbot.request.OpenAIChatCompletionRequest.Message;
import com.worli.chatbot.response.ConvModelPromptResponse;
import com.worli.chatbot.response.OpenAIChatCompletionResponse;
import com.worli.chatbot.service.email.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatAggregatorService {
    private final DatabaseHelper databaseHelper;
    private final ConversationalModelService conversationalModelService;
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;
    private final EmailSenderService emailSenderService;
    ExecutorService executorService = Executors.newFixedThreadPool(20);
    public void processMessageReceived(MessageRecievedPojo messageRecievedPojo) throws JsonProcessingException {
        List<ConversationalHistoryData> conversationalHistoryData = databaseHelper.getTopConversationalDatas(messageRecievedPojo.getEmail(), 20);
        OpenAIChatCompletionResponse openAIChatCompletionResponse = conversationalModelService.callOpenAiModel(createOpenAIRequest(messageRecievedPojo, conversationalHistoryData));
        String message = openAIChatCompletionResponse.getChoices().get(0).getMessage().getContent();
        ConvModelPromptResponse convModelPromptResponse = objectMapper.readValue(message, ConvModelPromptResponse.class);
        handleBusinessUseCaseBasisIntent(convModelPromptResponse, messageRecievedPojo);
        CompletableFuture.runAsync(() -> saveMessageInConversationalHistoryData(messageRecievedPojo, message));
    }

    private void handleBusinessUseCaseBasisIntent(ConvModelPromptResponse convModelPromptResponse, MessageRecievedPojo messageRecievedPojo) {
        if(convModelPromptResponse.getIntent() == 4) {
            emailSenderService.sendEmail(messageRecievedPojo.getEmail(), "Please include conversation related to emails only :)", "We only cater queries related to meetings. Hope you understand :)", false);
        } else if(convModelPromptResponse.getIntent() == 1) {
            emailSenderService.sendEmail(convModelPromptResponse.getToEmail(), "You have a meet bud", "A meeting is scheduled with " + convModelPromptResponse.getToEmail(), false );
            emailSenderService.sendEmail(messageRecievedPojo.getEmail(), "Your meeting is sucssessful", "A meeting is scheduled with " + convModelPromptResponse.getToEmail(), false );
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
        String chatHistory = makeChatHistory(conversationalHistoryData);
        String emailReceived = messageRecievedPojo.getMessage();
        return String.format(prompt, emailReceived, chatHistory);
    }

    private String makeChatHistory(List<ConversationalHistoryData> conversationalHistoryData) {
        StringBuilder chatHistory = new StringBuilder();
        for(int index = 0; index < conversationalHistoryData.size(); index++) {
            chatHistory.append(index)
                    .append(". user_message : ")
                    .append("\n")
                    .append(conversationalHistoryData.get(index).getMessage())
                    .append("\n")
                    .append("your_response : ")
                    .append("\n")
                    .append(conversationalHistoryData.get(index).getResponseFromOpenAi())
                    .append("\n");
        }
        return chatHistory.toString();
    }

    private void saveMessageInConversationalHistoryData(MessageRecievedPojo messageRecievedPojo, String openAiResponse) {
        databaseHelper.saveConversationalHistoryData(messageRecievedPojo, "email", openAiResponse);
    }


}
