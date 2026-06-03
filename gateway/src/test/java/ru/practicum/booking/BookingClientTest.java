package ru.practicum.booking;

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
import ru.practicum.booking.dto.BookingState;
import ru.practicum.booking.dto.NewBooking;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(BookingClient.class)
class BookingClientTest {

    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private MockRestServiceServer mockServer; // Симулирует удаленный shareit-server

    @Autowired
    private ObjectMapper objectMapper; // Для перевода объектов в JSON строчки

    private final long userId = 1L;

    private final String baseUrl = "http://localhost:9090/bookings";

    @Test
    @DisplayName("bookItem: успешная отправка POST-запроса с телом")
    void bookItem() throws Exception {
        NewBooking newBooking = new NewBooking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        String expectedResponse = "{\"id\": 1, \"status\": \"WAITING\"}";

        mockServer.expect(requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(newBooking)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        ResponseEntity<Object> response = bookingClient.bookItem(userId, newBooking);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        mockServer.verify();
    }

    @Test
    @DisplayName("updateBooking: успешный PATCH-запрос с query-параметром approved")
    void updateBooking() {
        long bookingId = 7L;
        String expectedResponse = "{\"id\": 7, \"status\": \"APPROVED\"}";

        // Обратите внимание: URI собирается с query string
        mockServer.expect(requestTo(baseUrl + "/7?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(expectedResponse));

        ResponseEntity<Object> response = bookingClient.updateBooking(userId, bookingId, true);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("getBooking: успешный GET-запрос конкретного бронирования по ID")
    void getBooking() {
        long bookingId = 7L;

        mockServer.expect(requestTo(baseUrl + "/7"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{}"));

        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("getBookings: успешный GET-запрос списка бронирований со значением state")
    void getBookings() {
        mockServer.expect(requestTo(baseUrl + "?state=FUTURE"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = bookingClient.getBookings(userId, BookingState.FUTURE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    @DisplayName("getOwnerBookings: успешный GET-запрос бронирований владельца вещей")
    void getOwnerBookings() {
        mockServer.expect(requestTo(baseUrl + "/owner?state=ALL"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = bookingClient.getOwnerBookings(userId, BookingState.ALL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockServer.verify();
    }
}