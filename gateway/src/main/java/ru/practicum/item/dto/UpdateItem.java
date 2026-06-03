package ru.practicum.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItem {
    private String name;
    private String description;

    @JsonProperty("available")
    private Boolean isAvailable;
}
