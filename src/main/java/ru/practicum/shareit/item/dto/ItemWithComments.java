package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemWithComments {
    private Long id;
    private String name;
    private String description;

    @JsonProperty("available")
    private boolean isAvailable;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<String> comments;
}
