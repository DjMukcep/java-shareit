package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static BookingDto bookingDto;
    private static Long userId;

    @BeforeAll
    public static void setUp() {
        userId = 1L;
        bookingDto = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusSeconds(5))
                .status(BookingStatus.WAITING)
                .booker(new BookingDto.Booker(userId))
                .item(new BookingDto.BookingItem(1L, "item"))
                .build();
    }

    @Test
    @DisplayName("Добавить новый запрос на бронирование.")
    @SneakyThrows
    void addBooking() {
        NewBooking newBooking = new NewBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusSeconds(5));

        ArgumentCaptor<NewBooking> argumentCaptor = ArgumentCaptor.forClass(NewBooking.class);

        when(bookingService.create(eq(userId), any(NewBooking.class))).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

        verify(bookingService).create(eq(userId), argumentCaptor.capture());
        assertEquals(newBooking, argumentCaptor.getValue());
        assertEquals(newBooking.getStart(), argumentCaptor.getValue().getStart());
        assertEquals(newBooking.getEnd(), argumentCaptor.getValue().getEnd());
        assertEquals(newBooking.getItemId(), argumentCaptor.getValue().getItemId());
    }

    @Test
    @DisplayName("Вернуть 400 если вещь недоступна для бронирования.")
    @SneakyThrows
    void addBooking_whenItemNotAvailable_thenThrowsError() throws Exception {
        NewBooking newBooking = new NewBooking(
                1L, LocalDateTime.now(), LocalDateTime.now().plusSeconds(5));
        ArgumentCaptor<NewBooking> argumentCaptor = ArgumentCaptor.forClass(NewBooking.class);

        when(bookingService.create(eq(userId), any(NewBooking.class))).thenThrow(
                new ValidationException(
                        String.format("Вещь id:%d недоступна для бронирования.", newBooking.getItemId()))
        );

        mockMvc.perform(post("/bookings")
                .header("X-Sharer-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Вещь id:1 недоступна для бронирования."));

        verify(bookingService).create(eq(userId), argumentCaptor.capture());
        assertEquals(newBooking, argumentCaptor.getValue());
        assertEquals(newBooking.getStart(), argumentCaptor.getValue().getStart());
        assertEquals(newBooking.getEnd(), argumentCaptor.getValue().getEnd());
        assertEquals(newBooking.getItemId(), argumentCaptor.getValue().getItemId());
    }

    @Test
    @DisplayName("Подтвердить или отклонить запрос на бронирование.")
    @SneakyThrows
    void updateBooking() {
        Long bookingId = 2L;

        when(bookingService.updateBooking(eq(userId), eq(bookingId), eq(true))).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

        verify(bookingService).updateBooking(eq(userId), eq(bookingId), eq(true));
    }

    @Test
    @DisplayName("Получить данные о конкретном бронировании.")
    @SneakyThrows
    void getBooking() {
        Long bookingId = 2L;

        when(bookingService.getBooking(eq(bookingId), eq(userId))).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDto)));

        verify(bookingService).getBooking(eq(bookingId), eq(userId));
    }

    @Test
    @DisplayName("Получить список всех бронирований текущего пользователя.")
    @SneakyThrows
    void getBookings() {
        List<BookingDto> bookingDtoList = List.of(bookingDto);

        when(bookingService.getBookerBookings(userId, "all")).thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", userId)
                .param("state","all"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoList)));

        verify(bookingService).getBookerBookings(userId, "all");
    }

    @Test
    @DisplayName("Получить список бронирований для всех вещей владельца.")
    @SneakyThrows
    void getOwnerBookings() {
        List<BookingDto> bookingDtoList = List.of(bookingDto);

        when(bookingService.getOwnerBookings(userId, "all")).thenReturn(bookingDtoList);

        mockMvc.perform(get("/bookings/owner")
        .header("X-Sharer-User-Id", userId)
                .param("state","all"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoList)));

        verify(bookingService).getOwnerBookings(userId, "all");
    }
}