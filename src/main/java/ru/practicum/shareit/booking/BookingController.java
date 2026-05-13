package ru.practicum.shareit.booking;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid NewBooking newBooking) {
        return BookingMapper.toBookingDto(bookingService.create(userId, newBooking));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam(name = "approved") boolean isApproved) {
        return BookingMapper.toBookingDto(bookingService.updateBooking(userId, bookingId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId) {
        return BookingMapper.toBookingDto(bookingService.getBooking(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        return BookingMapper.toBookingDto(bookingService.getBookerBookings(userId, state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        return BookingMapper.toBookingDto(bookingService.getOwnerBookings(userId, state));
    }
}
