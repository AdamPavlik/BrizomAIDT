package org.brizom.aidt.kickoffservice.utils;


import java.time.ZonedDateTime;

public class TimeUtils {

    public static int getMinutesSinceMidnightUTC() {
        ZonedDateTime utcNow = ZonedDateTime.now(java.time.ZoneOffset.UTC);
        return utcNow.getHour() * 60;
    }

}
