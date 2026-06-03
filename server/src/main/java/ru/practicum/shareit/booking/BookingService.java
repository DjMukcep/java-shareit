package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;

import java.util.List;

public interface BookingService {

    BookingDto create(Long userId, NewBooking newBooking);

    BookingDto updateBooking(Long ownerId, Long bookingId, boolean isApproved);

    BookingDto getBooking(Long bookingId, Long userId);

    List<BookingDto> getBookerBookings(Long userId, String state);

    List<BookingDto> getOwnerBookings(Long userId, String state);

}
