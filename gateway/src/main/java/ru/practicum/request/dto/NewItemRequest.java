package ru.practicum.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewItemRequest {

    @NotBlank(message = "Поле описания запроса не может быть пустым.")
    private String description;
}
