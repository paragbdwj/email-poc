package com.worli.chatbot.utils;

import lombok.experimental.UtilityClass;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class TimeUtils {
    public static String getTimePlusNDaysFromNowISO(int nDays) {
        ZonedDateTime futureTime = ZonedDateTime.now(java.time.ZoneOffset.UTC).plusDays(nDays);
        return futureTime.format(DateTimeFormatter.ISO_INSTANT);
    }
}
