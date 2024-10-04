package com.worli.chatbot.service;

import com.worli.chatbot.enums.Intent;
import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.model.MessageRecievedPojo;
import com.worli.chatbot.mongo.models.UserProfileData;
import com.worli.chatbot.request.GoogleCalendarRequest;
import com.worli.chatbot.request.PriorityModelRequest;
import com.worli.chatbot.response.ConvModelPromptResponse;
import com.worli.chatbot.response.GoogleCalendarResponse;
import com.worli.chatbot.response.GoogleCalendarResponse.Event;
import com.worli.chatbot.response.PriorityModelResponse;
import com.worli.chatbot.service.email.EmailSenderService;
import com.worli.chatbot.service.google.GoogleCalendarService;
import com.worli.chatbot.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Rand;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusinessUseCaseHandlingService {
    private final EmailSenderService emailSenderService;
    private final PriorityModelService priorityModelService;
    private final GoogleCalendarService googleCalendarService;
    private final DatabaseHelper databaseHelper;

    //TODO : for now I am handling for 1:1 situation
    public void handleBusinessUseCaseBasisIntent(ConvModelPromptResponse convModelPromptResponse, MessageRecievedPojo messageRecievedPojo) {
        if(Intent.IRRELEVANT_INTENT.getIntent().equals(convModelPromptResponse.getIntent())) {         // irrelevant_message intent
            emailSenderService.sendEmail(messageRecievedPojo.getEmail(), "Please include conversation related to emails only :)", "We only cater queries related to meetings. Hope you understand :)", false);
        } else if(Intent.SCHEDULE_MEETING.getIntent().equals(convModelPromptResponse.getIntent())) {  // schedule_meeting intent
            messageRecievedPojo.setReceiverPartOfWorli(checkIfOtherUserIsPartOfWorli(convModelPromptResponse.getParticipants().get(0)));
            // handle null participants case
            if(CollectionUtils.isEmpty(convModelPromptResponse.getParticipants())) {
                emailSenderService.sendEmail(messageRecievedPojo.getEmail(), "Receiver email missing", "Please provide us email of the receiver so that we can assist you better :)", false );
                return;
            }
            GoogleCalendarRequest googleCalendarRequest = getGoogleCalendarRequest(messageRecievedPojo);
            if(StringUtils.isNotBlank(convModelPromptResponse.getDateAndTimeOfTheMeeting())) {
                googleCalendarRequest.setTimeMin(convModelPromptResponse.getDateAndTimeOfTheMeeting());
                googleCalendarRequest.setTimeMax(convModelPromptResponse.getDateAndTimeOfTheMeeting());
            }
            GoogleCalendarResponse googleCalendarResponse = googleCalendarService.googleCalendar(googleCalendarRequest);
//            PriorityModelResponse priorityModelResponse = priorityModelService.getPriorityScores(new PriorityModelRequest());
            //TODO : remove below line
            setRandomPriorityScore(googleCalendarResponse);
            for(String participantEmail : convModelPromptResponse.getParticipants()) {
                emailSenderService.sendEmail(participantEmail, "You have a meet bud", "A meeting is scheduled with " + convModelPromptResponse.getParticipants().get(0), false);
            }
            emailSenderService.sendEmail(messageRecievedPojo.getEmail(), "Your meeting is successful", "A meeting is scheduled with " + convModelPromptResponse.getParticipants().get(0), false );
        }
    }

    private void setRandomPriorityScore(GoogleCalendarResponse googleCalendarResponse) {
        Random random = new Random();
        List<Event> calendarItems =  googleCalendarResponse.getItems();
        for(Event event : calendarItems) {
            event.setPriorityScore(random.nextDouble());
        }
        googleCalendarResponse.setItems(calendarItems);
    }

    private boolean checkIfOtherUserIsPartOfWorli(String receiverEmail) {
        UserProfileData userProfileData = databaseHelper.findByEmail(receiverEmail);
        return Objects.nonNull(userProfileData);
    }

    private GoogleCalendarRequest getGoogleCalendarRequest(MessageRecievedPojo messageRecievedPojo) {
        return GoogleCalendarRequest.builder()
                .accessToken(messageRecievedPojo.getGoogleAccessToken())
                .maxResults(100)
                .orderBy("startTime")
                .singleEvents(true)
                .timeMin(TimeUtils.getTimePlusNDaysFromNowISO(0))
                .timeMax(TimeUtils.getTimePlusNDaysFromNowISO(3))
                .build();
    }
}
