package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, NewItem newItem);

    ItemDto updateItem(Long userId, Long itemId, UpdateItem request);

    Item getItem(Long itemId);

    List<Item> getItems(Long userId);

    List<ItemDto> searchItem(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, NewComment comment);

    ItemWithComments getItemWithComments(Long userId, Long itemId);

    List<ItemWithComments> getItemsWithComments(Long userId);
}
