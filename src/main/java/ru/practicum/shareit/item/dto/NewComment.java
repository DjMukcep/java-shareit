package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewComment {

    @JsonProperty("text")
    @NotBlank(message = "Поле комментария не может быть пустым.")
    String comment;
}
