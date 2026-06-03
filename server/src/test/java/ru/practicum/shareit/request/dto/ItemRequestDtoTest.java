package ru.practicum.shareit.request.dto;

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
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @DisplayName("Сериализовать класс ItemRequestDto в Json.")
    @SneakyThrows
    void serializeItemRequestDto() {
        ItemRequestDto.Item item = new ItemRequestDto.Item(10L, "item", 1L);
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .createdAt(LocalDateTime.of(
                        2020,
                        6,
                        6,
                        10,
                        11,
                        12,
                        123_456_789))
                .items(List.of(item))
                .build();

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.description")
                .isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("@.created")
                .isEqualTo("2020-06-06T10:11:12");
        assertThat(result.getJson()).doesNotContain("123456789");
        assertThat(result).extractingJsonPathNumberValue("@.items[0].id")
                .isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("@.items[0].name")
                .isEqualTo("item");
        assertThat(result).extractingJsonPathNumberValue("@.items[0].ownerId")
                .isEqualTo(1);
    }
}