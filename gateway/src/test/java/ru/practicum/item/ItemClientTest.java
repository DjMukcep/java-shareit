package ru.practicum.item;

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
import ru.practicum.item.dto.NewComment;
import ru.practicum.item.dto.NewItem;
import ru.practicum.item.dto.UpdateItem;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(ItemClient.class)
class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private final long userId = 1L;
    private final long itemId = 42L;

    @Test
    @DisplayName("addItem: успешное добавление новой вещи")
    void addItem() throws Exception {
        NewItem newItem = new NewItem(
                "item-name",
                "item-description",
                true,
                null);
        String expectedResponse = "{\"id\": 42, \"name\": \"item-name\"}";

        mockServer.expect(requestTo(containsString("/items")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(newItem)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        ResponseEntity<Object> response = itemClient.addItem(userId, newItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        mockServer.verify();
    }

    @Test
    @DisplayName("updateItem: успешное частичное обновление вещи")
    void updateItem() throws Exception {
        UpdateItem updateItem = new UpdateItem();
        updateItem.setName("some-name");
        String expectedResponse = "{\"id\": 42, \"name\": \"some-name\"}";

        mockServer.expect(requestTo(containsString("/items/" + itemId)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(updateItem)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        ResponseEntity<Object> response = itemClient.updateItem(userId, itemId, updateItem);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("getItem: успешное получение вещи по ID")
    void getItem() {
        mockServer.expect(requestTo(containsString("/items/" + itemId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}"));

        ResponseEntity<Object> response = itemClient.getItem(userId, itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("getItems: успешное получение всех вещей пользователя")
    void getItems() {
        mockServer.expect(requestTo(containsString("/items")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemClient.getItems(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("searchItemByName: успешный поиск вещей по строке text")
    void searchItemByName() {
        String searchText = "дрель";
        String encodedText = URLEncoder.encode(searchText, StandardCharsets.UTF_8);

        mockServer.expect(requestTo(containsString("/items/search?text=" + encodedText)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemClient.searchItemByName(userId, searchText);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("addComment: успешное добавление комментария к вещи")
    void addComment() throws Exception {
        NewComment comment = new NewComment();
        comment.setComment("comment");
        String expectedResponse = "{\"id\": 1, \"text\": \"comment\"}";

        mockServer.expect(requestTo(containsString("/items/" + itemId + "/comment")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(comment)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        ResponseEntity<Object> response = itemClient.addComment(userId, itemId, comment);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}