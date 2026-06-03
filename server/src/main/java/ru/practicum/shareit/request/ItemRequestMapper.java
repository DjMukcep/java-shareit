package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(NewItemRequest newItemRequest, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(newItemRequest.getDescription());
        itemRequest.setRequester(user);

        return itemRequest;
    }

    public static RequestDto toRequestDto(ItemRequest itemRequest) {
        return new RequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreatedAt()
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreatedAt(),
                items.stream()
                        .map(item -> new ItemRequestDto.Item(
                                item.getId(),
                                item.getName(),
                                item.getOwner().getId()))
                        .toList()
        );
    }

    public static List<ItemRequestDto> toItemRequestDto(List<ItemRequest> itemRequests, List<Item> items) {
        return itemRequests.stream()
                .map(itemRequest -> new ItemRequestDto(
                        itemRequest.getId(),
                        itemRequest.getDescription(),
                        itemRequest.getCreatedAt(),
                        items.stream()
                                .filter(item -> item.getRequest().getId().equals(itemRequest.getId()))
                                .map(item -> new ItemRequestDto.Item(
                                        item.getId(),
                                        item.getName(),
                                        item.getOwner().getId()))
                                .toList()))
                .toList();
    }
}
