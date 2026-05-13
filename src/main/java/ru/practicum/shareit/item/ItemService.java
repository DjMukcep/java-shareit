package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithComments;
import ru.practicum.shareit.item.dto.NewComment;
import ru.practicum.shareit.item.dto.UpdateItem;

import java.util.List;

public interface ItemService {

    Item addItem(Long userId, ItemDto itemDto);

    Item updateItem(Long userId, Long itemId, UpdateItem request);

    Item getItem(Long itemId);

    List<Item> getItems(Long userId);

    List<Item> searchItem(Long userId, String text);

    Comment addComment(Long userId, Long itemId, NewComment comment);

    ItemWithComments getItemWithComments(Long userId, Long itemId);

    List<ItemWithComments> getItemsWithComments(Long userId);
}
