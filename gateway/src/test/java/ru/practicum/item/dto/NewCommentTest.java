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
class NewCommentTest {

    @Autowired
    private JacksonTester<NewComment> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Десериализовать Json в класс NewComment.")
    @SneakyThrows
    void deserializeNewComment() {
        String content = objectMapper.writeValueAsString(
                Map.of("text", "some text")
        );

        NewComment comment = json.parseObject(content);

        assertThat(comment).isNotNull();
        assertThat(comment.getComment()).isEqualTo("some text");
    }

}