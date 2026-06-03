package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    @DisplayName("Добавить вещь.")
    @SneakyThrows
    void addItem() {
        Long userId = 1L;
        NewItem newItem = new NewItem("name", "desc", true, null);
        ItemDto itemDto = new ItemDto(1L, "name", "desc", true, null);
        ArgumentCaptor<NewItem> captor = ArgumentCaptor.forClass(NewItem.class);

        when(itemService.addItem(eq(userId), any(NewItem.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        verify(itemService).addItem(eq(userId), captor.capture());
        assertEquals(newItem.getName(), captor.getValue().getName());
        assertEquals(newItem.getDescription(), captor.getValue().getDescription());
        assertTrue(captor.getValue().getIsAvailable());
        assertNull(captor.getValue().getRequestId());
    }

    @Test
    @DisplayName("Обновить вещь.")
    @SneakyThrows
    void updateItem() {
        Long userId = 1L;
        Long itemId = 2L;
        UpdateItem updateItem = new UpdateItem("name", "desc", true);
        ItemDto itemDto = new ItemDto(itemId, "name", "desc", true, null);
        ArgumentCaptor<UpdateItem> captor = ArgumentCaptor.forClass(UpdateItem.class);

        when(itemService.updateItem(eq(userId), eq(itemId), any(UpdateItem.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItem)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        verify(itemService).updateItem(eq(userId), eq(itemId), captor.capture());
        assertEquals(updateItem.getName(), captor.getValue().getName());
        assertEquals(updateItem.getDescription(), captor.getValue().getDescription());
        assertTrue(captor.getValue().getIsAvailable());
    }

    @Test
    @DisplayName("Вернуть 400 при обновлении вещи если пользователь не является ее владельцем.")
    @SneakyThrows
    void updateItem_whenWrongUser_thenThrowsError() {
        Long userId = 999L;
        Long itemId = 1L;
        UpdateItem updateItem = new UpdateItem("name", "desc", true);

        when(itemService.updateItem(eq(userId), eq(itemId), any(UpdateItem.class)))
                .thenThrow(new ValidationException(
                        String.format("Пользователь id:%d не является владельцем вещи id:%d", userId, itemId)
                ));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Пользователь id:999 не является владельцем вещи id:1"));

        verify(itemService).updateItem(userId, itemId, updateItem);
    }

    @Test
    @DisplayName("Вернуть 404 при обновлении вещи если вещь не найдена.")
    @SneakyThrows
    void updateItem_WhenWrongItem_thenThrowsError() {
        Long userId = 1L;
        Long itemId = 999L;
        UpdateItem updateItem = new UpdateItem("name", "desc", true);

        when(itemService.updateItem(eq(userId), eq(itemId), any(UpdateItem.class)))
                .thenThrow(new NotFoundException("Вещь с id: " + itemId + " не найдена."));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItem)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Вещь с id: 999 не найдена."));

        verify(itemService).updateItem(userId, itemId, updateItem);
    }

    @Test
    @DisplayName("Найти вещь.")
    @SneakyThrows
    void getItem() {
        Long userId = 1L;
        Long itemId = 2L;
        ItemWithComments item = ItemWithComments.builder()
                .id(itemId)
                .name("item")
                .description("desc")
                .isAvailable(true)
                .lastBooking(LocalDateTime.now().minusDays(1).withNano(0))
                .nextBooking(LocalDateTime.now().plusDays(1).withNano(0))
                .comments(List.of(
                        CommentDto.builder()
                                .id(1L)
                                .text("text")
                                .authorName("author")
                                .created(LocalDateTime.now().withNano(0))
                                .build(),
                        CommentDto.builder()
                                .id(2L)
                                .text("text")
                                .authorName("author")
                                .created(LocalDateTime.now().withNano(0))
                                .build())
                ).build();

        when(itemService.getItemWithComments(userId, itemId)).thenReturn(item);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(item)));

        verify(itemService).getItemWithComments(userId, itemId);
    }

    @Test
    @DisplayName("Получить все вещи.")
    @SneakyThrows
    void getItems() {
        Long userId = 1L;
        List<ItemWithComments> items = List.of(
                ItemWithComments.builder().id(1L).build(),
                ItemWithComments.builder().id(2L).build()
        );

        when(itemService.getItemsWithComments(userId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        verify(itemService).getItemsWithComments(userId);
    }

    @Test
    @DisplayName("Получить вещи по описанию.")
    @SneakyThrows
    void searchItemByName() {
        Long userId = 2L;
        String text = "some_name";
        List<ItemDto> items = List.of(
                new ItemDto(1L, "some_name", "desc1", true, null),
                new ItemDto(2L, "name2", "some_name", false, null)
        );

        when(itemService.searchItem(userId, text)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));

        verify(itemService).searchItem(userId, text);
    }

    @Test
    @DisplayName("Добавить комментарий на вещь.")
    @SneakyThrows
    void addComment() {
        Long userId = 1L;
        Long itemId = 2L;
        NewComment comment = new NewComment();
        comment.setComment("comment");
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.now().withNano(0))
                .build();
        ArgumentCaptor<NewComment> captor = ArgumentCaptor.forClass(NewComment.class);

        when(itemService.addComment(eq(userId), eq(itemId), any(NewComment.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));

        verify(itemService).addComment(eq(userId), eq(itemId), captor.capture());
        assertEquals("comment", captor.getValue().getComment());
    }

    @Test
    @DisplayName("Вернуть 400 когда у пользователя нет прав на оставление комментария.")
    @SneakyThrows
    void addComment_whenUserHasNotRightsForComment_thenThrowsError() {
        Long userId = 1L;
        Long itemId = 2L;
        NewComment comment = new NewComment();
        comment.setComment("comment");
        ArgumentCaptor<NewComment> captor = ArgumentCaptor.forClass(NewComment.class);

        when(itemService.addComment(eq(userId), eq(itemId), any(NewComment.class)))
                .thenThrow(new ValidationException(
                                String.format(
                                        "У Пользователя с id:%d нет прав на оставление комментария к вещи с id:%d.",
                                        userId, itemId)
                        )
                );

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("У Пользователя с id:1 нет прав на оставление комментария к вещи с id:2.")
                );

        verify(itemService).addComment(eq(userId), eq(itemId), captor.capture());
        assertEquals("comment", captor.getValue().getComment());
    }
}