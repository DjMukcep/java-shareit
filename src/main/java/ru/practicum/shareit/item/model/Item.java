package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    @JsonProperty("available")
    private boolean isAvailable;
    private User owner;
    private ItemRequest request;
}
