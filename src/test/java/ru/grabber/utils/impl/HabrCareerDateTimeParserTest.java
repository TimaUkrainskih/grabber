package ru.grabber.utils.impl;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HabrCareerDateTimeParserTest {

    private final HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();

    @Test
    void whenValidIsoDateWithOffsetThenParseCorrectly() {
        String input = "2025-09-22T13:08:58+03:00";
        LocalDateTime expected = LocalDateTime.of(2025, 9, 22, 13, 8, 58);
        LocalDateTime result = parser.parse(input);
        assertThat(expected).isEqualTo(result);
    }

    @Test
    void whenSecondOffsetThenIgnoreOffset() {
        String input = "2025-09-22T10:00:00-05:00";
        LocalDateTime expected = LocalDateTime.of(2025, 9, 22, 10, 0, 0);
        LocalDateTime result = parser.parse(input);
        assertThat(expected).isEqualTo(result);
    }

    @Test
    void whenInvalidFormatThenThrowException() {
        String invalid = "22 сентября 2025";
        assertThatThrownBy(() -> parser.parse(invalid))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void whenEmptyStringThenThrowException() {
        assertThatThrownBy(() -> parser.parse(""))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void whenNullInputThenThrowException() {
        assertThatThrownBy(() -> parser.parse(null))
                .isInstanceOf(NullPointerException.class);
    }
}