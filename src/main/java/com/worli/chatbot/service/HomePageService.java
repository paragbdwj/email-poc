package com.worli.chatbot.service;

import com.worli.chatbot.helper.DatabaseHelper;
import com.worli.chatbot.mongo.models.CalendarData;
import com.worli.chatbot.response.GetCalendarDataResponse;
import com.worli.chatbot.response.GetCalendarDataResponse.MeetingData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worli.chatbot.constants.TimeConstants.SECONDS_IN_A_DAY;

@Service
@Slf4j
@RequiredArgsConstructor
public class HomePageService {
    private final DatabaseHelper databaseHelper;
    public GetCalendarDataResponse getCalendarData(String userId, String accessToken) {
        List<CalendarData> calendarDataList = databaseHelper.getCalendarDataAfterNSeconds(Long.parseLong(userId), SECONDS_IN_A_DAY);
        return makeGetCalendarResponse(userId, calendarDataList);
    }

    private GetCalendarDataResponse makeGetCalendarResponse(String userId, List<CalendarData> calendarDataList) {
        return GetCalendarDataResponse.builder()
                .meetingDataList(calendarDataList.stream().map(calendarData -> MeetingData.builder()
                        .agenda(calendarData.getAgenda())
                        .currentStatus(calendarData.getCurrentStatus())
                        .location(calendarData.getLocation())
                        .endEpochTimestamp(calendarData.getEndEpochTimestamp())
                        .meetingLink(calendarData.getMeetingLink())
                        .priorityScore(calendarData.getPriorityScore())
                        .startEpochTimestamp(calendarData.getStartEpochTimestamp())
                        .meetingId(calendarData.getMeetingId())
                        .build()).toList())
                .build();
    }
}
