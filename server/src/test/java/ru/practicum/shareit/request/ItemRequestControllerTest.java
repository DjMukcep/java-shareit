package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;


    @Test
    @DisplayName("Добавить запрос на вещь.")
    @SneakyThrows
    void addRequest() {
        Long userId = 1L;
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("description");
        RequestDto requestDto = new RequestDto(
                1L,
                "description",
                LocalDateTime.now().withNano(0)
        );
        ArgumentCaptor<NewItemRequest> captor = ArgumentCaptor.forClass(NewItemRequest.class);

        when(itemRequestService.addRequest(any(NewItemRequest.class), eq(1L))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(objectMapper.writeValueAsString(requestDto)));

        verify(itemRequestService).addRequest(captor.capture(), eq(userId));
        assertEquals("description", captor.getValue().getDescription());
    }

    @Test
    @DisplayName("Получить запрос пользователя вместе с откликами.")
    @SneakyThrows
    void getRequest() {
        Long userId = 1L;
        Long requestId = 2L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        when(itemRequestService.getRequest(userId, requestId)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));

        verify(itemRequestService).getRequest(userId, requestId);
    }

    @Test
    @DisplayName("Вернуть 404 если запрос не найден.")
    @SneakyThrows
    void getRequest_whenWrongRequestId_thenThrowNotFound() {
        Long userId = 1L;
        Long requestId = 999L;

        when(itemRequestService.getRequest(userId, requestId))
                .thenThrow(new NotFoundException(String.format(
                        "Запрос вещи с id: %d не найден.", requestId)));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Запрос вещи с id: 999 не найден."));

        verify(itemRequestService).getRequest(userId, requestId);
    }

    @Test
    @DisplayName("Получить все запросы пользователей.")
    @SneakyThrows
    void getRequests() {
        Long userId = 1L;
        List<RequestDto> requests = List.of(new RequestDto(
                2L,
                "desc1",
                LocalDateTime.now().withNano(0)
        ), new RequestDto(
                3L,
                "desc2",
                LocalDateTime.now().withNano(0)
        ));

        when(itemRequestService.getRequests(userId)).thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(requests)));

        verify(itemRequestService).getRequests(userId);
    }

    @Test
    @DisplayName("Получить все запросы пользователя вместе с откликами.")
    @SneakyThrows
    void getUserRequests() {
        Long userId = 1L;
        List<ItemRequestDto> itemRequests = List.of(
                ItemRequestDto.builder()
                        .id(1L)
                        .description("desc1")
                        .createdAt(LocalDateTime.now().withNano(0))
                        .items(List.of(
                                new ItemRequestDto.Item(
                                        10L,
                                        "item1",
                                        1L
                                )
                        ))
                        .build(),
                ItemRequestDto.builder()
                        .id(2L)
                        .description("desc2")
                        .createdAt(LocalDateTime.now().withNano(0))
                        .items(List.of())
                        .build()
        );

        when(itemRequestService.getUserRequests(userId)).thenReturn(itemRequests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequests)));

        verify(itemRequestService).getUserRequests(userId);
    }
}