package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotBlank(message = "Имя пользователя должно быть указано.")
    String name;
    @Email(message = "Некорректный адрес электронной почты.")
    @NotBlank(message = "Имейл должен быть указан.")
    String email;
}
