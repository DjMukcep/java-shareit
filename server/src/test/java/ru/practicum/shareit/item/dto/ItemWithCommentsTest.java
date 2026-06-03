package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemWithCommentsTest {

    @Autowired
    private JacksonTester<ItemWithComments> json;

    @Test
    @DisplayName("Сериализовать класс ItemWithComments в Json.")
    @SneakyThrows
    void serializeItemWithComments() {

        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("author")
                .created(LocalDateTime.of(2026, 5, 29, 10, 15, 30))
                .build();

        ItemWithComments dto = ItemWithComments.builder()
                .id(10L)
                .name("name")
                .description("desc")
                .isAvailable(true)
                .lastBooking(LocalDateTime.of(
                        2026, 5, 28, 12,
                        0, 0, 123_456_789))
                .nextBooking(LocalDateTime.of(
                        2026, 5, 30, 18,
                        45, 10, 999_999_999))
                .comments(List.of(comment))
                .build();

        JsonContent<ItemWithComments> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(10);

        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo("name");

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("desc");

        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isTrue();

        assertThat(result).extractingJsonPathStringValue("$.lastBooking")
                .isEqualTo("2026-05-28T12:00:00");

        assertThat(result).extractingJsonPathStringValue("$.nextBooking")
                .isEqualTo("2026-05-30T18:45:10");

        assertThat(result.getJson())
                .doesNotContain("123456789")
                .doesNotContain("999999999");

        assertThat(result).extractingJsonPathArrayValue("$.comments")
                .hasSize(1);

        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo("comment");

        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo("author");

        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo("2026-05-29T10:15:30");
    }


}