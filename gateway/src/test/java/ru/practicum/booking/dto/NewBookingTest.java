package ru.practicum.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NewBookingTest {

    @Autowired
    private JacksonTester<NewBooking> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Десериализовать Json в класс NewBooking.")
    @SneakyThrows
    void deserializeNewComment() {
        String content = objectMapper.writeValueAsString(
                Map.of(
                        "itemId", 1,
                        "start","2026-05-30T12:20:00",
                        "end","2026-05-30T18:20:00"
                )
        );

        NewBooking booking = json.parseObject(content);

        assertThat(booking).isNotNull();
        assertThat(booking.getItemId()).isEqualTo(1);
        assertThat(booking.getStart()).isEqualTo("2026-05-30T12:20:00");
        assertThat(booking.getEnd()).isEqualTo("2026-05-30T18:20:00");
    }
}