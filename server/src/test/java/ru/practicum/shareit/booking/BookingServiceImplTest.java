package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional(readOnly = true)
@SpringBootTest
class BookingServiceImplTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingService bookingService;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    public void setUp() {
        owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@email");

        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@email");

        item = Item.builder()
                .name("item")
                .description("description")
                .isAvailable(true)
                .owner(owner)
                .request(null)
                .build();

        booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
        booking = bookingRepository.save(booking);
    }

    @Test
    @Transactional
    @DisplayName("Новый запрос на бронирование: успешное сохранение в базу.")
    void create() {
        NewBooking newBooking = new NewBooking(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );


        BookingDto bookingDto = bookingService.create(booker.getId(), newBooking);

        assertEquals(bookingDto.getItem().getId(), item.getId());
        assertEquals(bookingDto.getStart(), newBooking.getStart());
        assertEquals(bookingDto.getEnd(), newBooking.getEnd());
        assertEquals(booker.getId(), bookingDto.getBooker().getId());
        assertEquals("item", bookingDto.getItem().getName());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    @Transactional
    @DisplayName("Новый запрос на бронирование: вернуть 400 если вещь не доступна для бронирования.")
    void create_whenItemIsNotAvailable_thenThrowException() {
        item.setAvailable(false);
        NewBooking newBooking = new NewBooking(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        ValidationException exception = assertThrows(
                ValidationException.class, () -> bookingService.create(booker.getId(), newBooking));
        assertEquals(exception.getMessage(), String.format("Вещь id:%d недоступна для бронирования.", item.getId()));
    }

    @Test
    @Transactional
    @DisplayName("Новый запрос на бронирование: вернуть 400 если арендатор вещи является ее владельцем.")
    void create_whenItemBookerIsItemOwner_thenThrowException() {
        booker.setId(owner.getId());
        NewBooking newBooking = new NewBooking(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        ValidationException exception = assertThrows(
                ValidationException.class, () -> bookingService.create(booker.getId(), newBooking));
        assertEquals(exception.getMessage(), String.format("Арендатор этой вещи id:%d является ее владельцем id:%d",
                booking.getItem().getId(), booking.getBooker().getId()));
    }

    @Test
    @Transactional
    @DisplayName("Новый запрос на бронирование: вернуть 400 если время начало бронирования позже конца бронирования.")
    void create_whenStartBookingIsAfterEnd_thenThrowException() {
        NewBooking newBooking = new NewBooking(
                item.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        ValidationException exception = assertThrows(
                ValidationException.class, () -> bookingService.create(booker.getId(), newBooking));
        assertTrue(exception.getMessage().contains("Не верный промежуток времени начала"));
        assertTrue(exception.getMessage().contains("завершения бронирования"));
    }

    @Test
    @Transactional
    @DisplayName("Новый запрос на бронирование: вернуть 400 если этот промежуток " +
            "времени пересекается с другим одобренным бронированием.")
    void create_whenBookingTimeOverlappingWithOtherApprovedBooking_thenThrowException() {
        booking.setStatus(BookingStatus.APPROVED); // аренда от текущего времени + день до + 2 дня
        NewBooking newBooking = new NewBooking(
                item.getId(),
                LocalDateTime.now().plusDays(1).plusHours(12),
                LocalDateTime.now().plusDays(1).plusHours(20)
        );

        ValidationException exception = assertThrows(
                ValidationException.class, () -> bookingService.create(booker.getId(), newBooking));
        assertTrue(exception.getMessage().contains("Промежуток времени от"));
        assertTrue(exception.getMessage().contains("уже занят."));
    }

    @Test
    @Transactional
    @DisplayName("Подтвердить запрос на бронирование: успешное изменение данных.")
    void updateBooking() {
        BookingDto updatedBooking = bookingService.updateBooking(owner.getId(), booking.getId(), true);

        assertEquals(updatedBooking.getItem().getId(), item.getId());
        assertEquals(updatedBooking.getStart(), booking.getStart());
        assertEquals(updatedBooking.getEnd(), booking.getEnd());
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Test
    @Transactional
    @DisplayName("Обновить запрос на бронирование: вернуть 400 если запрос уже был до этого одобрен.")
    void updateBooking_whenStatusAlreadyIsApproved_thenThrowException() {
        booking.setStatus(BookingStatus.APPROVED);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(owner.getId(), booking.getId(), false));

        assertEquals(exception.getMessage(), String.format("Невозможно обновить бронирование, " +
                "так как статус бронирования был уже изменен на %S.", booking.getStatus()));
    }

    @Test
    @Transactional
    @DisplayName("Обновить запрос на бронирование: вернуть 404 бронирование не найдено.")
    void updateBooking_whenBookingNotFound_thenThrowException() {
        long wrongBookingId = 999L;

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(owner.getId(), wrongBookingId, true));

        assertEquals(exception.getMessage(), String.format("Бронирование с id:%d не найдено.", wrongBookingId));
    }

    @Test
    @Transactional
    @DisplayName("Обновить запрос на бронирование: вернуть 400 если вещь недоступна для бронирования.")
    void updateBooking_whenItemIsNotAvailable_thenThrowException() {
        item.setAvailable(false);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(owner.getId(), booking.getId(), true));

        assertEquals(exception.getMessage(), String.format("Вещь id:%d недоступна для бронирования.", item.getId()));
    }

    @Test
    @Transactional
    @DisplayName("Обновить запрос на бронирование: вернуть 400 если вещь недоступна для бронирования.")
    void updateBooking_whenUserIsNotOwner_thenThrowException() {

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(booker.getId(), booking.getId(), true));

        assertEquals(exception.getMessage(),
                String.format("Пользователь с id:%d не является хозяином вещи с id:%d.",
                        booker.getId(), item.getId()));
    }

    @Test
    @DisplayName("Получить данные о конкретном бронировании.")
    void getBooking() {
        BookingDto bookingDto = bookingService.getBooking(booking.getId(), owner.getId());

        assertEquals(bookingDto.getItem().getId(), item.getId());
        assertEquals(bookingDto.getStart(), booking.getStart());
        assertEquals(bookingDto.getEnd(), booking.getEnd());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
        assertEquals("item", bookingDto.getItem().getName());
    }

    @Test
    @DisplayName("Получить данные бронировании: 400 пользователь не владелец вещи и не ее арендатор.")
    void getBooking_whenUserIdIsNotOwnerOrBooker_thenThrowException() {
        long wrongUserId = 999L;

        ValidationException exception = assertThrows(
                ValidationException.class, () -> bookingService.getBooking(booking.getId(), wrongUserId));

        assertEquals(exception.getMessage(), String.format(
                "У пользователя id:%d нет прав на получение данных о бронировании.", wrongUserId
        ));
    }

    @Test
    @DisplayName("Получить данные бронировании: 404 не валидный bookingId.")
    void getBooking_whenBookingNotFound_thenThrowException() {
        long wrongBookingId = 999L;

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> bookingService.getBooking(wrongBookingId, owner.getId()));

        assertEquals(exception.getMessage(), String.format("Бронирование с id:%d не найдено.", wrongBookingId));
    }

    @Test
    @DisplayName("Получить список всех бронирований текущего пользователя.")
    void getBookerBookings() {
        List<BookingDto> bookingsAll = bookingService.getBookerBookings(booker.getId(), "ALL");
        assertNotNull(bookingsAll);
        assertEquals(1, bookingsAll.size());
        assertEquals(booking.getId(), bookingsAll.getFirst().getId());

        List<BookingDto> bookingsWaiting = bookingService.getBookerBookings(booker.getId(), "WAITING");
        assertEquals(1, bookingsWaiting.size());

        List<BookingDto> bookingsPast = bookingService.getBookerBookings(booker.getId(), "PAST");
        assertTrue(bookingsPast.isEmpty());

        List<BookingDto> bookingsFuture = bookingService.getBookerBookings(booker.getId(), "FUTURE");
        assertEquals(1, bookingsFuture.size());
        assertEquals(booking.getId(), bookingsFuture.getFirst().getId());

        List<BookingDto> bookingsCurrent = bookingService.getBookerBookings(booker.getId(), "CURRENT");
        assertTrue(bookingsCurrent.isEmpty());

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.getBookerBookings(booker.getId(), "UNSUPPORTED_STATUS"));

        assertEquals("Статус не поддерживается: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    @DisplayName("Получить список бронирований для всех вещей владельца.")
    void getOwnerBookings() {
        List<BookingDto> bookingsAll = bookingService.getOwnerBookings(owner.getId(), "ALL");
        assertNotNull(bookingsAll);
        assertEquals(1, bookingsAll.size());
        assertEquals(booking.getId(), bookingsAll.getFirst().getId());

        List<BookingDto> bookingsWaiting = bookingService.getOwnerBookings(owner.getId(), "WAITING");
        assertEquals(1, bookingsWaiting.size());

        List<BookingDto> bookingsRejected = bookingService.getOwnerBookings(owner.getId(), "REJECTED");
        assertTrue(bookingsRejected.isEmpty());

        List<BookingDto> bookingsCurrent = bookingService.getOwnerBookings(owner.getId(), "CURRENT");
        assertTrue(bookingsCurrent.isEmpty());

        List<BookingDto> bookingsPast = bookingService.getOwnerBookings(owner.getId(), "PAST");
        assertTrue(bookingsPast.isEmpty());

        List<BookingDto> bookingsFuture = bookingService.getOwnerBookings(owner.getId(), "FUTURE");
        assertEquals(1, bookingsFuture.size());

        List<BookingDto> bookingsNoItems = bookingService.getOwnerBookings(booker.getId(), "ALL");
        assertTrue(bookingsNoItems.isEmpty());

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.getOwnerBookings(owner.getId(), "UNSUPPORTED_STATUS"));

        assertEquals("Статус не поддерживается: UNSUPPORTED_STATUS", exception.getMessage());
    }
}