package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User owner;
    private User booker;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("item-owner");
        owner.setEmail("owner@mail.com");
        entityManager.persist(owner);

        booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@mail.com");
        entityManager.persist(booker);

        item = Item.builder()
                .name("some-item")
                .description("Мощная дрель")
                .isAvailable(true)
                .owner(owner)
                .build();
        entityManager.persist(item);

        booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        entityManager.persist(booking);

        entityManager.flush();
    }

    @Test
    @DisplayName("findFullBookingInfoById: успешное извлечение бронирования со всеми связями")
    void findFullBookingInfoById() {
        Optional<Booking> foundBooking = bookingRepository.findFullBookingInfoById(booking.getId());

        assertTrue(foundBooking.isPresent());
        assertEquals(booking.getId(), foundBooking.get().getId());
        assertEquals("some-item", foundBooking.get().getItem().getName());
        assertEquals("item-owner", foundBooking.get().getItem().getOwner().getName());
        assertEquals("booker", foundBooking.get().getBooker().getName());
    }

    @Test
    @DisplayName("findAllByBookerIdAndStatusOrderByStartDesc: поиск по арендатору и статусу")
    void findAllByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> result = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.WAITING);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("hasOverlappingBookings: проверка пересечения интервалов времени (Кастомный @Query)")
    void hasOverlappingBookings() {
        boolean hasOverlapInside = bookingRepository.hasOverlappingBookings(
                item.getId(),
                BookingStatus.WAITING,
                LocalDateTime.now().plusDays(1).plusHours(2),
                LocalDateTime.now().plusDays(1).plusHours(5)
        );

        boolean noOverlap = bookingRepository.hasOverlappingBookings(
                item.getId(),
                BookingStatus.WAITING,
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4)
        );

        assertTrue(hasOverlapInside, "Должно обнаружить пересечение дат");
        assertFalse(noOverlap, "Не должно обнаружить пересечение дат");
    }

    @Test
    @DisplayName("existsByBookerIdAndItemIdAndStatusAndEndBefore: проверка завершенного бронирования вещи для отзыва")
    void existsByBookerIdAndItemIdAndStatusAndEndBefore() {
        boolean existsInFuture = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                booker.getId(), item.getId(), BookingStatus.WAITING, LocalDateTime.now());

        Booking pastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(pastBooking);
        entityManager.flush();

        boolean existsInPast = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                booker.getId(), item.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertFalse(existsInFuture);
        assertTrue(existsInPast, "Должно подтвердить, что пользователь уже брал эту вещь ранее");
    }

    @Test
    @DisplayName("findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc: поиск последнего бронирования")
    void findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc() {
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusHours(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
        entityManager.persist(lastBooking);
        entityManager.flush();

        Optional<Booking> found = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                item.getId(), BookingStatus.APPROVED, LocalDateTime.now());

        assertTrue(found.isPresent());
        assertEquals(lastBooking.getId(), found.get().getId());
    }
}