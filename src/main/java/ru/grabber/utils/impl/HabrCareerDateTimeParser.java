package ru.grabber.utils.impl;

import ru.grabber.utils.DateTimeParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.OffsetDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {

    private static final DateTimeFormatter HABR_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public LocalDateTime parse(String parse) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(parse, HABR_FORMATTER);
        return offsetDateTime.toLocalDateTime();
    }
}
