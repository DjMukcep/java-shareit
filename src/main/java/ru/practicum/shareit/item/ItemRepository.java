package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    public Item addItem(Item item) {
        long id = getNextId();

        item.setId(id);
        items.put(id, item);

        return item;
    }

    public Item updateItem(Item item) {
        return items.put(item.getId(), item);
    }

    public Optional<Item> getItem(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    public List<Item> getItemsByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(ownerId))
                .toList();
    }

    public List<Item> searchItem(Long userId, String text) {
        return getItemsByOwnerId(userId).stream()
                .filter(item -> item.getName().equalsIgnoreCase(text))
                .filter(Item::isAvailable)
                .toList();
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
