package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.NewBooking;

import java.util.List;

public interface BookingService {

    Booking create(Long userId, NewBooking newBooking);

    Booking updateBooking(Long ownerId, Long bookingId, boolean isApproved);

    Booking getBooking(Long bookingId, Long userId);

    List<Booking> getBookerBookings(Long userId, String state);

    List<Booking> getOwnerBookings(Long userId, String state);

}
