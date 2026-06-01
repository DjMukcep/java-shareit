package ru.practicum.request.dto;

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
class NewItemRequestTest {

    @Autowired
    private JacksonTester<NewItemRequest> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Десериализовать Json в класс NewItemRequest.")
    @SneakyThrows
    void deserializeNewItemRequest() {
        String content = objectMapper.writeValueAsString(
                Map.of(
                        "description","desc"
                )
        );

        NewItemRequest itemRequest = json.parseObject(content);

        assertThat(itemRequest.getDescription()).isEqualTo("desc");
    }
}