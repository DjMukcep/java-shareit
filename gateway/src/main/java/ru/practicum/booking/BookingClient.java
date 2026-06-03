package ru.practicum.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.booking.dto.BookingState;
import ru.practicum.booking.dto.NewBooking;
import ru.practicum.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    public static final String API_PATH = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PATH))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> bookItem(long userId, NewBooking newBooking) {
        return post("", userId, newBooking);
    }

    public ResponseEntity<Object> updateBooking(long userId, long bookingId, boolean isApproved) {
        Map<String, Object> params = Map.of("approved", isApproved);

        return patch("/" + bookingId + "?approved={approved}", userId, params, null);
    }

    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state) {
        Map<String, Object> params = Map.of("state", state.name());

        return get("?state={state}", userId, params);
    }

    public ResponseEntity<Object> getOwnerBookings(long userId, BookingState state) {
        Map<String, Object> params = Map.of("state", state.name());

        return get("/owner?state={state}", userId, params);
    }
}
