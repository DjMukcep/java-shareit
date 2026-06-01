package ru.practicum.shareit.item.dto;

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
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @DisplayName("Сериализовать класс CommentDto в Json")
    @SneakyThrows
    void serializeCommentDto() {

        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.of(
                        2026,
                        5,
                        29,
                        14,
                        30,
                        15,
                        123_456_789
                ))
                .build();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("text");

        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo("author");

        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2026-05-29T14:30:15");

        assertThat(result.getJson())
                .doesNotContain("123456789");
    }
}