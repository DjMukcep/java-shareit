package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public Booking create(Long userId, NewBooking newBooking) {
        User booker = userService.getUser(userId);
        Item item = itemService.getItem(newBooking.getItemId());

        if (!item.isAvailable()) {
            throw new ValidationException(
                    String.format("Вещь id:%d недоступна для бронирования.", item.getId()));
        }

        Booking booking = BookingMapper.toBooking(booker, item, newBooking);
        Booking storedBooking = bookingRepository.save(booking);
        log.info("Новый запрос на бронирование id:{}, статус:{} время:{}",
                storedBooking.getId(), storedBooking.getStatus(), storedBooking.getStart());

        return storedBooking;
    }

    @Override
    @Transactional
    public Booking updateBooking(Long ownerId, Long bookingId, boolean isApproved) {
        Booking booking = getBookingWithItemAndBooker(bookingId);
        Item item = booking.getItem();

        validateItemUpdate(item, ownerId);
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking storedBooking = bookingRepository.save(booking);
        log.info("Статус бронирования id:{} обновлен на {} для вещи id:{} время:{}",
                storedBooking.getId(), storedBooking.getStatus(), item.getId(), storedBooking.getStart());

        return getBookingWithItemAndBooker(storedBooking.getId());
    }

    @Override
    public Booking getBooking(Long bookingId, Long userId) {
        Booking booking = getBookingWithItemBookerOwner(bookingId);
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!(userId.equals(bookerId) || userId.equals(ownerId))) {
            throw new ValidationException(String.format(
                    "У пользователя id:%d нет прав на получение данных о бронировании.", userId
            ));
        }
        return booking;
    }

    @Override
    public List<Booking> getBookerBookings(Long userId, String state) {
        checkUserExists(userId);

        switch (state.toUpperCase()) {
            case "REJECTED", "WAITING" -> {
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.valueOf(state));
            }
            case "PAST" -> {
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now());
            }
            case "FUTURE" -> {
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
            }
            case "CURRENT" -> {
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                userId, now, now);
            }
            case "ALL" -> {
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            }
            default -> throw new ValidationException("Статус не поддерживается: " + state);
        }
    }

    @Override
    public List<Booking> getOwnerBookings(Long userId, String state) {
        checkUserExists(userId);

        if (itemService.getItems(userId).isEmpty()) {
            return List.of();
        }

        switch (state.toUpperCase()) {
            case "REJECTED", "WAITING" -> {
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.valueOf(state));
            }
            case "PAST" -> {
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now());
            }
            case "FUTURE" -> {
                return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
            }
            case "CURRENT" -> {
                LocalDateTime now = LocalDateTime.now();
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now);
            }
            case "ALL" -> {
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            }
            default -> throw new ValidationException("Статус не поддерживаатся: " + state);
        }
    }

    private Booking getBookingWithItemAndBooker(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронирование с id:%d не найдено.", bookingId)
                ));
    }

    private void checkUserExists(Long userId) {
        userService.getUser(userId);
    }

    private Booking getBookingWithItemBookerOwner(Long bookingId) {
        return bookingRepository.findFullBookingInfoById(bookingId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Бронирование с id:%d не найдено.", bookingId)
                ));
    }

    private void validateItemUpdate(Item item, Long ownerId) {
        if (!item.isAvailable()) {
            throw new ValidationException(
                    String.format("Вещь id:%d недоступна для бронирования.", item.getId()));
        }

        if (!Objects.equals(ownerId, item.getOwner().getId())) {
            throw new ValidationException(
                    String.format("Пользователь с id:%d не является хозяином вещи с id:%d.",
                            ownerId, item.getId())
            );
        }
    }
}
