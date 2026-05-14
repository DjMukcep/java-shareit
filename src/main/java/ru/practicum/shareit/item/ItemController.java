package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @RequestBody @Valid ItemDto item) {
        return ItemMapper.toItemDto(itemService.addItem(userId, item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody @Valid UpdateItem item) {
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, item));
    }

    @GetMapping("/{itemId}")
    public ItemWithComments getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        return itemService.getItemWithComments(userId, itemId);
    }

    @GetMapping
    public List<ItemWithComments> getItems(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        return itemService.getItemsWithComments(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByName(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestParam String text) {
        return ItemMapper.toItemDto(itemService.searchItem(userId, text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody @Valid NewComment comment) {
        return CommentMapper.toCommentDto(itemService.addComment(userId, itemId, comment));
    }
}
