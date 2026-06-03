package ru.practicum.user.dto;

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
class NewUserTest {

    @Autowired
    private JacksonTester<NewUser> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Десериализовать Json в класс NewUser.")
    @SneakyThrows
    void deserializeNewUser() {
        String content = objectMapper.writeValueAsString(
                Map.of(
                        "name","some name",
                        "email", "some@email.com"
                )
        );

        NewUser user = json.parseObject(content);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("some name");
        assertThat(user.getEmail()).isEqualTo("some@email.com");
    }
}