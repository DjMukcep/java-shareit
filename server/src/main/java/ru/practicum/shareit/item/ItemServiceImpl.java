package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemRequestService itemRequestService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;


    @Override
    @Transactional
    public ItemDto addItem(Long userId, NewItem newItem) {
        ItemRequest request = newItem.getRequestId() != null ?
               itemRequestService.getRequest(newItem.getRequestId()) : null;
        User owner = userService.getUser(userId);

        Item item = ItemMapper.toItem(newItem, request, owner);


        Item storedItem = itemRepository.save(item);
        log.info("Новая вещь: {} - владелец id: {}", storedItem, storedItem.getOwner().getId());

        return ItemMapper.toItemDto(storedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, UpdateItem request) {
        checkUserExists(userId);
        Item storedItem = getItem(itemId);

        checkUserIsOwner(userId, storedItem);
        Item updatedItem = ItemMapper.updateItemFields(storedItem, request);
        updatedItem = itemRepository.save(updatedItem);
        log.info("Вещь обновлена: {}", updatedItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    private void checkUserIsOwner(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException(
                    String.format("Пользователь id:%d не является владельцем вещи id:%d",
                            userId, item.getId())
            );
        }
    }

    @Override
    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id: " + itemId + " не найдена."));
    }

    @Override
    public List<Item> getItems(Long userId) { // возможно надо оставить только findByOwnerId
        return itemRepository.findByOwnerId(userId);
    }

    @Override
    public ItemWithComments getItemWithComments(Long userId, Long itemId) {
        LocalDateTime last = null;
        LocalDateTime next = null;

        Item item = getItem(itemId);

        List<CommentDto> comments = getComments(itemId);

        if (item.getOwner().getId().equals(userId)) {
            last = getLastBooking(itemId);
            next = getNextBooking(itemId);
        }

        return ItemMapper.toItemWithComments(item, last, next, comments);
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    private LocalDateTime getLastBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now())
                .map(Booking::getStart)
                .orElse(null);
    }

    private LocalDateTime getNextBooking(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                        itemId,
                        BookingStatus.APPROVED,
                        LocalDateTime.now())
                .map(Booking::getStart)
                .orElse(null);
    }

    @Override
    public List<ItemWithComments> getItemsWithComments(Long userId) {
        checkUserExists(userId);
        LocalDateTime now = LocalDateTime.now();

        Map<Long, Item> items = getItemsMap(userId);
        Map<Long, List<CommentDto>> comments = getCommentsMap(userId);
        Map<Long, LocalDateTime> lastDateBookings = getLastDateBookings(userId, now);
        Map<Long, LocalDateTime> nextBooking = getNextDateBookings(userId, now);

        return items.values().stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(item -> ItemMapper.toItemWithComments(
                        item,
                        lastDateBookings.get(item.getId()),
                        nextBooking.get(item.getId()),
                        comments.getOrDefault(item.getId(), Collections.emptyList())
                ))
                .toList();
    }

    private Map<Long, Item> getItemsMap(Long userId) {
        return getItems(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
    }

    private Map<Long, List<CommentDto>> getCommentsMap(Long userId) {
        return commentRepository.findAllByItemOwnerId(userId)
                .stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted(Comparator.comparing(Comment::getCreated))
                                        .map(CommentMapper::toCommentDto)
                                        .toList()
                                )
                        )
                );
    }

    private Map<Long, LocalDateTime> getLastDateBookings(Long userId, LocalDateTime time) {
        return bookingRepository.findAllByItemOwnerIdAndStatusAndStartBeforeOrderByStartDesc(
                        userId, BookingStatus.APPROVED, time)
                .stream()
                .collect(Collectors.toMap(booking ->
                                booking.getItem().getId(),
                        Booking::getStart,
                        ((existing, replacement) -> existing))
                );
    }

    private Map<Long, LocalDateTime> getNextDateBookings(Long userId, LocalDateTime time) {
        return bookingRepository.findAllByItemOwnerIdAndStatusAndStartAfterOrderByStartAsc(
                        userId, BookingStatus.APPROVED, time)
                .stream()
                .collect(Collectors.toMap(booking ->
                                booking.getItem().getId(),
                        Booking::getStart,
                        ((existing, replacement) -> existing))
                );
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        return ItemMapper.toItemDto(itemRepository.searchItem(userId, text));
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, NewComment newComment) {
        User booker = userService.getUser(userId);
        Item item = getItem(itemId);

        checkUserRightsForComment(userId, itemId);

        Comment comment = CommentMapper.toComment(booker, item, newComment);
        Comment storedComment = commentRepository.save(comment);
        log.info("Новый комментарий: {}", storedComment);

        return CommentMapper.toCommentDto(storedComment);
    }

    private void checkUserRightsForComment(Long userId, Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        boolean hasRight = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, now);

        if (!hasRight) {
            throw new ValidationException(
                    String.format("У Пользователя с id:%d нет прав на оставление комментария к вещи с id:%d.",
                            userId, itemId)
            );
        }
    }

    private void checkUserExists(Long userId) {
        userService.getUser(userId);
    }
}
