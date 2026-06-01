package ru.practicum.request;

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
import ru.practicum.request.dto.NewItemRequest;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(ItemRequestClient.class)
class ItemRequestClientTest {

    @Autowired
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private final long userId = 1L;

    @Test
    @DisplayName("addRequest: успешное добавление нового запроса на вещь")
    void addRequest() throws Exception {
        NewItemRequest newItemRequest = new NewItemRequest("some-description");
        String expectedResponse = "{\"id\": 10, \"description\": \"some-description\"}";

        mockServer.expect(requestTo(containsString("/requests")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(newItemRequest)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        ResponseEntity<Object> response = itemRequestClient.addRequest(userId, newItemRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        mockServer.verify();
    }

    @Test
    @DisplayName("addRequest: сервер вернул 404 — проверка ответа клиента")
    void addRequest_whenBackendReturns404_thenBaseClientRebuildsIt() {
        long userId = 999L;
        NewItemRequest request = new NewItemRequest();
        request.setDescription("request-description");

        String errorJson = "{\"error\": \"Пользователь с id:999 не найден\"}";


        mockServer.expect(requestTo(containsString("/requests")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorJson));

        ResponseEntity<Object> response = itemRequestClient.addRequest(userId, request);

        assertNotNull(response.getBody());
        String responseBodyText = new String((byte[]) response.getBody(), StandardCharsets.UTF_8);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(responseBodyText.contains("Пользователь с id:999 не найден"));
        mockServer.verify();
    }

    @Test
    @DisplayName("getRequest: успешное получение конкретного запроса по ID")
    void getRequest() {
        long requestId = 10L;
        mockServer.expect(requestTo(containsString("/requests/" + requestId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}"));

        ResponseEntity<Object> response = itemRequestClient.getRequest(userId, requestId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("getRequests: успешное получение списка чужих запросов (/all)")
    void getRequests() {
        mockServer.expect(requestTo(containsString("/requests/all")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemRequestClient.getRequests(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("getUserRequests: успешное получение списка запросов самого пользователя")
    void getUserRequests() {
        mockServer.expect(requestTo(org.hamcrest.Matchers.allOf(
                        containsString("/requests"),
                        org.hamcrest.Matchers.not(containsString("/all"))
                )))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemRequestClient.getUserRequests(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}