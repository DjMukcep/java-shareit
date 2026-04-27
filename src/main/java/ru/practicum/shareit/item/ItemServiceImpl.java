package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;


    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.mapToItem(itemDto);

        item.setOwner(userService.getUser(userId));

        Item storedItem = itemRepository.addItem(item);
        log.info("Новая вещь: {}", storedItem);

        return ItemMapper.toItemDto(storedItem);
    }

    public ItemDto updateItem(Long userId, Long itemId, UpdateItemRequest request) {

        Item storedItem = itemRepository.getItem(itemId)
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId
                        + ", где владелец id: " + userId + " не найдена."));

        Item updatedItem = ItemMapper.updateItemFields(storedItem, request);
        updatedItem = itemRepository.updateItem(updatedItem);
        log.info("Вещь обновлена: {}", updatedItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    public ItemDto getItem(Long userId, Long itemId) {
        return itemRepository.getItem(itemId)
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId
                        + ", где владелец id: " + userId + " не найдена."));
    }

    public List<ItemDto> getItems(Long userId) {
        return userId == null ? ItemMapper.toItemDtoList(itemRepository.getItems())
                : ItemMapper.toItemDtoList(itemRepository.getItemsByOwnerId(userId));
    }

    public List<ItemDto> searchItem(Long userId, String text) {
        return ItemMapper.toItemDtoList(itemRepository.searchItem(userId, text));

    }
}
