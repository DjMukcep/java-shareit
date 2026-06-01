package ru.practicum.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.item.dto.NewComment;
import ru.practicum.item.dto.NewItem;
import ru.practicum.item.dto.UpdateItem;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    @DisplayName("Добавить вещь.")
    @SneakyThrows
    void addItem() {
        Long userId = 1L;
        NewItem newItem = new NewItem("name", "desc", true, null);
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED).build();
        ArgumentCaptor<NewItem> captor = ArgumentCaptor.forClass(NewItem.class);

        when(itemClient.addItem(eq(userId), any(NewItem.class))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated());

        verify(itemClient).addItem(eq(userId), captor.capture());
        assertEquals(newItem.getName(), captor.getValue().getName());
        assertEquals(newItem.getDescription(), captor.getValue().getDescription());
        assertTrue(captor.getValue().getIsAvailable());
        assertNull(captor.getValue().getRequestId());
    }

    @Test
    @DisplayName("Вернуть 400 если имя в запросе пустое.")
    void addItem_whenNewItemNameIsEmpty_thenTrowsError() throws Exception {
        Long userId = 1L;
        String wrongName = "";
        NewItem request = new NewItem(wrongName, "desc", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(NewItem.class));
    }

    @Test
    @DisplayName("Вернуть 400 если в имя в запросе подан null.")
    void addItem_whenNewItemNameIsNull_thenTrowsError() throws Exception {
        Long userId = 1L;
        NewItem request = new NewItem(null, "desc", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(NewItem.class));
    }

    @Test
    @DisplayName("Вернуть 400 если описание вещи в запросе пустое.")
    void addItem_whenNewItemDescriptionIsEmpty_thenTrowsError() throws Exception {
        Long userId = 1L;
        String wrongDesc = "";
        NewItem request = new NewItem("name", wrongDesc, true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(NewItem.class));
    }

    @Test
    @DisplayName("Вернуть 400 если в описание вещи в запросе подан null.")
    void addItem_whenNewItemDescriptionIsNull_thenTrowsError() throws Exception {
        Long userId = 1L;
        NewItem request = new NewItem("name", null, true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(NewItem.class));
    }

    @Test
    @DisplayName("Вернуть 400 если поле isAvailable в запросе null.")
    void addItem_whenNewItemAvailableIsNull_thenTrowsError() throws Exception {
        Long userId = 1L;
        NewItem request = new NewItem("name", "desc", null, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addItem(anyLong(), any(NewItem.class));
    }

    @Test
    @DisplayName("Обновить вещь.")
    @SneakyThrows
    void updateItem() {
        Long userId = 1L;
        Long itemId = 2L;
        UpdateItem updateItem = new UpdateItem("name", "desc", true);
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();
        ArgumentCaptor<UpdateItem> captor = ArgumentCaptor.forClass(UpdateItem.class);

        when(itemClient.updateItem(eq(userId), eq(itemId), any(UpdateItem.class))).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItem)))
                .andExpect(status().isOk());

        verify(itemClient).updateItem(eq(userId), eq(itemId), captor.capture());
        assertEquals(updateItem.getName(), captor.getValue().getName());
        assertEquals(updateItem.getDescription(), captor.getValue().getDescription());
        assertTrue(captor.getValue().getIsAvailable());
    }

    @Test
    @DisplayName("Найти вещь.")
    @SneakyThrows
    void getItem() {
        long userId = 1L;
        long itemId = 2L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(itemClient.getItem(userId, itemId)).thenReturn(response);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient).getItem(userId, itemId);
    }

    @Test
    @DisplayName("Получение вещи: сервер вернул ошибку с телом")
    @SneakyThrows
    void getItem_whenBackendReturnsErrorWithBody_thenStatus404AndBodyPassed() {
        long userId = 1L;
        long itemId = 42L;

        Map<String, String> backendError = Map.of("error", "Вещь с ID 42 не найдена");

        ResponseEntity<Object> errorResponse = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(backendError);

        when(itemClient.getItem(userId, itemId)).thenReturn(errorResponse);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Вещь с ID 42 не найдена"));

        verify(itemClient).getItem(userId, itemId);
    }

    @Test
    @DisplayName("Получить все вещи.")
    @SneakyThrows
    void getItems() {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(itemClient.getItems(userId)).thenReturn(response);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemClient).getItems(userId);
    }

    @Test
    @DisplayName("Получить вещи по описанию.")
    @SneakyThrows
    void searchItemByName() {
        long userId = 2L;
        String text = "some_name";
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(itemClient.searchItemByName(userId, text)).thenReturn(response);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk());

        verify(itemClient).searchItemByName(userId, text);
    }

    @Test
    @DisplayName("Поиск с пустым текстом должен возвращать пустой список без вызова клиента.")
    @SneakyThrows
    void searchItemByName_whenTextIsEmpty_thenReturnEmptyList() {
        long userId = 2L;
        String emptyText = "";

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", emptyText))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verifyNoInteractions(itemClient);
    }

    @Test
    @DisplayName("Вернуть 400 - на поиск с null.")
    @SneakyThrows
    void searchItemByName_whenTextIsNull_thenReturnEmptyList() {
        long userId = 2L;

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", (String) null))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }

    @Test
    @DisplayName("Добавить комментарий на вещь.")
    @SneakyThrows
    void addComment() {
        Long userId = 1L;
        Long itemId = 2L;
        NewComment comment = new NewComment();
        comment.setComment("comment");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED).build();
        ArgumentCaptor<NewComment> captor = ArgumentCaptor.forClass(NewComment.class);

        when(itemClient.addComment(eq(userId), eq(itemId), any(NewComment.class))).thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated());

        verify(itemClient).addComment(eq(userId), eq(itemId), captor.capture());
        assertEquals("comment", captor.getValue().getComment());
    }

    @Test
    @DisplayName("Вернуть 400 если  текст комментария на вещь пуст.")
    @SneakyThrows
    void addComment_whenCommentTextIsEmpty_thenThrowException() {
        Long userId = 1L;
        Long itemId = 2L;
        NewComment comment = new NewComment();
        comment.setComment("   ");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any(NewComment.class));
    }

    @Test
    @DisplayName("Вернуть 400 если в текст комментария на вещь подан null.")
    @SneakyThrows
    void addComment_whenCommentTextIsNull_thenThrowException() {
        Long userId = 1L;
        Long itemId = 2L;
        NewComment comment = new NewComment();
        comment.setComment(null);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).addComment(anyLong(), anyLong(), any(NewComment.class));
    }
}