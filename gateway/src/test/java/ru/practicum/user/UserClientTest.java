package ru.practicum.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.user.dto.NewUser;
import ru.practicum.user.dto.UpdateUser;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(UserClient.class)
class UserClientTest {

    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private final long userId = 1L;

    @Test
    @DisplayName("createUser: успешное создание нового пользователя")
    void createUser() throws Exception {
        NewUser newUser = new NewUser();
        newUser.setName("Ivan");
        newUser.setEmail("ivan@email.com");
        String expectedResponse = "{\"id\": 1, \"name\": \"Ivan\", \"email\": \"ivan@email.com\"}";

        mockServer.expect(requestTo(containsString("/users")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(newUser)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        ResponseEntity<Object> response = userClient.createUser(newUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        mockServer.verify();
    }

    @Test
    @DisplayName("updateUser: успешное обновление данных пользователя")
    void updateUser() throws Exception {
        UpdateUser updateUser = new UpdateUser();
        updateUser.setName("Ivan Updated");
        String expectedResponse = "{\"id\": 1, \"name\": \"Ivan Updated\", \"email\": \"ivan@email.com\"}";

        mockServer.expect(requestTo(containsString("/users/" + userId)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json(objectMapper.writeValueAsString(updateUser)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        ResponseEntity<Object> response = userClient.updateUser(userId, updateUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("findUserById: успешное получение пользователя по его ID")
    void findUserById() {
        mockServer.expect(requestTo(containsString("/users/" + userId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}"));

        ResponseEntity<Object> response = userClient.findUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("deleteUser: успешное удаление пользователя")
    void deleteUser() {
        mockServer.expect(requestTo(containsString("/users/" + userId)))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}