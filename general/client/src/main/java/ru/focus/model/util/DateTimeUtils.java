package ru.focus.model.util;

import lombok.experimental.UtilityClass;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtils {

    public String getCurrentDateTime(DateTimeFormatter dateTimeFormatter) {
        OffsetDateTime currentOffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        return currentOffsetDateTime.format(dateTimeFormatter);

    }

}
