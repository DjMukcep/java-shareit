package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static List<ItemDto> toItemDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).toList();
    }

    public static Item toItem(NewItem newItem, ItemRequest request, User owner) {
        Item item = new Item();

        item.setName(newItem.getName());
        item.setDescription(newItem.getDescription());
        item.setAvailable(newItem.getIsAvailable());
        item.setOwner(owner);
        item.setRequest(request);

        return item;
    }

    public static Item updateItemFields(Item item, UpdateItem request) {
        item.setName(request.hasItemName() ? request.getName() : null);
        item.setDescription(request.hasItemDescription() ? request.getDescription() : null);
        item.setAvailable(request.hasItemAvailable() ? request.getIsAvailable() : false);

        return item;
    }

    public static ItemWithComments toItemWithComments(
            Item item,
            LocalDateTime lastBooking,
            LocalDateTime nextBooking,
            List<CommentDto> comments) {
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
