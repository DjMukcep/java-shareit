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
class UpdateUserTest {

    @Autowired
    private JacksonTester<UpdateUser> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Десериализовать Json в класс UpdateUser.")
    @SneakyThrows
    void deserializeUpdateUser() {
        String content = objectMapper.writeValueAsString(
                Map.of(
                        "name","some name",
                        "email", "some@email.com"
                )
        );

        UpdateUser user = json.parseObject(content);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("some name");
        assertThat(user.getEmail()).isEqualTo("some@email.com");
    }

    @Test
    @DisplayName("Десериализовать Json в класс UpdateUser только с именем пользователя.")
    @SneakyThrows
    void deserializeUpdateUser_withOnlyNameField() {
        String content = objectMapper.writeValueAsString(
                Map.of(
                        "name","some name"
                )
        );

        UpdateUser user = json.parseObject(content);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("some name");
        assertThat(user.getEmail()).isEqualTo(null);
    }

    @Test
    @DisplayName("Десериализовать Json в класс UpdateUser только с полем email.")
    @SneakyThrows
    void deserializeUpdateUser_withOnlyEmailField() {
        String content = objectMapper.writeValueAsString(
                Map.of(
                        "email","some@email.com"
                )
        );

        UpdateUser user = json.parseObject(content);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(null);
        assertThat(user.getEmail()).isEqualTo("some@email.com");
    }

    @Test
    @DisplayName("hasUsername: проверка граничных состояний имени")
    void testHasUsername() {
        UpdateUser user = new UpdateUser();

        user.setName(null);
        assertThat(user.hasUsername()).isFalse();

        user.setName("   ");
        assertThat(user.hasUsername()).isFalse();

        user.setName("Иван");
        assertThat(user.hasUsername()).isTrue();
    }

    @Test
    @DisplayName("hasEmail: проверка граничных состояний email")
    void testHasEmail() {
        UpdateUser user = new UpdateUser();

        user.setEmail(null);
        assertThat(user.hasEmail()).isFalse();

        user.setEmail("");
        assertThat(user.hasEmail()).isFalse();

        user.setEmail("user@mail.com");
        assertThat(user.hasEmail()).isTrue();
    }
}