package ru.practicum.shareit.booking;

/**
 * Status — статус бронирования. Может принимать одно из следующих
 * значений: WAITING — новое бронирование, ожидает одобрения, APPROVED
 * бронирование подтверждено владельцем, REJECTED — бронирование
 * отклонено владельцем, CANCELED — бронирование отменено создателем.
 */
public enum BookingStatus {
    WAITING, APPROVED, REJECTED, CANCELED
}
