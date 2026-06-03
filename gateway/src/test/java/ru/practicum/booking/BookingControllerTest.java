package ru.practicum.booking;

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
import ru.practicum.booking.dto.BookingState;
import ru.practicum.booking.dto.NewBooking;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient client;

    @Test
    @DisplayName("Добавить новый запрос на бронирование.")
    @SneakyThrows
    void addBooking() {
        Long userId = 1L;
        NewBooking newBooking = new NewBooking(
                2L, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(5));
        ArgumentCaptor<NewBooking> argumentCaptor = ArgumentCaptor.forClass(NewBooking.class);
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(client.bookItem(eq(userId), any(NewBooking.class))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isOk());

        verify(client).bookItem(eq(userId), argumentCaptor.capture());
        assertEquals(newBooking, argumentCaptor.getValue());
        assertEquals(newBooking.getStart(), argumentCaptor.getValue().getStart());
        assertEquals(newBooking.getEnd(), argumentCaptor.getValue().getEnd());
        assertEquals(newBooking.getItemId(), argumentCaptor.getValue().getItemId());
    }

    @Test
    @DisplayName("Вернуть 400 если новый запрос на бронирование содержит null в поле itemId.")
    @SneakyThrows
    void addBooking_whenItemIdIsNull_thenThrowsException() {
        Long userId = 1L;
        NewBooking newBooking = new NewBooking(
                null, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(5));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest());

        verify(client, never()).bookItem(eq(userId), any(NewBooking.class));
    }

    @Test
    @DisplayName("Вернуть 400 если новый запрос в поле start содержит дату в прошлом.")
    @SneakyThrows
    void addBooking_whenStartInPast_thenThrowsException() {
        Long userId = 1L;
        NewBooking newBooking = new NewBooking(
                2L, LocalDateTime.now().minusSeconds(1), LocalDateTime.now().plusSeconds(5));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest());

        verify(client, never()).bookItem(eq(userId), any(NewBooking.class));
    }

    @Test
    @DisplayName("Вернуть 400 если новый запрос в поле end содержит дату в прошлом.")
    @SneakyThrows
    void addBooking_whenEndInPast_thenThrowsException() {
        Long userId = 1L;
        NewBooking newBooking = new NewBooking(
                2L, LocalDateTime.now().plusSeconds(1), LocalDateTime.now().minusSeconds(5));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest());

        verify(client, never()).bookItem(eq(userId), any(NewBooking.class));
    }

    @Test
    @DisplayName("Вернуть 400 если новый запрос в поле end содержит null")
    @SneakyThrows
    void addBooking_whenEndIsNull_thenThrowsException() {
        Long userId = 1L;
        NewBooking newBooking = new NewBooking(
                2L, LocalDateTime.now().plusSeconds(1), null);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest());

        verify(client, never()).bookItem(eq(userId), any(NewBooking.class));
    }

    @Test
    @DisplayName("Вернуть 400 если новый запрос в поле start содержит null")
    @SneakyThrows
    void addBooking_whenStartIsNull_thenThrowsException() {
        Long userId = 1L;
        NewBooking newBooking = new NewBooking(
                2L, null, LocalDateTime.now().plusSeconds(5));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest());

        verify(client, never()).bookItem(eq(userId), any(NewBooking.class));
    }

    @Test
    @DisplayName("Вернуть 400 если в новый запросе дата начала позже даты конца бронирования.")
    @SneakyThrows
    void addBooking_whenStartIsAfterEnd_thenThrowsException() {
        Long userId = 1L;
        NewBooking newBooking = new NewBooking(
                2L, LocalDateTime.now().plusSeconds(10), LocalDateTime.now().plusSeconds(5));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest());

        verify(client, never()).bookItem(eq(userId), any(NewBooking.class));
    }

    @Test
    @DisplayName("Подтвердить или отклонить запрос на бронирование.")
    @SneakyThrows
    void updateBooking() {
        Long userId = 1L;
        Long bookingId = 2L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(client.updateBooking(eq(userId), eq(bookingId), eq(true))).thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(client).updateBooking(eq(userId), eq(bookingId), eq(true));
    }

    @Test
    @DisplayName("Получить данные о конкретном бронировании.")
    @SneakyThrows
    void getBooking() {
        Long userId = 1L;
        Long bookingId = 2L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(client.getBooking(eq(userId), eq(bookingId))).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(client).getBooking(eq(userId), eq(bookingId));
    }

    @Test
    @DisplayName("Получить список всех бронирований текущего пользователя.")
    @SneakyThrows
    void getBookings() {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(client.getBookings(userId, BookingState.ALL)).thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state","all"))
                .andExpect(status().isOk());

        verify(client).getBookings(userId, BookingState.ALL);
    }

    @Test
    @DisplayName("Вернуть 400 если в параметры state подан неизвестный статус.")
    @SneakyThrows
    void getBookings_whenBookingStateIsUnknown_thenThrowsException() {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(client.getBookings(userId, BookingState.ALL)).thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state","unknown"))
                .andExpect(status().isBadRequest());

        verify(client, never()).getBookings(anyLong(), any(BookingState.class));
    }

    @Test
    @DisplayName("Получить список бронирований для всех вещей владельца.")
    @SneakyThrows
    void getOwnerBookings() {
        long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).build();

        when(client.getOwnerBookings(userId, BookingState.ALL)).thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state","all"))
                .andExpect(status().isOk());

        verify(client).getOwnerBookings(userId, BookingState.ALL);
    }

    @Test
    @DisplayName("Вернуть 400 если в параметры state подан неизвестный статус.")
    @SneakyThrows
    void getOwnerBookings_whenBookingStateIsUnknown_thenThrowsException() {
        long userId = 1L;

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state","unknown"))
                .andExpect(status().isBadRequest());

        verify(client, never()).getBookings(anyLong(), any(BookingState.class));
    }
}