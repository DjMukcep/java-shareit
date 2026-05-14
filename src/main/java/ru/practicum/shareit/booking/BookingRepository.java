package ru.practicum.shareit.booking;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @NonNull
    @EntityGraph(attributePaths = {"item", "booker"})
    Optional<Booking> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    Optional<Booking> findFullBookingInfoById(Long id);

    @EntityGraph(attributePaths = {"item"})
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long id, BookingStatus status);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long id, BookingStatus status);

    @EntityGraph(attributePaths = {"item"})
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime end);

    @EntityGraph(attributePaths = {"item"})
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long id, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long id, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"item"})
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime start);

    @EntityGraph(attributePaths = {"item"})
    List<Booking> findAllByBookerIdOrderByStartDesc(Long id);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long id);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerIdAndStatusAndStartBeforeOrderByStartDesc(
            Long id, BookingStatus status, LocalDateTime start);

    @EntityGraph(attributePaths = {"item", "item.owner", "booker"})
    List<Booking> findAllByItemOwnerIdAndStatusAndStartAfterOrderByStartAsc(
            Long id, BookingStatus status, LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
            Long itemId, BookingStatus status, LocalDateTime start);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, BookingStatus status, LocalDateTime start);

    boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);

    @Query("select count(b) > 0 from Booking b " +
            "where b.item.id = :itemId " +
            "and b.status = :status " +
            "and b.start < :end " +
            "and b.end > :start")
    boolean hasOverlappingBookings(Long itemId, BookingStatus status, LocalDateTime start, LocalDateTime end);
}
