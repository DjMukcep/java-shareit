package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBooking {
    @NotNull
    private Long itemId;

    @NotNull
    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    private LocalDateTime start;

    @NotNull
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDateTime end;
}
