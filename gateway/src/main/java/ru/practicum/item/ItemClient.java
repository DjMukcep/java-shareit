package ru.practicum.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.client.BaseClient;
import ru.practicum.item.dto.NewComment;
import ru.practicum.item.dto.NewItem;
import ru.practicum.item.dto.UpdateItem;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    public static final String API_PATH = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PATH))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(long userId, NewItem item) {
        return post("",userId,item);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, UpdateItem item) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItemByName(long userId, String text) {
        Map<String, Object> params = Map.of("text", text);

        return get("/search?text={text}", userId, params);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, NewComment comment) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}
