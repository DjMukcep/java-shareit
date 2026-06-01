package ru.practicum.request;

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
import ru.practicum.request.dto.NewItemRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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
    private ItemRequestClient requestClient;


    @Test
    @DisplayName("Добавить запрос на вещь.")
    @SneakyThrows
    void addRequest() {
        Long userId = 1L;
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("description");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.CREATED).build();
        ArgumentCaptor<NewItemRequest> captor = ArgumentCaptor.forClass(NewItemRequest.class);

        when(requestClient.addRequest(eq(userId), any(NewItemRequest.class))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemRequest)))
                .andExpect(status().isCreated());

        verify(requestClient).addRequest(eq(userId), captor.capture());
        assertEquals("description", captor.getValue().getDescription());
    }

    @Test
    @DisplayName("Вернуть 400 если при создании запроса описание вещи пустое.")
    @SneakyThrows
    void addRequest_whenDescriptionIsBlank_thenReturnBadRequest() {
        Long userId = 1L;

        NewItemRequest request = new NewItemRequest();
        request.setDescription("   ");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(requestClient, never()).addRequest(anyLong(), any());
    }

    @Test
    @DisplayName("Бэкенд вернул ошибку с телом.")
    @SneakyThrows
    void addRequest_whenBackendReturnsError_thenGatewayRebuildsIt() {
        Long userId = 999L;

        NewItemRequest request = new NewItemRequest();
        request.setDescription("request-description");

        Map<String, String> backendErrorBody = Map.of("error", "Пользователь с id:999 не найден");

        ResponseEntity<Object> errorResponse = ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(backendErrorBody);

        when(requestClient.addRequest(eq(userId), any())).thenReturn(errorResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь с id:999 не найден"));

        verify(requestClient).addRequest(eq(userId), any());
    }


    @Test
    @DisplayName("Получить запрос пользователя вместе с откликами.")
    @SneakyThrows
    void getRequest() {
        long userId = 1L;
        long requestId = 2L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(requestClient.getRequest(userId, requestId)).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestClient).getRequest(userId, requestId);
    }

    @Test
    @DisplayName("Получить все запросы пользователей.")
    @SneakyThrows
    void getRequests() {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(requestClient.getRequests(userId)).thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestClient).getRequests(userId);
    }

    @Test
    @DisplayName("Получить все запросы пользователя вместе с откликами.")
    @SneakyThrows
    void getUserRequests() {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(requestClient.getUserRequests(userId)).thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(requestClient).getUserRequests(userId);
    }
}