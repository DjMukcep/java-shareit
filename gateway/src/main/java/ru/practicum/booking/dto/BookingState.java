package ru.practicum.booking.dto;

import java.util.Optional;

public enum BookingState {
    WAITING, REJECTED, PAST, FUTURE, CURRENT, ALL;

    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
