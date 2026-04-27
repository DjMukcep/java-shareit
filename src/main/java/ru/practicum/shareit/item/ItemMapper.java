package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static Item mapToItem(ItemDto itemDto) {
        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getIsAvailable());

        return item;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
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

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).toList();
    }
}
