package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewItem {

    private String name;
    private String description;

    @JsonProperty("available")
    private Boolean isAvailable;
    private Long requestId;
}
