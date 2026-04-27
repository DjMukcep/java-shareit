package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Поле name не может быть пустым.")
    private String name;

    @NotBlank(message = "Поле description не может быть пустым.")
    private String description;

    @NotNull(message = "Поле available должно быть инициализировано.")
    @JsonProperty("available")
    private Boolean isAvailable;
    private Long requestId;
}
