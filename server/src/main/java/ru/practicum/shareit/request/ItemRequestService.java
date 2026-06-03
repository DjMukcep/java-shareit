package ru.practicum.shareit.request;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {

    RequestDto addRequest(NewItemRequest newRequest, Long userId);

    ItemRequestDto getRequest(Long userId, Long requestId);

    ItemRequest getRequest(Long requestId);

    List<RequestDto> getRequests(Long userId);

    List<ItemRequestDto> getUserRequests(Long userId);
}
