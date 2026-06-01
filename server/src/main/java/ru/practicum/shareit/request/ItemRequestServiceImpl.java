package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public RequestDto addRequest(NewItemRequest newRequest, Long userId) {
        User requester = userService.getUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(newRequest, requester);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        log.info("Новый запрос на вещь {}", savedItemRequest);

        return ItemRequestMapper.toRequestDto(savedItemRequest);
    }

    @Override
    public ItemRequestDto getRequest(Long userId, Long requestId) {
        checkUserExists(userId);
        ItemRequest itemRequest = getRequest(requestId);
        List<Item> items = itemRepository.findAllByRequestId(requestId);

        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Override
    public ItemRequest getRequest(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Запрос вещи с id: %d не найден.", requestId))
        );
    }

    @Override
    public List<RequestDto> getRequests(Long userId) {
        checkUserExists(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAll();

        return itemRequests.stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                .map(itemRequest -> new RequestDto(
                        itemRequest.getId(),
                        itemRequest.getDescription(),
                        itemRequest.getCreatedAt()))
                .sorted(Comparator.comparing(RequestDto::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedAtDesc(userId);
        List<Item> items = itemRepository.findAllByRequestRequesterId(userId);
        return ItemRequestMapper.toItemRequestDto(requests, items);
    }

    private void checkUserExists(Long userId) {
        userService.getUser(userId);
    }
}
