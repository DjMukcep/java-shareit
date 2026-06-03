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
class UpdateItemTest {

    @Autowired
    private JacksonTester<UpdateItem> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Десериализовать Json в класс UpdateItem.")
    @SneakyThrows
    void deserializeUpdateItem() {
        String content = objectMapper.writeValueAsString(
                Map.of(
                        "name", "name",
                        "description","desc",
                        "available",true
                )
        );

        UpdateItem updateItem = json.parseObject(content);

        assertThat(updateItem).isNotNull();
        assertThat(updateItem.getName()).isEqualTo("name");
        assertThat(updateItem.getDescription()).isEqualTo("desc");
        assertThat(updateItem.getIsAvailable()).isEqualTo(Boolean.TRUE);
    }
}