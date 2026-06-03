package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @DisplayName("Сериализовать класс BookingDto в Json")
    @SneakyThrows
    void serializeBookingDto() {
        BookingDto.Booker booker = new BookingDto.Booker(1L);
        BookingDto.BookingItem item = new BookingDto.BookingItem(10L, "name");

        BookingDto dto = BookingDto.builder()
                .id(100L)
                .start(LocalDateTime.of(
                        2026,
                        5,
                        29,
                        12,
                        30,
                        15,
                        123_456_789
                ))
                .end(LocalDateTime.of(
                        2026,
                        5,
                        30,
                        18,
                        45,
                        10,
                        999_999_999
                ))
                .status(BookingStatus.APPROVED)
                .booker(booker)
                .item(item)
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(100);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2026-05-29T12:30:15");

        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2026-05-30T18:45:10");

        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("APPROVED");

        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(1);

        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(10);

        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("name");

        assertThat(result.getJson())
                .doesNotContain("123456789")
                .doesNotContain("999999999");
    }
}