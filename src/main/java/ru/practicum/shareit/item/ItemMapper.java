package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithComments;
import ru.practicum.shareit.item.dto.UpdateItem;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static List<ItemDto> toItemDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).toList();
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getIsAvailable());

        return item;
    }

    public static Item updateItemFields(Item item, UpdateItem request) {
        if (request.hasItemName()) {
            item.setName(request.getName());
        }

        if (request.hasItemDescription()) {
            item.setDescription(request.getDescription());
        }

        if (request.hasItemAvailable()) {
            item.setAvailable(request.getIsAvailable());
        }

        return item;
    }

    public static ItemWithComments toItemWithComments(
            Item item,
            LocalDateTime lastBooking,
            LocalDateTime nextBooking,
            List<String> comments) {
        return ItemWithComments.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.isAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}
