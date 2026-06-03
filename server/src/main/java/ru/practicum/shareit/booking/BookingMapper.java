package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        Item item = booking.getItem();
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingDto.Booker(booking.getBooker().getId()),
                new BookingDto.BookingItem(item.getId(),item.getName())
        );
    }

    public static List<BookingDto> toBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    public static Booking toBooking(User booker, Item item, NewBooking newBooking) {
        return Booking.builder()
                .start(newBooking.getStart())
                .end(newBooking.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }
}
