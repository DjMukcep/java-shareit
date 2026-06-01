package ru.practicum.item.dto;

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
class NewItemTest {

    @Autowired
    private JacksonTester<NewItem> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Десериализовать Json в класс NewItem.")
    @SneakyThrows
    void deserializeNewItem() {
        String content = objectMapper.writeValueAsString(
                Map.of("name", "some name",
                        "description", "desc",
                        "available", true,
                        "requestId", 1
                )
        );

        NewItem newItem = json.parseObject(content);

        assertThat(newItem).isNotNull();
        assertThat(newItem.getName()).isEqualTo("some name");
        assertThat(newItem.getDescription()).isEqualTo("desc");
        assertThat(newItem.getIsAvailable()).isEqualTo(Boolean.TRUE);
        assertThat(newItem.getRequestId()).isEqualTo(1L);
    }
}