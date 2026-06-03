package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoTest {

    @Autowired
    private JacksonTester<RequestDto> json;

    @Test
    @DisplayName("Сериализовать класс RequestDto в Json.")
    @SneakyThrows
    void serializeItemRequestDto() {
        RequestDto requestDto = new RequestDto(
                1L,
                "desc",
                LocalDateTime.of(
                        2020,
                        6,
                        6,
                        10,
                        11,
                        12,
                        123_456_789));

        JsonContent<RequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2020-06-06T10:11:12");
        assertThat(result.getJson()).doesNotContain("123456789");
    }
}