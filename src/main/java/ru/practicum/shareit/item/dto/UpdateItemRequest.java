package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateItemRequest {
    private String name;
    private String description;

    @JsonProperty("available")
    private Boolean isAvailable;

    public boolean hasItemName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasItemDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasItemAvailable() {
        return isAvailable != null;
    }
}
